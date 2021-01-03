package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ticker")
public class TickerEntity {

    @Version
    private int version;

    @Id
    private String name;

    @OneToMany
    private Set<TaxonomyEntity> taxonomies;

    public TickerEntity(String name) {
        this();
        this.name = name;
    }

    public TickerEntity() {
        this.taxonomies = new LinkedHashSet<>();

    }

    @Override
    public String toString() {
        return "TickerEntity{" +
                "version=" + version +
                ", name='" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerEntity that = (TickerEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TaxonomyEntity> getTaxonomies() {
        return taxonomies;
    }

    public void setTaxonomies(Set<TaxonomyEntity> taxonomies) {
        this.taxonomies = taxonomies;
    }
}
