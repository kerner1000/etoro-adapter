package com.github.kerner1000.etoro.stats.model;

@Deprecated
public interface Trade {

    String getInstrument();

    Number getTransactionId();

    Number getAmount();
}
