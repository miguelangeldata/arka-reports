package com.arka_store.reports.controller;

import com.arka_store.reports.resources.EmailToSend;
import com.arka_store.reports.resources.ReportResponse;
import com.arka_store.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor

public class CreateReportController {
    private final ReportService service;

    @PostMapping
    public ResponseEntity<ReportResponse>createReport(@RequestBody EmailToSend emailToSend) throws IOException {
        ReportResponse response=service.createReport(emailToSend.email());
        return ResponseEntity.ok(response);
    }
}
