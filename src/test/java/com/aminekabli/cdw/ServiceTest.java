package com.aminekabli.cdw;


import com.aminekabli.cdw.model.Deal;
import com.aminekabli.cdw.repositories.DealRepository;
import com.aminekabli.cdw.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealServiceTest {

    private DealRepository repo;
    private DealService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(DealRepository.class);
        service = new DealService(repo);
    }

    @Test
    void saveDeal_shouldSaveIfNotExists() {
        Deal deal = new Deal("D001", "USD", "EUR", OffsetDateTime.now(), BigDecimal.TEN);
        when(repo.findByDealId("D001")).thenReturn(Optional.empty());
        when(repo.save(deal)).thenReturn(deal);

        boolean result = service.saveDeal(deal);

        assertTrue(result);
        verify(repo, times(1)).save(deal);
    }

    @Test
    void saveDeal_shouldSkipIfDuplicate() {
        Deal deal = new Deal("D001", "USD", "EUR", OffsetDateTime.now(), BigDecimal.TEN);
        when(repo.findByDealId("D001")).thenReturn(Optional.of(deal));

        boolean result = service.saveDeal(deal);

        assertFalse(result);
        verify(repo, never()).save(any());
    }
}
