package com.github.kerner1000.etoro.stats.model;

import java.util.Objects;

@Deprecated
public class OpenTrade implements Trade {

    private Transaction openTransaction;

    public OpenTrade(Transaction openTransaction) {
        this.openTransaction = Objects.requireNonNull(openTransaction);
    }

    OpenTrade() {

    }

    @Override
    public String getInstrument() {
        return openTransaction.getInstrument();
    }

    public Number getTransactionId(){
        return openTransaction.getTransactionId();
    }

    @Override
    public Number getAmount() {
        return openTransaction.getAmount().doubleValue();
    }

    @Override
    public String toString() {
        return "OpenPosition{" +
                "openTransaction=" + openTransaction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenTrade that = (OpenTrade) o;
        return openTransaction.equals(that.openTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openTransaction);
    }

    // Getter / Setter //


    public Transaction getOpenTransaction() {
        return openTransaction;
    }

    void setOpenTransaction(Transaction openTransaction) {
        this.openTransaction = openTransaction;
    }
}
