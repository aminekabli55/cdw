package com.aminekabli.cdw.model;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "deals", uniqueConstraints = @UniqueConstraint(columnNames = {"deal_id"}))
 public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="deal_id", nullable=false, unique=true)
    private String dealId;

    @Column(name="ordering_currency", length = 3, nullable=false)
    private String orderingCurrency;

    @Column(name="to_currency", length = 3, nullable=false)
    private String toCurrency;

    @Column(name="deal_timestamp", nullable=false)
    private OffsetDateTime dealTimestamp;

    @Column(name="amount", nullable=false)
    private BigDecimal amount;

    public Deal() {}
    public Deal(String dealId, String orderingCurrency, String toCurrency, OffsetDateTime dealTimestamp, BigDecimal amount) {
        this.dealId = dealId;
        this.orderingCurrency = orderingCurrency;
        this.toCurrency = toCurrency;
        this.dealTimestamp = dealTimestamp;
        this.amount = amount;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getOrderingCurrency() {
        return orderingCurrency;
    }

    public void setOrderingCurrency(String orderingCurrency) {
        this.orderingCurrency = orderingCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public OffsetDateTime getDealTimestamp() {
        return dealTimestamp;
    }

    public void setDealTimestamp(OffsetDateTime dealTimestamp) {
        this.dealTimestamp = dealTimestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

