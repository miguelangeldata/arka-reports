package com.arka_store.reports.clients;

import com.arka_store.reports.models.MetricsForShipping;
import com.arka_store.reports.models.MetricsOfProducts;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "SHIPPING-SERVICE",path = "shipping")
public interface ShippingClient {
    @GetMapping("/metrics")
    MetricsForShipping getMetricsOfShipping();
}
