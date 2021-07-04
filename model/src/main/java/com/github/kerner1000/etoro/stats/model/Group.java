package com.github.kerner1000.etoro.stats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Group<T extends AmountSupplier> implements Iterable<T>, AmountSupplier {

    private List<T> elements;

    private String groupIdentifier;

    public Group() {
        elements = new ArrayList<>();
    }

    public Group(Iterable<? extends T> elements) {
        this();
        elements.forEach(this.elements::add);
    }

    public BigDecimal getAmount() {
        return getElements().stream().map(e -> e.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean add(T element){
        return elements.add(element);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    // Getter / Setter //

    public String getGroupIdentifier() {
        return groupIdentifier;
    }

    public void setGroupIdentifier(String groupIdentifier) {
        this.groupIdentifier = groupIdentifier;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }
}
