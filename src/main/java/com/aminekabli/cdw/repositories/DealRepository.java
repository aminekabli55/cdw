package com.aminekabli.cdw.repositories;


 import com.aminekabli.cdw.model.Deal;
 import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Long> {
    Optional<Deal> findByDealId(String dealId);
}