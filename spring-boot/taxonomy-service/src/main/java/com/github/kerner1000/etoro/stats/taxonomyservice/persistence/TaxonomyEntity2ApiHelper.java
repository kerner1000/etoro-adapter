package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;

public class TaxonomyEntity2ApiHelper {

    private TaxonomyEntity taxonomyEntity;

    public TaxonomyEntity2ApiHelper() {

    }

    @BeforeMapping
    public void beforeMapping(@MappingTarget TaxonomyEntity entity) {
        this.taxonomyEntity = entity;
    }

    @AfterMapping
    public void afterMapping(@MappingTarget DefaultTaxonomy taxonomy) {

    }


}
