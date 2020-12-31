package com.github.kerner1000.etoro.stats.taxonomyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.spring.boot.api.TaxonomyService;
import com.github.kerner1000.etoro.stats.spring.boot.util.exceptions.InvalidInputException;
import com.github.kerner1000.etoro.stats.spring.boot.util.exceptions.NotFoundException;
import com.github.kerner1000.etoro.stats.spring.boot.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class CompositeIntegration implements TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(CompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String taxonomyProviderUrl;

    @Autowired
    CompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.taxonomy-provider.host}") String taxonomyProviderHost,
            @Value("${app.taxonomy-provider.port}") int taxonomyProviderPort

    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.taxonomyProviderUrl = "http://" + taxonomyProviderHost + ":" + taxonomyProviderPort + "/taxonomy/";

    }

    @Override
    public DefaultTaxonomy getTaxonomy(String exchange, String identifier, String instrument) {

        if(exchange == null){
            return getTaxonomy(identifier, instrument);
        }

        if(identifier == null || instrument == null){
            throw new IllegalArgumentException();
        }
        try {
            String url = taxonomyProviderUrl + exchange + "/" + identifier + "/" + instrument;
            logger.debug("Will call API on URL: {}", url);

            DefaultTaxonomy result = restTemplate.getForObject(url, DefaultTaxonomy.class);
            logger.debug("returning: {}", result);

            return result;

        } catch (HttpClientErrorException ex) {

            switch (ex.getStatusCode()) {

                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY :
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    logger.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    logger.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    @Override
    public Set<String> getExchangeForTicker(String ticker) {
        if(ticker == null){
            throw new IllegalArgumentException();
        }
        try {
            String url = taxonomyProviderUrl + ticker;
            logger.debug("Will call API on URL: {}", url);

            Set<String> exchanges = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Set<String>>() {
            }).getBody();
            logger.debug("returning: {}", exchanges);

            return exchanges;

        } catch (HttpClientErrorException ex) {

            switch (ex.getStatusCode()) {

                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY :
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    logger.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    logger.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {
        if(identifier == null || instrument == null){
            throw new IllegalArgumentException();
        }
        try {
            String url = taxonomyProviderUrl + identifier + "/" + instrument;
            logger.debug("Will call API on URL: {}", url);

            DefaultTaxonomy result = restTemplate.getForObject(url, DefaultTaxonomy.class);
            logger.debug("returning: {}", result);

            return result;

        } catch (HttpClientErrorException ex) {

            switch (ex.getStatusCode()) {

                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY :
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    logger.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    logger.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
