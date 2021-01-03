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

    private final TickerRepository tickerRepository;

    private final TaxonomyMapper mapper;

    @Autowired
    DefaultTaxonomyService(ServiceUtil serviceUtil,
                           CompositeIntegration compositeIntegration,
                           TaxonomyRepository taxonomyRepository,
                           TaxonomyMapper mapper,
                           TickerRepository tickerRepository) {
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

        if(tickerEntity.getName().toLowerCase().contains("pltr")){
            int wait = 0;
        }

        return tickerEntity;
    }

    @Transactional
    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {

        TickerEntity tickerEntity = getTickerEntity(instrument);

        DefaultTaxonomy defaultTaxonomy = getTaxonomy(identifier, tickerEntity);

        return defaultTaxonomy;
    }

    DefaultTaxonomy getTaxonomy(String identifier, TickerEntity tickerEntity) {
        Taxonomy taxonomy = taxonomyRepository.getTaxonomyEntityByIdentifierAndTickerEntity(identifier, tickerEntity);

        if (taxonomy == null || !taxonomy.isComplete()) {
            logger.debug("Didn't find in db [{}], asking delegate", identifier + "," + tickerEntity);
            taxonomy = compositeIntegration.getTaxonomy(identifier, tickerEntity.getName());
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
