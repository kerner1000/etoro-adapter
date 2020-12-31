package com.github.kerner1000.etoro.stats.model;

import java.util.*;

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

    public Collection<Transaction> getTransactions() {
        return Collections.unmodifiableCollection(transactions);
    }

    public void setTransactions(Collection<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    public Collection<OpenTrade> getOpenTrades() {
        Collection<OpenTrade> result = new ArrayList<>();


        Map<Number, Transaction> open = new LinkedHashMap<>();
        Map<Number, Transaction> close = new LinkedHashMap<>();

        for (Transaction t : getTransactions()) {
            switch (t.getType()) {
                case OPEN_POSITION:
                    open.put(t.getTransactionId(), t);
                    break;
                case CLOSE_POSITION:
                    close.put(t.getTransactionId(), t);
                    break;
                default:
            }
        }

        for (Map.Entry<Number, Transaction> e : open.entrySet()) {
            Transaction t = close.get(e.getKey());
            if (t == null)
                result.add(new OpenTrade(e.getValue()));
        }

        return result;
    }

    public Collection<ClosedTrade> getClosedTrades() {
        Collection<ClosedTrade> result = new ArrayList<>();

        Map<Number, Transaction> open = new LinkedHashMap<>();
        Map<Number, Transaction> close = new LinkedHashMap<>();

        for (Transaction t : getTransactions()) {
            switch (t.getType()) {
                case OPEN_POSITION:
                    open.put(t.getTransactionId(), t);
                    break;
                case CLOSE_POSITION:
                    close.put(t.getTransactionId(), t);
                    break;
                default:
            }
        }

        for (Map.Entry<Number, Transaction> e : open.entrySet()) {
            Transaction t = close.get(e.getKey());
            if (t != null)
                result.add(new ClosedTrade(e.getValue(), t));
        }

        return result;
    }

    public Collection<Trade> getAllTrades() {
        Collection<Trade> result = new ArrayList<>();
        result.addAll(getOpenTrades());
        result.addAll(getClosedTrades());
        return result;
    }

    public Positions getOpenPositions() {
        Collection<OpenTrade> openTrades = getOpenTrades();
        Map<String, Collection<Trade>> instrumentToTransactionsMap = new LinkedHashMap<>();
        for (OpenTrade t : openTrades) {
            String instrument = t.getInstrument();
            Collection<Trade> transactions = instrumentToTransactionsMap.get(instrument);
            if (transactions == null) {
                transactions = new ArrayList<>();
            }
            transactions.add(t);
            instrumentToTransactionsMap.put(instrument, transactions);
        }
        Positions result = new Positions(instrumentToTransactionsMap);
//        result.add(new Position("Cash", getFreeAmount()));
        return result;
    }

    public Positions getAllPositions() {
        Collection<Trade> openTrades = getAllTrades();
        Map<String, Collection<Trade>> instrumentToTransactionsMap = new LinkedHashMap<>();
        for (Trade t : openTrades) {
            String instrument = t.getInstrument();
            Collection<Trade> transactions = instrumentToTransactionsMap.get(instrument);
            if (transactions == null) {
                transactions = new ArrayList<>();
            }
            transactions.add(t);
            instrumentToTransactionsMap.put(instrument, transactions);
        }
        return new Positions(instrumentToTransactionsMap);
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

    public double getPercentageAmount(Position p) {
        double freeAmount = getFreeAmount();
        double totalAmount = getTotalAmount();
        return p.getAmount() / (totalAmount + freeAmount) * 100;
    }

    private double getTotalAmount() {
        return transactions.stream().mapToDouble(t -> t.getAmount().doubleValue()).sum();
    }

}
