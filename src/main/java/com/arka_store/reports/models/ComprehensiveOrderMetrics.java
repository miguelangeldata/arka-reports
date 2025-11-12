package com.arka_store.reports.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_metrics")
public class ComprehensiveOrderMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime saveAt=LocalDateTime.now();
    private Long totalOrders;
    private Long acceptedOrders;
    private Long pendingOrders;
    private Double totalSalesAmount;
    private Double averageOrderValue;
    @Embedded
    private BestSellerMetrics bestsellers;
}