package com.logistics.delivery_optimizer.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String address;
    private Double latitude;
    private Double longitude;

    @Column(name = "sla_minutes", nullable = false)
    private Integer slaMinutes;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Enum for the status field
    public enum Status {
        PENDING, ASSIGNED, DELIVERED
    }

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
}