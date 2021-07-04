package com.github.kerner1000.etoro.stats.taxonomy.yahoo;

import com.github.kerner1000.etoro.stats.taxonomy.prototypes.APICallsProto;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public class APICalls extends APICallsProto {

    private static final Logger logger = LoggerFactory.getLogger(APICalls.class);

    private final String apiKey;

    public APICalls(String apiKey) {
        this.apiKey = apiKey;
    }

    public Optional<JsonNode> getSummary(List<String> tickers) {
        for(String ticker : tickers){
            Optional<JsonNode> result = getSummary(ticker);
            if(result.isPresent()){
                return result;
            }
        }
        return Optional.empty();
    }

    public Optional<JsonNode> getSummary(String ticker) {
        return getSummary(ticker, null);
    }

    public Optional<JsonNode> getSummary(String ticker, String countryCode) {
        try {
            // we need to give the API some rest
            Thread.sleep(DEFAULT_API_REST_TIME);
            String urlString = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?symbol="  + APICallsProto.encodeValue(ticker) + (countryCode == null ? "" : "&region=" + countryCode);
            HttpResponse<JsonNode> response = Unirest.get(urlString)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                    .asJson();
            if (response.getStatus() == 200) {
                return Optional.of(response.getBody());
            } else {
                logger.error("Got unexpected return code from response {}, {}", response.getStatus(), response.getStatusText());
            }
        } catch (UnirestException | InterruptedException | UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<JsonNode> getAnalysis(String ticker) {
        return getAnalysis(ticker, null);
    }

    public Optional<JsonNode> getAnalysis(String ticker, String countryCode) {
        try {
            // we need to give the API some rest
            Thread.sleep(DEFAULT_API_REST_TIME);
            HttpResponse<JsonNode> response = Unirest.get("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-analysis?symbol=" + APICallsProto.encodeValue(ticker) + (countryCode == null ? "" : "&region=" + countryCode))
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                    .asJson();
            if (response.getStatus() == 200) {
                return Optional.of(response.getBody());
            } else {
                logger.error("Got unexpected return code from response {}, {}", response.getStatus(), response.getStatusText());
            }
        } catch (UnirestException | InterruptedException | UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }
}
