package com.github.kerner1000.etoro.stats.listpositionsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.model.Transaction;
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
import java.util.Objects;

@Component
public class PositionIntegration implements TransactionService, TaxonomyService {

    private static final Logger logger = LoggerFactory.getLogger(PositionIntegration.class);

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    private final ObjectMapper mapper;

    private final String transactionsServiceUrl;

    private final String taxonomyServiceUrl;

    @Autowired
    PositionIntegration(
            RestTemplate restTemplate,
            WebClient.Builder webclient,
            ObjectMapper mapper,

            @Value("${app.transactions-service.host}") String transactionsServiceHost,
            @Value("${app.transactions-service.port}") int transactionServicePort,

            @Value("${app.taxonomy-service.host}") String taxonomyServiceHost,
            @Value("${app.taxonomy-service.port}") int taxonomyServicePort

    ) {
        this.restTemplate = restTemplate;

        this.webClient = webclient.build();

        this.mapper = mapper;

        this.transactionsServiceUrl = "http://" + transactionsServiceHost + ":" + transactionServicePort;

        this.taxonomyServiceUrl = "http://" + taxonomyServiceHost + ":" + taxonomyServicePort;

    }


    @Override
    public List<Transaction> getTransactions(String instrument) {

        String url = transactionsServiceUrl + "/transactions/" + instrument;
        logger.debug("Will call API on URL: {}", url);
        List<Transaction> result = restTemplate.exchange(url, HttpMethod.GET,null, new ParameterizedTypeReference<List<Transaction>>() {}).getBody();
        logger.debug("returning: {}", result);
        return result;

    }

    @Override
    public List<Transaction> getOpenTransactions(String instrument) {

        String url = transactionsServiceUrl + "/open-transactions/" + instrument;
        logger.debug("Will call API on URL: {}", url);
        List<Transaction> result = restTemplate.exchange(url, HttpMethod.GET,null, new ParameterizedTypeReference<List<Transaction>>() {}).getBody();
        logger.debug("returning: {}", result);
        return result;


    }

    @Override
    public List<Transaction> getOpenTransactions() {

        String url = transactionsServiceUrl + "/open-transactions";
        logger.debug("Will call API on URL: {}", url);
        List<Transaction> result = restTemplate.exchange(url, HttpMethod.GET,null, new ParameterizedTypeReference<List<Transaction>>() {}).getBody();
        logger.debug("returning: {}", result);
        return result;

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
        if (Objects.requireNonNull(instrument).contains("/")) {
            return instrument.substring(0, instrument.indexOf("/")).trim();
        }
        return instrument;
    }


    @Override
    public DefaultTaxonomy getTaxonomy(String identifier, String instrument) {

        String url = taxonomyServiceUrl + "/taxonomy/" + identifier + "/" + extractTicker(instrument);
        logger.debug("Will call API on URL: {}", url);

        DefaultTaxonomy result = restTemplate.getForObject(url, DefaultTaxonomy.class);

        logger.debug("returning: {}", result);

        return result;


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
