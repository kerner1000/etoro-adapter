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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.transaction.Transactional;

@RestController
public class DefaultTaxonomyService implements TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTaxonomyService.class);

    private final ServiceUtil serviceUtil;

    private final CompositeIntegration compositeIntegration;

    private final TaxonomyRepository taxonomyRepository;

    private final TickerRepository tickerRepository;

    private final TaxonomyMapper mapper;

    private final Scheduler scheduler;

    @Autowired
    DefaultTaxonomyService(Scheduler scheduler, ServiceUtil serviceUtil,
                           CompositeIntegration compositeIntegration,
                           TaxonomyRepository taxonomyRepository,
                           TaxonomyMapper mapper,
                           TickerRepository tickerRepository) {
        this.scheduler = scheduler;
        this.serviceUtil = serviceUtil;
        this.compositeIntegration = compositeIntegration;
        this.taxonomyRepository = taxonomyRepository;
        this.mapper = mapper;
        this.tickerRepository = tickerRepository;

    }

    @Transactional
    TickerEntity getTickerEntity(String instrument) {
        TickerEntity tickerEntity = tickerRepository.getTickerEntityByName(instrument);

        if (tickerEntity == null) {
            tickerEntity = tickerRepository.save(new TickerEntity(instrument));
            logger.info("Saved new {}: {}", TickerEntity.class.getSimpleName(), tickerEntity);
        }

        return tickerEntity;
    }

    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {

        DefaultTaxonomy defaultTaxonomy = getTaxonomy(identifier, getTickerEntity(instrument));

        return defaultTaxonomy;
    }

    private Mono<DefaultTaxonomy> asyncMono(Taxonomy taxonomy) {
        return Mono.just(new DefaultTaxonomy(taxonomy)).publishOn(scheduler);
    }

    @Transactional
    DefaultTaxonomy getTaxonomy(String identifier, TickerEntity tickerEntity) {

        Taxonomy taxonomy = taxonomyRepository.getTaxonomyEntityByIdentifierAndTickerEntity(identifier, tickerEntity);
        if (taxonomy != null && taxonomy.isComplete()) {
            return new DefaultTaxonomy(taxonomy);
        }
        
        logger.debug("Didn't find in db [{}], asking delegate", identifier + "," + tickerEntity);
        DefaultTaxonomy taxonomyMono = compositeIntegration.getTaxonomy(identifier, tickerEntity.getName());
        logger.debug("Got {} from composite, persisting", taxonomyMono);


            if (taxonomyMono.isComplete()) {
                TaxonomyEntity taxonomyEntity = mapper.apiToEntity(taxonomy, new TaxonomyApi2EntityHelper());
                taxonomyEntity.setTickerEntity(tickerEntity);
                tickerEntity.getTaxonomies().add(taxonomyEntity);
                taxonomyRepository.save(taxonomyEntity);
                logger.debug("Saved new entity '{}' -> '{}'", taxonomy, taxonomyEntity);
            } else {
                logger.debug("Did not save incomplete taxonomy {}", taxonomy);
            }
            return taxonomyMono;



    }


}
