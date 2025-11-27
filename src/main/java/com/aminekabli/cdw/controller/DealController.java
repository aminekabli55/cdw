package com.aminekabli.cdw.controller;


import com.aminekabli.cdw.service.CsvDealImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/deals")
public class DealController {
    private final CsvDealImporter importer;
    private final Logger logger = LoggerFactory.getLogger(DealController.class);

    public DealController(CsvDealImporter importer) {
        this.importer = importer;
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importDeals(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only CSV files are allowed"));
        }

        try {
            var result = importer.importFromStream(file.getInputStream());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "completed");
            response.put("processed", result.processed);
            response.put("saved", result.saved);
            response.put("failed", result.processed - result.saved);
            response.put("errors", result.errors);

            logger.info("Import completed: processed={}, saved={}", result.processed, result.saved);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Error importing file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}