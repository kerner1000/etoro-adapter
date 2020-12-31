package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import com.github.kerner1000.etoro.stats.model.Taxonomy;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "taxonomies")
public class TaxonomyEntity implements Taxonomy {

    @GeneratedValue
    @Id
    private int id;

    /**
     * Optimistic locking.
     */
    @Version
    private int version;

    /**
     * E.g., "sector".
     */
    private String identifier;

    /**
     * E.g., "Technology".
     */
    private String value;

    @ManyToOne
    private TickerEntity tickerEntity;

    public TaxonomyEntity() {

    }

    public TaxonomyEntity(String identifier, String value) {
        this.value = value;
        this.identifier = identifier;
    }

    public TaxonomyEntity(String identifier) {
        this.identifier = identifier;
    }


    @Override
    public boolean isComplete() {
        return getValue() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxonomyEntity that = (TaxonomyEntity) o;
        return identifier.equals(that.identifier) && tickerEntity.equals(that.tickerEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, tickerEntity);
    }

    @Override
    public String toString() {
        return "TaxonomyEntity{" +
                "id=" + id +
                ", version=" + version +
                ", identifier='" + identifier + '\'' +
                ", value='" + value + '\'' +
                ", tickerEntity=" + tickerEntity +
                '}';
    }

    @Override
    public String getInstrument() {
        if(getTickerEntity() == null)
            return null;
        return getTickerEntity().getName();
    }

    // Getter / Setter //


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public TickerEntity getTickerEntity() {
        return tickerEntity;
    }

    public void setTickerEntity(TickerEntity tickerEntity) {
        this.tickerEntity = tickerEntity;
    }
}
