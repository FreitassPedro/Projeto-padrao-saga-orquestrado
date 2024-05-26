package br.com.microservices.orchestrated.orderservice.core.document;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {
    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private String source;
    private String status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
}
