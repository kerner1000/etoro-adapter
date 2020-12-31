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

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class CompositeIntegration implements TransactionService, PositionService, TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(CompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String transactionsServiceUrl;
    private final String positionServiceUrl;
    private final String taxonomyServiceUrl;

    @Autowired
    CompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.transaction-service.host}") String transactionServiceHost,
            @Value("${app.transaction-service.port}") int transactionServicePort,

            @Value("${app.position-service.host}") String positionServiceHost,
            @Value("${app.position-service.port}") int positionServicePort,

            @Value("${app.taxonomy-service.host}") String taxonomyServiceHost,
            @Value("${app.taxonomy-service.port}") int taxonomyServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.transactionsServiceUrl = "http://" + transactionServiceHost + ":" + transactionServicePort;
        this.positionServiceUrl = "http://" + positionServiceHost + ":" + positionServicePort;
        this.taxonomyServiceUrl = "http://" + taxonomyServiceHost + ":" + taxonomyServicePort + "/taxonomy";

    }

    @Override
    public Set<String> getExchangeForTicker(String ticker) {


        if (ticker == null) {
            throw new IllegalArgumentException();
        }
        try {
            String url = taxonomyServiceUrl + "/" + ticker;
            logger.debug("Will call API on URL: {}", url);

            Set<String> exchanges = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Set<String>>() {
            }).getBody();

            logger.debug("returning: {}", exchanges);

            return exchanges;

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

    @Override
    public TransactionGroup getTransactions(String instrument) {
        try {
            String url = transactionsServiceUrl + "/transactions/" + instrument;
            logger.debug("Will call API on URL: {}", url);
            TransactionGroup result = restTemplate.getForObject(url, TransactionGroup.class);
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

    @Override
    public TransactionGroup getOpenTransactions(String instrument) {
        try {
            String url = transactionsServiceUrl + "/open-transactions/" + instrument;
            logger.debug("Will call API on URL: {}", url);
            TransactionGroup result = restTemplate.getForObject(url, TransactionGroup.class);
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

    @Override
    public TransactionGroup getOpenTransactions() {
        try {
            String url = positionServiceUrl + "/open-transactions";
            logger.debug("Will call API on URL: {}", url);

            TransactionGroup result = restTemplate.getForObject(url, TransactionGroup.class);
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

    @Override
    public Transaction createTransaction(Transaction body) {
        try {
            String url = transactionsServiceUrl + "/transaction";
            logger.debug("Will post to URL: {}", url);

            Transaction transaction = restTemplate.postForObject(url, body, Transaction.class);
            logger.debug("Created with id: {}", transaction.getTransactionId());

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
        try {
            String url = positionServiceUrl + "/position/" + instrument;
            logger.debug("Will call API on URL: {}", url);

            Position2 result = restTemplate.getForObject(url, Position2.class);
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

    @Override
    public Position2 getOpenPosition(String instrument) {
        try {
            String url = positionServiceUrl + "/open-position/" + instrument;
            logger.debug("Will call API on URL: {}", url);

            Position2 result = restTemplate.getForObject(url, Position2.class);
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

    @Override
    public PositionGroup getOpenPositions() {
        try {
            String url = positionServiceUrl + "/open-positions";
            logger.debug("Will call API on URL: {}", url);

            PositionGroup result = restTemplate.getForObject(url, PositionGroup.class);
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

    @Override
    public PositionGroups getOpenPositionsGrouped() {
        try {
            String url = positionServiceUrl + "/open-positions-grouped";
            logger.debug("Will call API on URL: {}", url);

            PositionGroups result = restTemplate.getForObject(url, PositionGroups.class);
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

    @Override
    public PositionGroups getOpenPositionsGroupedBySector() {
        try {
            String url = positionServiceUrl + "/open-positions-grouped-bysector";
            logger.debug("Will call API on URL: {}", url);

            PositionGroups result = restTemplate.getForObject(url, PositionGroups.class);
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

    @Override
    public PositionGroups getOpenPositionsGroupedByIndustry() {
        try {
            String url = positionServiceUrl + "/open-positions-grouped-byindustry";
            logger.debug("Will call API on URL: {}", url);

            PositionGroups result = restTemplate.getForObject(url, PositionGroups.class);
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

    @Override
    public DefaultTaxonomy getTaxonomy(String exchange, String identifier, String instrument) {
        try {
            String url = taxonomyServiceUrl + "/" + exchange + "/" + identifier + "/" + instrument;
            logger.debug("Will call getTaxonomy API on URL: {}", url);

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

    @Override
    public DefaultTaxonomy getTaxonomy(String name, String instrument) {
        try {
            String url = taxonomyServiceUrl + "/" + name + "/" + instrument;
            logger.debug("Will call getTaxonomy API on URL: {}", url);

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


}
