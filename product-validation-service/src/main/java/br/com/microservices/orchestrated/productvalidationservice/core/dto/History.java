package br.com.microservices.orchestrated.productvalidationservice.core.dto;

import br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class History {

    private String source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
