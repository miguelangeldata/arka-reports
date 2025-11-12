package com.arka_store.reports.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shipping_metrics")
public class MetricsForShipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime saveAt=LocalDateTime.now();
    private Integer totalOfShipping;
    private Integer totalSend;
    private Integer totalReceived;
    private Integer totalReturned;
}
