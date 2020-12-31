package com.github.kerner1000.etoro.stats.taxonomyservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface ExchangeRepository extends CrudRepository<ExchangeEntity, String> {

    @Transactional(readOnly = true)
    ExchangeEntity getExchangeEntityByName(String name);

    @Transactional(readOnly = true)
    Set<ExchangeEntity> getExchangeEntityByTickersContains(TickerEntity ticker);
}
