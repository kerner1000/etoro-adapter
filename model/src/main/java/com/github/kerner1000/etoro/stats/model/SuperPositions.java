package com.github.kerner1000.etoro.stats.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SuperPositions {

    private final List<SuperPosition> superPositions;

    public SuperPositions(Map<String, Positions> positionsMap) {
        Objects.requireNonNull(positionsMap);
        if (positionsMap.isEmpty()) {
            throw new IllegalArgumentException();
        }
        superPositions = new ArrayList<>();
        positionsMap.entrySet().forEach(e -> superPositions.add(new SuperPosition(e.getKey(), e.getValue())));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        superPositions.sort((p1, p2) -> Double.compare(p2.getPositions().getTotalAmount(), p1.getPositions().getTotalAmount()));

        for (SuperPosition p : superPositions) {
            sb.append(p);
            sb.append(" (");
            sb.append(String.format("%6.2f", p.getPositions().getTotalAmount() / getAmount() * 100));
            sb.append("%)");
            sb.append("\n");
        }

        sb.append(String.format("%-40s: %,10.2f", "Sum", getAmount()));
        sb.append(" (");
        sb.append(String.format("%6.2f", getAmount() / getAmount() * 100));
        sb.append("%)");

        return sb.toString();
    }

    private double getAmount() {
        return superPositions.stream().mapToDouble(s -> s.getPositions().getTotalAmount()).sum();
    }

    public String toString(String delimiter) {

        if(delimiter == null){
            return toString();
        }

        StringBuilder sb = new StringBuilder();

        superPositions.sort((p1, p2) -> Double.compare(p2.getPositions().getTotalAmount(), p1.getPositions().getTotalAmount()));

        for (SuperPosition p : superPositions) {
            sb.append(p.toString(delimiter));
            sb.append(delimiter);
            sb.append(p.getPositions().getTotalAmount() / getAmount() * 100);
            sb.append("\n");
        }

        sb.append("Sum" + delimiter + getAmount());
        sb.append(delimiter);
        sb.append(getAmount() / getAmount() * 100);

        return sb.toString();
    }
}
