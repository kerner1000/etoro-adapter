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
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Component
public class CompositeIntegration implements TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(CompositeIntegration.class);

    private final WebClient webClient;

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final String taxonomyProviderUrl;

    @Autowired CompositeIntegration(
            RestTemplate restTemplate,
            WebClient.Builder webclient,
            ObjectMapper mapper,

            @Value("${app.taxonomy-provider.host}") String taxonomyProviderHost,
            @Value("${app.taxonomy-provider.port}") int taxonomyProviderPort

    ) {
        this.restTemplate = restTemplate;
        this.webClient = webclient.build();
        this.mapper = mapper;

        this.taxonomyProviderUrl = "http://" + taxonomyProviderHost + ":" + taxonomyProviderPort + "/taxonomy/";

    }

    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {
        if (identifier == null || instrument == null) {
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

                case UNPROCESSABLE_ENTITY:
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

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            logger.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException) ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                logger.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                logger.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
