package com.aminekabli.cdw;

import com.aminekabli.cdw.service.CsvDealImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.InputStream;




@SpringBootApplication
public class CdwApplication {
    private static final Logger logger = LoggerFactory.getLogger(CdwApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CdwApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CsvDealImporter importer) {
        return args -> {
            logger.info("=".repeat(80));
            logger.info("CDW - Clustered Data Warehouse - FX Deals Importer");
            logger.info("=".repeat(80));

            String csvPath = args.length > 0 ? args[0] : "./sample_deals.csv";
            logger.info("Looking for CSV file: {}", csvPath);

            try (InputStream is = new FileInputStream(csvPath)) {
                logger.info("Starting import from: {}", csvPath);
                var result = importer.importFromStream(is);

                logger.info("=".repeat(80));
                logger.info("IMPORT SUMMARY");
                logger.info("-".repeat(80));
                logger.info("Total rows processed : {}", result.processed);
                logger.info("Successfully saved   : {}", result.saved);
                logger.info("Failed/Duplicates    : {}", result.processed - result.saved);
                logger.info("=".repeat(80));

                if (!result.errors.isEmpty()) {
                    logger.warn("ERRORS DETAILS:");
                    result.errors.forEach(logger::warn);
                    logger.info("=".repeat(80));
                }

                if (result.saved > 0) {
                    logger.info("Import completed successfully!");
                } else {
                    logger.warn("No deals were saved. Check errors above.");
                }

                 logger.info("Application will stay running. Press Ctrl+C to stop.");
                Thread.currentThread().join();

            } catch (Exception e) {
                logger.error("=".repeat(80));
                logger.error("✗ IMPORT FAILED");
                logger.error("Error: {}", e.getMessage(), e);
                logger.error("=".repeat(80));
                throw e;
            }
        };
    }
}