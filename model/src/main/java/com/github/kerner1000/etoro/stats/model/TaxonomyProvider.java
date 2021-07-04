package com.github.kerner1000.etoro.stats.model;

public interface TaxonomyProvider {

    Taxonomy getTaxonomy(String identifier, String instrument);

}
