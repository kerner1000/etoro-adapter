package com.github.kerner1000.etoro.stats.taxonomy.yahoo;

import com.mashape.unirest.http.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class YahooAPI {

    private static final Logger logger = LoggerFactory.getLogger(YahooAPI.class);

    private APICalls apiCalls;

    private Map<String, JSONObject> summaryProfileCache;

    private Map<String, JSONObject> analysisCache;

    private Map<String, String> sectorCache;

    private Map<String, String> industryCache;

    private Map<String, String> nameShortCache;

    private Map<String, List<String>> tickerAliases;

    public YahooAPI(String apiKey) {
        summaryProfileCache = new LinkedHashMap<>();
        analysisCache = new LinkedHashMap<>();
        sectorCache = new LinkedHashMap<>();
        industryCache = new LinkedHashMap<>();
        nameShortCache = new LinkedHashMap<>();
        tickerAliases = new LinkedHashMap<>();
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
        sectorCache.put("XLM", "Crypto");
        industryCache.put("XLM", "Alts");
        nameShortCache.put("XLM", "XLM");

        //
//        sectorCache.put("MStanley", "Financial Services");
//        industryCache.put("MStanley", " Capital Markets");
//        nameShortCache.put("MStanley", "Morgan Stanley");

        putAlias("MStanley", "MS");

        putAlias("3800", "GCL Poly Energy");

//        putAlias("BTC", "BTC-USD");
//        putAlias("ETH", "ETH-USD");
    }

    private void putAlias(String ticker, String alias) {
        List<String> aliases = tickerAliases.get(ticker);
        if (aliases == null) {
            aliases = new ArrayList<>();
            tickerAliases.put(ticker, aliases);
        }
        aliases.add(alias);
    }

    public Optional<JSONObject> getJSONProfile(String ticker, String countryCode) {
        JSONObject summaryProfile = summaryProfileCache.get(ticker);
        if (summaryProfile == null) {
            List<String> tickers = new ArrayList<>();
            tickers.add(ticker);
            tickers.addAll(tickerAliases.getOrDefault(ticker, Collections.emptyList()));
            for (String t : tickers) {
                Optional<JsonNode> response = apiCalls.getSummary(t, countryCode);
                if (response.isPresent()) {
                    try {
                        summaryProfile = (JSONObject) response.get().getObject().get("summaryProfile");
                        summaryProfileCache.put(ticker, summaryProfile);
                        break;
                    } catch (JSONException e) {
                        logger.info("Did not find a profile for {}, reason: {}", ticker, e.getLocalizedMessage());
                    }
                }
            }
        }
        return Optional.ofNullable(summaryProfile);
    }

    public Optional<JSONObject> getJSONProfile(String ticker) {
        return getJSONProfile(ticker, null);
    }

    public String getProfile(String ticker, String identifier, String countryCode) {
        Optional<JSONObject> profile = getJSONProfile(ticker, countryCode);
        if (profile.isPresent()) {
            try {
                return profile.get().get(identifier).toString();
            } catch (JSONException e) {
                logger.info("Did not find a profile for {} and {}", ticker, identifier);
            }
        }
        return "na";
    }

    public String getProfile(String ticker, String identifier) {
        return getProfile(ticker, identifier, null);
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

    protected String getShortName2(String ticker) {
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
        return getSector(ticker, null);
    }

    public String getSector(String ticker, String countryCode) {
        String result = sectorCache.get(ticker);
        if (result == null) {
            result = getProfile(ticker, "sector", countryCode);
            sectorCache.put(ticker, result);
        }
        return result;
    }

    public String getIndustry(String ticker) {
        return getIndustry(ticker, null);
    }

    public String getIndustry(String ticker, String countryCode) {
        String result = industryCache.get(ticker);
        if (result == null) {
            result = getProfile(ticker, "industry", countryCode);
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
