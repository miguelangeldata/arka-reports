package com.arka_store.reports.controller;

import com.arka_store.reports.etl.FetchData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fetch")
@RequiredArgsConstructor
public class FetchDataController {
    private final FetchData fetchData;

    @PostMapping
    public ResponseEntity<String> fetchData(){
        fetchData.fetchData();
        return ResponseEntity.ok("Data was fetching successfully");
    }
}
