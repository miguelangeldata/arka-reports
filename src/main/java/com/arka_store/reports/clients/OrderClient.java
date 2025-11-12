package com.arka_store.reports.clients;


import com.arka_store.reports.models.ComprehensiveOrderMetrics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ORDERS-SERVICE",path = "orders")
public interface OrderClient {
    @GetMapping("/metrics")
    ComprehensiveOrderMetrics getOrderMetrics();


}
