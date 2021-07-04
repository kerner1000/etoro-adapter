package com.github.kerner1000.etoro.stats.model;

public class DefaultTaxonomy implements Taxonomy {

    private String identifier;

    private String value;

    private String instrument;

    public DefaultTaxonomy(Taxonomy taxonomy) {
        this(taxonomy.getIdentifier(), taxonomy.getValue(), taxonomy.getInstrument());
    }

    public DefaultTaxonomy(String identifier, String value, String instrument) {
        this.identifier = identifier;
        this.value = value;
        this.instrument = instrument;
    }

    public DefaultTaxonomy() {

    }

    @Override
    public boolean isComplete() {
        return getValue() != null;
    }

    @Override
    public String toString() {
        return "DefaultTaxonomy{" +
                "identifier='" + identifier + '\'' +
                ", value='" + value + '\'' +
                ", instrument='" + instrument + '\'' +
                '}';
    }

    // Getter / Setter //

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getInstrument() {
        return instrument;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setValue(String value) {
        this.value = value;
    }



    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

}
