package com.aminekabli.cdw;


import com.aminekabli.cdw.model.Deal;
import com.aminekabli.cdw.service.CsvDealImporter;
import com.aminekabli.cdw.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvDealImporterTest {

    private DealService dealService;
    private CsvDealImporter importer;

    @BeforeEach
    void setUp() {
        dealService = Mockito.mock(DealService.class);
        importer = new CsvDealImporter(dealService);
    }

    @Test
    void importFromStream_shouldSaveValidRows() throws Exception {
        String csv = "dealId,orderingCurrency,toCurrency,dealTimestamp,amount\n" +
                "D001,USD,EUR,2024-01-15T10:30:00Z,1000.50\n" +
                "D002,GBP,USD,2024-01-15T11:45:00Z,2500.75\n";

        when(dealService.saveDeal(any(Deal.class))).thenReturn(true);

        CsvDealImporter.ImportResult result = importer.importFromStream(
                new ByteArrayInputStream(csv.getBytes())
        );

        assertEquals(2, result.processed);
        assertEquals(2, result.saved);
        assertTrue(result.errors.isEmpty());
    }

    @Test
    void importFromStream_shouldSkipInvalidRows() throws Exception {
        String csv = "dealId,orderingCurrency,toCurrency,dealTimestamp,amount\n" +
                "D001,USD,EUR,2024-01-15T10:30:00Z,1000.50\n" +
                "D002,GBP,USD,,2500.75\n"; // missing timestamp

        when(dealService.saveDeal(any(Deal.class))).thenReturn(true);

        CsvDealImporter.ImportResult result = importer.importFromStream(
                new ByteArrayInputStream(csv.getBytes())
        );

        assertEquals(2, result.processed);
        assertEquals(1, result.saved);
        assertEquals(1, result.errors.size());
    }
}
