package com.arka_store.reports.clients;

import com.arka_store.reports.models.MetricsOfProducts;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCTS-SERVICE",path = "products")
public interface ProductClient {
    @GetMapping("/metrics")
    MetricsOfProducts getMetricsOfProducts();
    @GetMapping("/info/{productId}")
    String getProductInfo(@PathVariable("productId")Long productId);
}
