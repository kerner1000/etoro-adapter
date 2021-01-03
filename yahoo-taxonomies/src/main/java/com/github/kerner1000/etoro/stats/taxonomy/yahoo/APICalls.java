package com.github.kerner1000.etoro.stats.taxonomy.yahoo;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kerner1000.etoro.stats.taxonomy.prototypes.APICallsProto;

import java.util.Optional;

public class APICalls extends APICallsProto {

    private static final Logger logger = LoggerFactory.getLogger(APICalls.class);

    private final String apiKey;

    public APICalls(String apiKey) {
        this.apiKey = apiKey;
    }

    public Optional<JsonNode> getSummary(String ticker) {
        try {
            // we need to give the API some rest
            Thread.sleep(DEFAULT_API_REST_TIME);
            HttpResponse<JsonNode> response = Unirest.get("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?symbol=" + ticker)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                    .asJson();
            if (response.getStatus() == 200) {
                return Optional.of(response.getBody());
            } else {
                logger.error("Got unexpected return code from response {}, {}", response.getStatus(), response.getStatusText());
            }
        } catch (UnirestException | InterruptedException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<JsonNode> getAnalysis(String ticker) {
        try {
            // we need to give the API some rest
            Thread.sleep(DEFAULT_API_REST_TIME);
            HttpResponse<JsonNode> response = Unirest.get("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-analysis?symbol=" + ticker)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                    .asJson();
            if (response.getStatus() == 200) {
                return Optional.of(response.getBody());
            } else {
                logger.error("Got unexpected return code from response {}, {}", response.getStatus(), response.getStatusText());
            }
        } catch (UnirestException | InterruptedException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }
}
