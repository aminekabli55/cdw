package com.aminekabli.cdw.model;


import com.aminekabli.cdw.service.CsvDealImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DealWebController {
    private final CsvDealImporter importer;
    private final Logger logger = LoggerFactory.getLogger(DealWebController.class);

    public DealWebController(CsvDealImporter importer) {
        this.importer = importer;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {

        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a CSV file to upload");
            return "index";
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            model.addAttribute("error", "Only CSV files are allowed");
            return "index";
        }

        try {
            logger.info("Processing file: {}", file.getOriginalFilename());
            var result = importer.importFromStream(file.getInputStream());

            model.addAttribute("success", true);
            model.addAttribute("filename", file.getOriginalFilename());
            model.addAttribute("processed", result.processed);
            model.addAttribute("saved", result.saved);
            model.addAttribute("failed", result.processed - result.saved);
            model.addAttribute("errors", result.errors);

            logger.info("Import completed: processed={}, saved={}, failed={}",
                    result.processed, result.saved, result.processed - result.saved);

        } catch (IOException e) {
            logger.error("Error processing file: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to process file: " + e.getMessage());
        }

        return "index";
    }
}