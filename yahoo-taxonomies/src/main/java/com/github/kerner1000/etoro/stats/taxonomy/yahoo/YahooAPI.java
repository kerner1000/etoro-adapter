package com.github.kerner1000.etoro.stats.taxonomy.yahoo;

import com.mashape.unirest.http.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class YahooAPI {

    private static final Logger logger = LoggerFactory.getLogger(YahooAPI.class);

    private APICalls apiCalls;

    private Map<String, JSONObject> summaryProfileCache;

    private Map<String, JSONObject> analysisCache;

    private Map<String, String> sectorCache;

    private Map<String, String> industryCache;

    private Map<String, String> nameShortCache;

    public YahooAPI(String apiKey) {
        summaryProfileCache = new LinkedHashMap<>();
        analysisCache = new LinkedHashMap<>();
        sectorCache = new LinkedHashMap<>();
        industryCache = new LinkedHashMap<>();
        nameShortCache = new LinkedHashMap<>();
        apiCalls = new APICalls(apiKey);
        initCaches();
    }

    private void initCaches() {
        // Crypto
        sectorCache.put("BTC", "Crypto");
        industryCache.put("BTC", "BTC");
        nameShortCache.put("BTC", "Bitcoin");
        sectorCache.put("ETH", "Crypto");
        industryCache.put("ETH", "ETH");
        nameShortCache.put("ETH", "Etherium");
        sectorCache.put("LTC", "Crypto");
        industryCache.put("LTC", "Alts");
        nameShortCache.put("LTC", "Litecoin");
        sectorCache.put("XRP", "Crypto");
        industryCache.put("XRP", "Alts");
        nameShortCache.put("XRP", "XRP");
        sectorCache.put("BNB", "Crypto");
        industryCache.put("BNB", "Alts");
        nameShortCache.put("BNB", "Binance Coin");
        sectorCache.put("BCH", "Crypto");
        industryCache.put("BCH", "Alts");
        nameShortCache.put("BCH", "Bitcoin Cash");
        sectorCache.put("NEO", "Crypto");
        industryCache.put("NEO", "Alts");
        nameShortCache.put("NEO", "NEO");
        //
        sectorCache.put("MStanley", "Financial Services");
        industryCache.put("MStanley", " Capital Markets");
        nameShortCache.put("MStanley", "Morgan Stanley");
    }

    public Optional<JSONObject> getProfile(String ticker) {
        JSONObject summaryProfile = summaryProfileCache.get(ticker);
        if (summaryProfile == null) {
            Optional<JsonNode> response = apiCalls.getSummary(ticker);
            if (response.isPresent()) {
                try {
                    summaryProfile = (JSONObject) response.get().getObject().get("summaryProfile");
                    summaryProfileCache.put(ticker, summaryProfile);
                } catch (JSONException e) {
                    logger.info("Did not find a profile for {}", ticker);
                }
            }
        }
        return Optional.ofNullable(summaryProfile);
    }

    public String getProfile(String ticker, String identifier) {
        Optional<JSONObject> profile = getProfile(ticker);
        if (profile.isPresent()) {
            try {
                return profile.get().get(identifier).toString();
            } catch (JSONException e) {
                logger.info("Did not find a profile for {} and {}", ticker, identifier);
            }
        }
        return "na";
    }

    public Optional<JSONObject> getQuoteType(String ticker) {
        JSONObject analysis = analysisCache.get(ticker);
        if (analysis == null) {
            Optional<JsonNode> response = apiCalls.getAnalysis(ticker);
            if (response.isPresent()) {
                try {
                    analysis = (JSONObject) response.get().getObject().get("quoteType");
                    analysisCache.put(ticker, analysis);
                } catch (JSONException e) {
                    logger.info("Did not find an analysis for {}", ticker);
                }
            }
        }
        return Optional.ofNullable(analysis);
    }

    public String getShortName2(String ticker) {
        Optional<JSONObject> profile = getQuoteType(ticker);
        if (profile.isPresent()) {
            try {
                return profile.get().get("shortName").toString();
            } catch (JSONException e) {
                logger.info("Did not find an analysis for {}", ticker);
            }
        }
        return "na";
    }

    public String getSector(String ticker) {
        String result = sectorCache.get(ticker);
        if (result == null) {
            result = getProfile(ticker, "sector");
            sectorCache.put(ticker, result);
        }
        return result;
    }

    public String getIndustry(String ticker) {
        String result = industryCache.get(ticker);
        if (result == null) {
            result = getProfile(ticker, "industry");
            industryCache.put(ticker, result);
        }
        return result;
    }

    public String getShortName(String ticker) {
        String result = nameShortCache.get(ticker);
        if (result == null) {
            result = getShortName2(ticker);
        }
        return result;
    }
}
