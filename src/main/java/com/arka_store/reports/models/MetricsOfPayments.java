package com.arka_store.reports.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "payments_metrics")
public class MetricsOfPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime saveAt=LocalDateTime.now();
    private Integer total;
    private Integer totalAccepted;
    private Integer totalCanceled;
    private Integer totalDeclined;
    private Integer totalPaymentByCreditCard;
    private Integer totalPaymentByPaypal;
    private Integer totalByPse;
    private Double max;
    private Double min;
    private Double sum;
    private Double avg;

    public MetricsOfPayments(Integer total, Integer totalAccepted, Integer totalCanceled, Integer totalDeclined, Integer totalPaymentByCreditCard, Integer totalPaymentByPaypal,
                             Integer totalByPse, Double max, Double min, Double sum, Double avg) {
        this.total = total;
        this.totalAccepted = totalAccepted;
        this.totalCanceled = totalCanceled;
        this.totalDeclined = totalDeclined;
        this.totalPaymentByCreditCard = totalPaymentByCreditCard;
        this.totalPaymentByPaypal = totalPaymentByPaypal;
        this.totalByPse = totalByPse;
        this.max = max;
        this.min = min;
        this.sum = sum;
        this.avg = avg;
    }
}