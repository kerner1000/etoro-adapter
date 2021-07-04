package com.github.kerner1000.etoro.stats.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements InstrumentSupplier, AmountSupplier {

    private BigDecimal positionId;

    private BigDecimal amount;

    private BigDecimal accountBalanceAfter;

    private LocalDateTime dateTime;

    private TransactionType type;

    private String instrument;

    public Transaction(BigDecimal positionId, BigDecimal amount, LocalDateTime dateTime, TransactionType type, String instrument, BigDecimal accountBalanceAfter) {
        this.positionId = Objects.requireNonNull(positionId);
        this.amount = Objects.requireNonNull(amount);
        this.dateTime = Objects.requireNonNull(dateTime);
        this.type = Objects.requireNonNull(type);
        this.instrument = Objects.requireNonNull(instrument).replaceAll("\\s+", "");
        this.accountBalanceAfter = accountBalanceAfter;
    }

    public Transaction(BigDecimal positionId, BigDecimal amount, LocalDateTime dateTime, TransactionType type, String instrument) {
        this(positionId, amount, dateTime, type, instrument, null);
    }

    /**
     * Deserialization constructor.
     */
    public Transaction() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(positionId, that.positionId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(dateTime, that.dateTime) &&
                type == that.type &&
                Objects.equals(instrument, that.instrument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionId, amount, dateTime, type, instrument);
    }

    // Getter / Setter //


    public BigDecimal getPositionId() {
        return positionId;
    }

    public void setPositionId(BigDecimal positionId) {
        this.positionId = positionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAccountBalanceAfter() {
        return accountBalanceAfter;
    }

    public void setAccountBalanceAfter(BigDecimal accountBalanceAfter) {
        this.accountBalanceAfter = accountBalanceAfter;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

}
