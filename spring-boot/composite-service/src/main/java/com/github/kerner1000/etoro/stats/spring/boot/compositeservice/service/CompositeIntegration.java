package com.github.kerner1000.etoro.stats.spring.boot.compositeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kerner1000.etoro.stats.model.*;
import com.github.kerner1000.etoro.stats.spring.boot.api.PositionService;
import com.github.kerner1000.etoro.stats.spring.boot.api.TaxonomyService;
import com.github.kerner1000.etoro.stats.spring.boot.api.TransactionService;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;

@Component
public class CompositeIntegration implements TransactionService, PositionService, TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(CompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final ObjectMapper mapper;

    private final String transactionsServiceUrl;
    private final String positionServiceUrl;
    private final String taxonomyServiceUrl;

    @Autowired
    CompositeIntegration(
            RestTemplate restTemplate,
            WebClient.Builder webclient,
            ObjectMapper mapper,

            @Value("${app.transaction-service.host}") String transactionServiceHost,
            @Value("${app.transaction-service.port}") int transactionServicePort,

            @Value("${app.position-service.host}") String positionServiceHost,
            @Value("${app.position-service.port}") int positionServicePort,

            @Value("${app.taxonomy-service.host}") String taxonomyServiceHost,
            @Value("${app.taxonomy-service.port}") int taxonomyServicePort
    ) {
        this.restTemplate = restTemplate;

        this.webClient = webclient.build();

        this.mapper = mapper;

        this.transactionsServiceUrl = "http://" + transactionServiceHost + ":" + transactionServicePort;
        this.positionServiceUrl = "http://" + positionServiceHost + ":" + positionServicePort;
        this.taxonomyServiceUrl = "http://" + taxonomyServiceHost + ":" + taxonomyServicePort + "/taxonomy";

    }

    @Override
    public List<Transaction> getTransactions(String instrument) {
        return getTransactions2(transactionsServiceUrl + "/transactions/" + instrument);
    }

    @Override
    public List<Transaction> getOpenTransactions(String instrument) {
        return getTransactions2(transactionsServiceUrl + "/open-transactions/" + instrument);
    }

    @Override
    public List<Transaction> getOpenTransactions() {
        return getTransactions2(transactionsServiceUrl + "/open-transactions");
    }

    List<Transaction> getTransactions2(String url) {
        logger.debug("Will call API on URL: {}", url);
        List<Transaction> result = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Transaction>>() {
        }).getBody();
        logger.debug("returning: {}", result);
        return result;
    }

    @Override
    public Transaction createTransaction(Transaction body) {
        try {
            String url = transactionsServiceUrl + "/transaction";
            logger.debug("Will post to URL: {}", url);

            Transaction transaction = restTemplate.postForObject(url, body, Transaction.class);
            logger.debug("Created with id: {}", transaction.getPositionId());

            return transaction;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteAll() {
        try {
            String url = transactionsServiceUrl + "/delete-all-transactions";
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Position2 getPosition(String instrument) {
        String url = positionServiceUrl + "/position/" + instrument;
        logger.debug("Will call API on URL: {}", url);
        Position2 result = restTemplate.getForObject(url, Position2.class);
        logger.debug("returning: {}", result);
        return result;
    }

    @Override
    public Position2 getOpenPosition(String instrument) {
        String url = positionServiceUrl + "/open-position/" + instrument;
        logger.debug("Will call API on URL: {}", url);
        Position2 result = restTemplate.getForObject(url, Position2.class);
        logger.debug("returning: {}", result);
        return result;
    }

    @Override
    public PositionGroup getOpenPositions() {
        String url = positionServiceUrl + "/open-positions";
        logger.debug("Will call API on URL: {}", url);
        PositionGroup result = restTemplate.getForObject(url, PositionGroup.class);
        logger.debug("returning: {}", result);
        return result;
    }

    @Override
    public PositionGroups getOpenPositionsGrouped() {
        String url = positionServiceUrl + "/open-positions-grouped";
        logger.debug("Will call API on URL: {}", url);
        PositionGroups result = restTemplate.getForObject(url, PositionGroups.class);
        logger.debug("returning: {}", result);
        return result;
    }

    @Override
    public PositionGroups getOpenPositionsGroupedBySector() {
        String url = positionServiceUrl + "/open-positions-grouped-bysector";
        logger.debug("Will call API on URL: {}", url);
        PositionGroups result = restTemplate.getForObject(url, PositionGroups.class);
        logger.debug("returning: {}", result);
        return result;
    }

    @Override
    public PositionGroups getOpenPositionsGroupedByIndustry() {
        String url = positionServiceUrl + "/open-positions-grouped-byindustry";
        logger.debug("Will call API on URL: {}", url);
        PositionGroups result = restTemplate.getForObject(url, PositionGroups.class);
        logger.debug("returning: {}", result);
        return result;
    }


    @Override
    public DefaultTaxonomy getTaxonomy(String name, String instrument) {
        String url = taxonomyServiceUrl + "/" + name + "/" + instrument;
        logger.debug("Will call getTaxonomy API on URL: {}", url);
        DefaultTaxonomy result = restTemplate.getForObject(url, DefaultTaxonomy.class);
        logger.debug("returning: {}", result);
        return result;
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));

            default:
                logger.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                logger.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
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
