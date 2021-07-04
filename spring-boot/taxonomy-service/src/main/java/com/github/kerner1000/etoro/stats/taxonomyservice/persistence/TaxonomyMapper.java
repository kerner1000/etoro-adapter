package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.model.Taxonomy;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaxonomyMapper {

    DefaultTaxonomy entityToApi(TaxonomyEntity entity);

    @Mappings({
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "instrument", ignore = true),
            @Mapping(target = "id", ignore = true ),
            @Mapping(target = "complete", ignore = true )
    })
    TaxonomyEntity apiToEntity(Taxonomy api, @Context TaxonomyApi2EntityHelper taxonomyApi2EntityHelper);
}