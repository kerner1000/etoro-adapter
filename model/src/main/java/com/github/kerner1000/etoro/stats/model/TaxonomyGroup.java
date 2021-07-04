package com.github.kerner1000.etoro.stats.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TaxonomyGroup {

    private List<DefaultTaxonomy> taxonomies;

    public TaxonomyGroup(){}

    public TaxonomyGroup(DefaultTaxonomy... taxonomies) {
        this(Arrays.asList(taxonomies));
    }

    public TaxonomyGroup(Collection<DefaultTaxonomy> taxonomies) {
        this.taxonomies = new ArrayList<>(taxonomies);
    }

    public boolean addTaxonomy(DefaultTaxonomy taxonomy){
        return this.taxonomies.add(taxonomy);
    }

    // Getter / Setter //

    public List<DefaultTaxonomy> getTaxonomies() {
        return taxonomies;
    }

    public void setTaxonomies(List<DefaultTaxonomy> taxonomies) {
        this.taxonomies = taxonomies;
    }
}
