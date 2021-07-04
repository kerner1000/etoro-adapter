package com.github.kerner1000.etoro.stats.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class EToroDefaultModel {

    private Collection<Transaction> transactions = new ArrayList<>();

    public EToroDefaultModel() {

    }

    public EToroDefaultModel(Collection<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void setTransactions(Collection<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    public double getFreeAmount() {
        return getLastTransaction().getAccountBalanceAfter().doubleValue();
    }

    private Transaction getLastTransaction() {
        Transaction lastTransaction = transactions.stream().sorted(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        }.reversed()).findFirst().orElse(null);
        return lastTransaction;
    }

    private double getTotalAmount() {
        return transactions.stream().mapToDouble(t -> t.getAmount().doubleValue()).sum();
    }

}
