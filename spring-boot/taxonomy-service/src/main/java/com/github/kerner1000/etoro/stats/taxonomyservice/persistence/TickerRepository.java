package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TickerRepository extends CrudRepository<TickerEntity, String> {

    @Transactional(readOnly = true)
    TickerEntity getTickerEntityByName(String name);
}
