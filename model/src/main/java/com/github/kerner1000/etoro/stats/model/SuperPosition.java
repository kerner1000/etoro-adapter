package com.github.kerner1000.etoro.stats.model;

public class SuperPosition {

    private final String identifier;

    private final Positions positions;

    public SuperPosition(String identifier, Positions positions) {
        this.identifier = identifier;
        this.positions = positions;
    }

    public Positions getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-40s", identifier));
        sb.append(": ");
        sb.append(String.format("%,10.2f", positions.getTotalAmount()));

        return sb.toString();
    }

    public String toString(String delimiter) {

        if(delimiter == null){
            return toString();
        }

        StringBuilder sb = new StringBuilder();

        sb.append(identifier);
        sb.append(delimiter);
        sb.append(positions.getTotalAmount());

        return sb.toString();
    }
}
