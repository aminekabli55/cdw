package com.aminekabli.cdw.service;


 import com.aminekabli.cdw.model.Deal;
 import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvDealImporter {
    private final DealService dealService;
    private final Logger logger = LoggerFactory.getLogger(CsvDealImporter.class);

    public CsvDealImporter(DealService dealService) {
        this.dealService = dealService;
    }

    public ImportResult importFromStream(InputStream is) throws IOException {
        List<String> errors = new ArrayList<>();
        int processed = 0;
        int saved = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withHeader("dealId","orderingCurrency","toCurrency","dealTimestamp","amount")
                     .withSkipHeaderRecord(true)
                     .withTrim())) {

            for (CSVRecord rec : parser) {
                processed++;
                String dealId = rec.get("dealId");
                String orderingCurrency = rec.get("orderingCurrency");
                String toCurrency = rec.get("toCurrency");
                String ts = rec.get("dealTimestamp");
                String amountStr = rec.get("amount");

                // validation
                String validation = validateRow(dealId, orderingCurrency, toCurrency, ts, amountStr);
                if (validation != null) {
                    String msg = "Line " + processed + " invalid: " + validation;
                    logger.warn(msg);
                    errors.add(msg);
                    continue;
                }

                try {
                    OffsetDateTime odt = OffsetDateTime.parse(ts);
                    BigDecimal amount = new BigDecimal(amountStr);
                    Deal deal = new Deal(dealId, orderingCurrency.toUpperCase(), toCurrency.toUpperCase(), odt, amount);
                    boolean savedOk = dealService.saveDeal(deal);
                    if (savedOk) saved++;
                } catch (DateTimeParseException | NumberFormatException ex) {
                    String msg = "Line " + processed + " parse error: " + ex.getMessage();
                    logger.warn(msg);
                    errors.add(msg);
                } catch (Exception ex) {
                    String msg = "Line " + processed + " unexpected: " + ex.getMessage();
                    logger.error(msg, ex);
                    errors.add(msg);
                }
            }
        }
        return new ImportResult(processed, saved, errors);
    }

    private String validateRow(String dealId, String orderingCurrency, String toCurrency, String ts, String amountStr) {
        if (dealId == null || dealId.isBlank()) return "dealId missing";
        if (orderingCurrency == null || orderingCurrency.length() != 3) return "orderingCurrency invalid";
        if (toCurrency == null || toCurrency.length() != 3) return "toCurrency invalid";
        if (ts == null || ts.isBlank()) return "dealTimestamp missing";
        if (amountStr == null || amountStr.isBlank()) return "amount missing";
        try {
            new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return "amount not a valid decimal";
        }
        return null;
    }

    public static class ImportResult {
        public final int processed;
        public final int saved;
        public final List<String> errors;
        public ImportResult(int processed, int saved, List<String> errors) {
            this.processed = processed;
            this.saved = saved;
            this.errors = errors;
        }
    }
}
