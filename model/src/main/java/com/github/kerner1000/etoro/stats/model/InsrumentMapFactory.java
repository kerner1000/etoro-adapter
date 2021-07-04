package com.github.kerner1000.etoro.stats.model;

import java.util.*;

public class InsrumentMapFactory<T extends InstrumentSupplier> {

    private final Collection<T> suppliers;

    public InsrumentMapFactory(Iterable<? extends T> suppliers){
        this.suppliers = new ArrayList<>();
        suppliers.forEach(this.suppliers::add);
    }

    public Map<String, List<T>> getMap(){
        Map<String, List<T>> result = new LinkedHashMap<>();

        for(T supplier : suppliers){
            List<T> value = result.get(supplier.getInstrument());
            if(value == null){
                value = new ArrayList<>();
                result.put(supplier.getInstrument(), value);
            }
            value.add(supplier);
        }

        return result;
    }
}
