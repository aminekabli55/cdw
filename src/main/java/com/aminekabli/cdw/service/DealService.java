package com.aminekabli.cdw.service;

  import com.aminekabli.cdw.model.Deal;
  import com.aminekabli.cdw.repositories.DealRepository;
  import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DealService {
    private final DealRepository repo;
    private final Logger logger = LoggerFactory.getLogger(DealService.class);

    public DealService(DealRepository repo) {
        this.repo = repo;
    }


    public boolean saveDeal(Deal deal) {
        try {
            Optional<Deal> existing = repo.findByDealId(deal.getDealId());
            if (existing.isPresent()) {
                logger.info("Duplicate deal skipped: {}", deal.getDealId());
                return false;
            }
            repo.save(deal);
            logger.info("Saved deal {}", deal.getDealId());
            return true;
        } catch (DataIntegrityViolationException ex) {
             logger.warn("DB constraint violation for deal {}: {}", deal.getDealId(), ex.getMessage());
            return false;
        } catch (Exception ex) {
            logger.error("Unexpected error saving deal {}: {}", deal.getDealId(), ex.getMessage(), ex);
            return false;
        }
    }
}
