package com.github.kerner1000.etoro.stats.transactionsservice.persistence;

import com.github.kerner1000.etoro.stats.model.TransactionIdSupplier;
import com.github.kerner1000.etoro.stats.model.TransactionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name ="transactions")
public class TransactionEntity implements TransactionIdSupplier {

    @Version
    private int version;

    @Id
    private long transactionId;

    private BigDecimal amount;

    private BigDecimal accountBalanceAfter;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String instrument;

    // Getter / Setter //

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
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
