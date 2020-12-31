package com.github.kerner1000.etoro.stats.model;

import java.util.*;

public class Positions implements Iterable<Position>{

    private String identifer;

    private List<Position> positions;

    public Positions(Map<String, Collection<Trade>> instrumentToTransactionsMap) {
        this.positions = new ArrayList<>();
        for (Map.Entry<String, Collection<Trade>> e : instrumentToTransactionsMap.entrySet()) {
            positions.add(new Position(e.getValue()));
        }
        positions.sort((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()));
    }

    public Positions(Collection<Position> positions) {
        this.positions = new ArrayList<>(positions);
        this.positions.sort((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()));
    }

    public Positions() {
        this.positions = new ArrayList<>();
    }

    public Positions(String identifer) {
        this();
        setIdentifer(identifer);
    }

    public boolean add(Position position){
        return this.positions.add(position);
    }

    public List<Position> getPositions() {
        return positions;
    }

    public int size(){
        return positions.size();
    }

    @Override
    public Iterator<Position> iterator() {
        return positions.iterator();
    }

    public double getPercentageAmount(Position p) {
        double totalAmount = getTotalAmount();
        return p.getAmount() / totalAmount * 100;
    }

    public double getTotalAmount() {
        return positions.stream().mapToDouble(p -> p.getAmount()).sum();
    }

    public String getIdentifer() {
        return identifer;
    }

    public void setIdentifer(String identifer) {
        this.identifer = identifer;
    }
}
