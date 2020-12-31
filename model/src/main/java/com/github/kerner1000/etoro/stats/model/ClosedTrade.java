package com.github.kerner1000.etoro.stats.model;

import java.util.Objects;

public class ClosedTrade implements Trade {

    private final Transaction openTransaction;

    private final Transaction closeTransaction;

    public ClosedTrade(Transaction openTransaction, Transaction closeTransaction) {
        this.openTransaction = Objects.requireNonNull(openTransaction);
        this.closeTransaction = Objects.requireNonNull(closeTransaction);
        if(!Trades.sameTransaction(openTransaction, closeTransaction)){
            throw new IllegalArgumentException();
        }
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
        return openTransaction.getAmount().doubleValue() + closeTransaction.getAmount().doubleValue();
    }

    @Override
    public String toString() {
        return "ClosedPosition open=" + openTransaction + "\n" +
                "              close=" + closeTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClosedTrade that = (ClosedTrade) o;
        return openTransaction.equals(that.openTransaction) &&
                closeTransaction.equals(that.closeTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openTransaction, closeTransaction);
    }
}
