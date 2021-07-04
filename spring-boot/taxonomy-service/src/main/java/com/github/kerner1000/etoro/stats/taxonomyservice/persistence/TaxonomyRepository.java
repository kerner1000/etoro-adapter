package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TaxonomyRepository extends CrudRepository<TaxonomyEntity, Integer> {

    @Transactional
    TaxonomyEntity getTaxonomyEntityByIdentifierAndTickerEntity(String identifier, TickerEntity ticker);

}
