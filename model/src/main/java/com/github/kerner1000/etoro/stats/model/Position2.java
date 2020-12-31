package com.github.kerner1000.etoro.stats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class Position2 implements InstrumentSupplier, AmountSupplier {

    private TransactionGroup transactions;

    private TaxonomyGroup taxonomies;

    public Position2(){

    }

    public Position2(TransactionGroup transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getAmount(){
        return transactions.getElements().stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getInstrument(){
        if(transactions == null || transactions.isEmpty()){
            return null;
        }
        return transactions.getElements().stream().findFirst().get().getInstrument();
    }

    // Getter / Setter //


    public TransactionGroup getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionGroup transactions) {
        this.transactions = transactions;
    }

    public TaxonomyGroup getTaxonomies() {
        return taxonomies;
    }

    public void setTaxonomies(TaxonomyGroup taxonomies) {
        this.taxonomies = taxonomies;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return transactions.isEmpty();
    }
}
