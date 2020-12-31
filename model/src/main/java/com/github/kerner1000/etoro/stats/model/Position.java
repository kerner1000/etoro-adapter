package com.github.kerner1000.etoro.stats.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Position<T extends Trade> {

    private String instrument;

    private double amount;

    private Collection<T> trades;

    protected Position(Collection<T> trades) {
        Objects.requireNonNull(trades);
        if(trades.isEmpty()){
            throw new IllegalArgumentException();
        }
        if(!Trades.samePosition(trades)){
            throw new IllegalArgumentException();
        }
        this.instrument = trades.iterator().next().getInstrument();
        this.amount = trades.stream().mapToDouble(t -> t.getAmount().doubleValue()).sum();
        this.trades = trades;
    }

    public Position(String instrument, double amount) {
        this.instrument = instrument;
        this.amount = amount;
        this.trades = Collections.emptyList();
    }

    public Position(){

    }

    @Override
    public String toString() {
        return "Position: " + String.format("%-12s: %8.2f", instrument, amount);
    }

    // Getter / Setter //


    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Collection<T> getTrades() {
        return trades;
    }

    public void setTrades(Collection<T> trades) {
        this.trades = trades;
    }
}
