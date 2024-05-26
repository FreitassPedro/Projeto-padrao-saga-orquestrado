package br.com.microservices.orchestrated.paymentservice.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationExpcetion(ValidationException validationException) {
        //Criei essas variaveis apenas para melhorar a minha leitura do código
        int status = HttpStatus.BAD_REQUEST.value();
        String errorMessage = validationException.getMessage();

        var details = new ExceptionDetails(status, errorMessage);
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}
