package com.github.kerner1000.etoro.stats.taxonomy.morningstar.services;

import com.github.kerner1000.etoro.stats.api.MorningstarAPI;
import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.spring.boot.api.TaxonomyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.scheduler.Scheduler;

@RestController
public class DefaultMorningstarTaxonomyService implements TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMorningstarTaxonomyService.class);

    private final MorningstarAPI api;

    private final Scheduler scheduler;

    @Autowired
    public DefaultMorningstarTaxonomyService(Scheduler scheduler, @Value("${app.morningstar.apikey}") String apikey) {
        this.scheduler = scheduler;
        this.api = new MorningstarAPI(apikey);
    }

    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {

        logger.info("Querying MorningStar API for identifier: '{}' and instrument: '{}'", identifier, instrument);

        DefaultTaxonomy taxonomy = buildTaxonomy(identifier, instrument);

        logger.debug("Got taxonomy: {} ", taxonomy);

        return taxonomy;
    }

    DefaultTaxonomy buildTaxonomy(String identifier, String instrument) {
        DefaultTaxonomy result = new DefaultTaxonomy();
        result.setIdentifier(identifier);
        result.setInstrument(instrument);
        switch (identifier) {
            case "industry":
                result.setValue(api.findIndustry(instrument));
                break;
            case "sector":
                result.setValue(api.findSector(instrument));
                break;
            case "name":
                result.setValue(api.findName(instrument));
                break;
            default:
                throw new RuntimeException("Unknown identifier " + identifier);
        }
        return result;
    }
}
