package com.github.kerner1000.etoro.stats.model;

public class TransactionGroup extends Group<Transaction> {

    public TransactionGroup() {
    }

    public TransactionGroup(Iterable<? extends Transaction> elements) {
        super(elements);
    }

}
