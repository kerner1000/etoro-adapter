package com.github.kerner1000.etoro.stats.model;

import java.util.Collection;
import java.util.stream.Collectors;

public class Trades {

    public static boolean sameTransaction(Transaction openTransaction, Transaction closeTransaction) {
        return openTransaction.getTransactionId().equals(closeTransaction.getTransactionId());
    }

    public static boolean samePosition(Collection<? extends Trade> trades) {
        return trades.stream().map(t -> t.getInstrument()).collect(Collectors.toSet()).size() == 1;
    }
}
