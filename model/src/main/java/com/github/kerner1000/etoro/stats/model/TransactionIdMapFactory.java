package com.github.kerner1000.etoro.stats.model;

import java.util.*;

public class TransactionIdMapFactory<T extends TransactionIdSupplier> {

    private final Collection<T> suppliers;

    public TransactionIdMapFactory(Iterable<? extends T> suppliers){
        this.suppliers = new ArrayList<>();
        suppliers.forEach(this.suppliers::add);
    }

    public Map<Number, List<T>> getTransactionIdMap(){
        Map<Number, List<T>> result = new LinkedHashMap<>();

        for(T supplier : suppliers){
            List<T> value = result.get(supplier.getTransactionId());
            if(value == null){
                value = new ArrayList<>();
                result.put(supplier.getTransactionId(), value);
            }
            value.add(supplier);
        }

        return result;
    }
}
