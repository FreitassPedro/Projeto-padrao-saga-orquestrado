package br.com.microservices.orchestrated.paymentservice.core.service;

import br.com.microservices.orchestrated.paymentservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.paymentservice.core.dto.Event;
import br.com.microservices.orchestrated.paymentservice.core.dto.History;
import br.com.microservices.orchestrated.paymentservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import br.com.microservices.orchestrated.paymentservice.core.model.Payment;
import br.com.microservices.orchestrated.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.core.repository.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.paymentservice.core.enums.ESagaStatus.SUCCESS;


@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";
    private static final Double MIN_AMOUNT_VALUE = 0.1;


    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final PaymentRepository paymentRepository;

    public void realizePayment(Event event) {
        try {
            checkCurrentValidation(event);
            createPendingPayment(event);
            var payment = findByOrderIdAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            changePaymentStatusToSuccess(payment);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error when trying to make payment");
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }


    private void checkCurrentValidation(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("Another transaction already exists for this validation.");
        }
    }

    private void createPendingPayment(Event event) {
        var totalAmount = calculateAmount(event);
        var totalItems = calculateTotalItems(event);

        var payment = Payment.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getPayload().getTransactionId())
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
        save(payment);
        setEventAmountItems(event, payment);
    }
    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "payment realized successfully!");
    }
    private void addHistory(Event event, String message) {
        var history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private double calculateAmount(Event event) {
        return event
                .getPayload().getProducts()
                .stream().map(
                        product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(0.0, Double::sum);
    }

    private int calculateTotalItems(Event event) {
        return event
                .getPayload().getProducts()
                .stream()
                .map(OrderProducts::getQuantity)
                .reduce(0, Integer::sum);
    }

    private void setEventAmountItems(Event event, Payment payment) {
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }

    private void save(Payment payment) {
        paymentRepository.save(payment);
    }
    private Payment findByOrderIdAndTransactionId(Event event) {
        return paymentRepository.findByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
                .orElseThrow(() -> new ValidationException("Payment not found by OrderID and TransactionID"));
    }
    private void validateAmount(double amount) {
        if (amount < 0.1) {
            throw new ValidationException("The minimum amount is ".concat(MIN_AMOUNT_VALUE.toString()));
        }
    }

    private void changePaymentStatusToSuccess(Payment payment) {
        payment.setStatus(EPaymentStatus.SUCCESS);
        save(payment);
    }
}
