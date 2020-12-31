package com.github.kerner1000.etoro.stats.listpositionsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.model.Taxonomy;
import com.github.kerner1000.etoro.stats.model.Transaction;
import com.github.kerner1000.etoro.stats.model.TransactionGroup;
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
import java.util.Objects;
import java.util.Set;

@Component
public class CompositeIntegration implements TransactionService, TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(CompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String transactionsServiceUrl;

    private final String taxonomyServiceUrl;

    @Autowired
    CompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.transactions-service.host}") String transactionsServiceHost,
            @Value("${app.transactions-service.port}") int    transactionServicePort,

            @Value("${app.taxonomy-service.host}") String taxonomyServiceHost,
            @Value("${app.taxonomy-service.port}") int    taxonomyServicePort

    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.transactionsServiceUrl = "http://" + transactionsServiceHost + ":" + transactionServicePort;

        this.taxonomyServiceUrl = "http://" + taxonomyServiceHost + ":" + taxonomyServicePort;

    }

    @Override
    public Set<String> getExchangeForTicker(String ticker) {
        if(ticker == null){
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
    public TransactionGroup getTransactions(String instrument) {
        try {
            String url = transactionsServiceUrl  + "/transactions/" + instrument;
            logger.debug("Will call API on URL: {}", url);
            TransactionGroup result = restTemplate.getForObject(url, TransactionGroup.class);
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
    public TransactionGroup getOpenTransactions(String instrument) {
        try {
            String url = transactionsServiceUrl  + "/open-transactions/" + instrument;
            logger.debug("Will call API on URL: {}", url);
            TransactionGroup result = restTemplate.getForObject(url, TransactionGroup.class);
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
    public TransactionGroup getOpenTransactions() {
        try {
            String url = transactionsServiceUrl  + "/open-transactions";
            logger.debug("Will call API on URL: {}", url);

            TransactionGroup result = restTemplate.getForObject(url, TransactionGroup.class);
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
    public Transaction createTransaction(Transaction body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }


    static String extractTicker(String instrument) {
        if (Objects.requireNonNull(instrument).contains(".")) {
            return instrument.substring(0, instrument.indexOf(".")).trim();
        }
        return instrument.substring(0, instrument.indexOf("/")).trim();
    }

    @Override
    public DefaultTaxonomy getTaxonomy(String exchange, String identifier, String instrument) {
        try {
            String url = taxonomyServiceUrl  + "/taxonomy/" + exchange + "/" + identifier + "/" + extractTicker(instrument);
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
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {
        try {
            String url = taxonomyServiceUrl  + "/taxonomy/" + identifier + "/" + extractTicker(instrument);
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
}
