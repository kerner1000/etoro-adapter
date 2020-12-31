package com.github.kerner1000.etoro.stats.taxonomyservice.services;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.model.Taxonomy;
import com.github.kerner1000.etoro.stats.spring.boot.api.TaxonomyService;
import com.github.kerner1000.etoro.stats.spring.boot.util.http.ServiceUtil;
import com.github.kerner1000.etoro.stats.taxonomyservice.CompositeIntegration;
import com.github.kerner1000.etoro.stats.taxonomyservice.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Set;

@RestController
public class DefaultTaxonomyService implements TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTaxonomyService.class);

    private final ServiceUtil serviceUtil;

    private final CompositeIntegration compositeIntegration;

    private final TaxonomyRepository taxonomyRepository;

    private final ExchangeRepository exchangeRepository;

    private final TickerRepository tickerRepository;

    private final TaxonomyMapper mapper;

    @Autowired
    DefaultTaxonomyService(ServiceUtil serviceUtil,
                           CompositeIntegration compositeIntegration,
                           TaxonomyRepository taxonomyRepository,
                           TaxonomyMapper mapper,
                           ExchangeRepository exchangeRepository,
                           TickerRepository tickerRepository) {
        this.serviceUtil = serviceUtil;
        this.compositeIntegration = compositeIntegration;
        this.taxonomyRepository = taxonomyRepository;
        this.mapper = mapper;
        this.exchangeRepository = exchangeRepository;
        this.tickerRepository = tickerRepository;
    }

    @Override
    public Set<String> getExchangeForTicker(String ticker) {
        return compositeIntegration.getExchangeForTicker(ticker);
    }

    @Transactional
    TickerEntity getTickerEntity(String instrument) {
        TickerEntity tickerEntity = tickerRepository.getTickerEntityByName(instrument);

        if (tickerEntity == null) {
            tickerEntity = tickerRepository.save(new TickerEntity(instrument));
            logger.info("Saved new {}: {}", TickerEntity.class.getSimpleName(), tickerEntity);
        }

        if(tickerEntity.getName().toLowerCase().contains("pltr")){
            int wait = 0;
        }

        return tickerEntity;
    }

    @Transactional
    Set<ExchangeEntity> getExchangeEntity(TickerEntity tickerEntity) {

        if(tickerEntity.getName().toLowerCase().contains("pltr")){
            int wait = 0;
        }

        Set<ExchangeEntity> exchangeEntities = exchangeRepository.getExchangeEntityByTickersContains(tickerEntity);

        if (exchangeEntities == null || exchangeEntities.isEmpty()) {
            Set<String> exchangeName = compositeIntegration.getExchangeForTicker(tickerEntity.getName());
            for (String en : exchangeName) {
                ExchangeEntity exchangeEntity2 = exchangeRepository.getExchangeEntityByName(en);
                if (exchangeEntity2 == null) {
                    exchangeEntity2 = exchangeRepository.save(new ExchangeEntity(en));
                    logger.info("Saved new {}: {}", ExchangeEntity.class.getSimpleName(), exchangeEntity2);
                }
                exchangeEntities.add(exchangeEntity2);
            }
        }

        exchangeEntities.forEach(e -> e.getTickers().add(tickerEntity));
        tickerEntity.setExchanges(exchangeEntities);

        return exchangeEntities;
    }

    @Override
    public DefaultTaxonomy getTaxonomy(String exchange, String identifier, String instrument) {
        TickerEntity tickerEntity = getTickerEntity(instrument);
        ExchangeEntity exchangeEntity = exchangeRepository.getExchangeEntityByName(exchange);
        DefaultTaxonomy defaultTaxonomy = getTaxonomy(identifier, tickerEntity, exchangeEntity);
        return defaultTaxonomy;
    }

    @Transactional
    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {

        TickerEntity tickerEntity = getTickerEntity(instrument);

        Set<ExchangeEntity> exchangeEntity = getExchangeEntity(tickerEntity);

        DefaultTaxonomy defaultTaxonomy = getTaxonomy(identifier, tickerEntity, exchangeEntity.iterator().next());

        return defaultTaxonomy;
    }

    DefaultTaxonomy getTaxonomy(String identifier, TickerEntity tickerEntity, ExchangeEntity exchangeEntity) {
        Taxonomy taxonomy = taxonomyRepository.getTaxonomyEntityByIdentifierAndTickerEntity(identifier, tickerEntity);

        if (taxonomy == null || !taxonomy.isComplete()) {
            logger.debug("Didn't find in db [{}], asking delegate", identifier + "," + tickerEntity);
            taxonomy = compositeIntegration.getTaxonomy(exchangeEntity.getName(), identifier, tickerEntity.getName());
            logger.debug("Got {} from composite, persisting", taxonomy);

            TaxonomyEntity taxonomyEntity = mapper.apiToEntity(taxonomy, new TaxonomyApi2EntityHelper());
            taxonomyEntity.setTickerEntity(tickerEntity);
            tickerEntity.getTaxonomies().add(taxonomyEntity);
            taxonomyRepository.save(taxonomyEntity);

            logger.debug("Saved new entity '{}' -> '{}'", taxonomy, taxonomyEntity);

        } else {
//            logger.debug("Got {} from db", taxonomy);
        }

        if (taxonomy.toString().contains("Telekom")) {
            int wait = 0;
        }
        return new DefaultTaxonomy(taxonomy);
    }


}
