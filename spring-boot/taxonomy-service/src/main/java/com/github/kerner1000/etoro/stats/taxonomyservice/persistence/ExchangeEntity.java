package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "exchange")
public class ExchangeEntity {


    @Version
    private int version;

    @Id
    private String name;

    @ManyToMany
    private Set<TickerEntity> tickers;

    public ExchangeEntity(String name) {
        this();
        this.name = name;
    }

    public ExchangeEntity() {
        tickers = new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        return "ExchangeEntity{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeEntity that = (ExchangeEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    // Getter / Setter //


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Set<TickerEntity> getTickers() {
        return tickers;
    }

    public void setTickers(Set<TickerEntity> tickers) {
        this.tickers = tickers;
    }
}
