package com.arka_store.reports.clients;
import com.arka_store.reports.models.MetricsOfPayments;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "PAYMENTS-SERVICE",path = "payments")
public interface PaymentClient {
    @GetMapping("/metrics")
    MetricsOfPayments getMetricsOfPayments();
}
