package com.github.kerner1000.etoro.stats.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements InstrumentSupplier, AmountSupplier {

    private BigDecimal transactionId;

    private BigDecimal amount;

    private BigDecimal accountBalanceAfter;

    private LocalDateTime dateTime;

    private TransactionType type;

    private String instrument;

    public Transaction(BigDecimal transactionId, BigDecimal amount, LocalDateTime dateTime, TransactionType type, String instrument, BigDecimal accountBalanceAfter) {
        this.transactionId = Objects.requireNonNull(transactionId);
        this.amount = Objects.requireNonNull(amount);
        this.dateTime = Objects.requireNonNull(dateTime);
        this.type = Objects.requireNonNull(type);
        this.instrument = Objects.requireNonNull(instrument.replaceAll("\\s+", ""));
        this.accountBalanceAfter = accountBalanceAfter;
    }

    public Transaction(BigDecimal transactionId, BigDecimal amount, LocalDateTime dateTime, TransactionType type, String instrument) {
        this(transactionId, amount, dateTime, type, instrument, null);
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
        Transaction transaction = (Transaction) o;
        return transactionId.equals(transaction.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    // Getter / Setter //


    public BigDecimal getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(BigDecimal transactionId) {
        this.transactionId = transactionId;
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
