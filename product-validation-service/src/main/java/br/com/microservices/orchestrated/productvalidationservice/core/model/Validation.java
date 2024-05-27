package br.com.microservices.orchestrated.productvalidationservice.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "validation")
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private boolean success;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist //Fazer uma ação antes de salvar no banco de dados
    public void prePersist() {
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate //Fazer uma ação antes de atualizar no banco de dados
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
