package com.github.kerner1000.etoro.stats.api;

import com.github.kerner1000.etoro.stats.model.DefaultTaxonomy;
import com.github.kerner1000.etoro.stats.model.Taxonomy;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MorningstarAPI {

    private static final Logger logger = LoggerFactory.getLogger(MorningstarAPI.class);

    Map<String, Set<String>> ticker2Exchanges = new LinkedHashMap<>();

    Map<String, String> companyNameForInstrument = new LinkedHashMap<>();

    Map<String, String> sectorForInstrument = new LinkedHashMap<>();

    Map<String, String> industryForInstrument = new LinkedHashMap<>();

    private List<String> exchangesCache = new ArrayList<>();

    private APICalls apiCalls;

    public MorningstarAPI(String apiKey) {
        apiCalls = new APICalls(apiKey);
        initCaches();
    }

    private void initCaches() {
        add2Ticker2Exchanges("BTC", "na");
        add2Ticker2Exchanges("ETH", "na");
        add2Ticker2Exchanges("LTC", "na");
        add2Ticker2Exchanges("XRP", "na");
        add2Ticker2Exchanges("BNB", "na");
        add2Ticker2Exchanges("Dash", "na");
        add2Ticker2Exchanges("SQ", "XNYS");
        sectorForInstrument.put("BTC", "Crypto");
        sectorForInstrument.put("ETH", "Crypto");
        sectorForInstrument.put("LTC", "Crypto");
        sectorForInstrument.put("XRP", "Crypto");
        sectorForInstrument.put("BNB", "Crypto");
        sectorForInstrument.put("Dash", "Crypto");
        sectorForInstrument.put("SQ", "Technology");
        industryForInstrument.put("BTC", "BTC");
        industryForInstrument.put("ETH", "ETH");
        industryForInstrument.put("LTC", "Alts");
        industryForInstrument.put("XRP", "Alts");
        industryForInstrument.put("BNB", "Alts");
        industryForInstrument.put("Dash", "Alts");
        industryForInstrument.put("SQ", "Software - Infrastructure\n");
    }

    private void add2Ticker2Exchanges(String ticker, String exchange) {
        Set<String> list = ticker2Exchanges.get(ticker);
        if (list == null) {
            list = new LinkedHashSet<>();
            ticker2Exchanges.put(ticker, list);
        }
        list.add(exchange);
    }

    public synchronized Set<String> findExchangeForTicker(String tickerName) {
        Set<String> result = ticker2Exchanges.get(tickerName);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        result = new LinkedHashSet<>();
        JsonNode searchResponse = apiCalls.search(tickerName);
        if (searchResponse != null) {
            JSONObject searchResponseObject = searchResponse.getObject();
            JSONArray results = (JSONArray) searchResponseObject.get("results");
            for (Object o : results) {
                JSONObject jo = (JSONObject) o;
                String tickerName2 = jo.get("ticker").toString();
                String micName = jo.get("mic").toString();
                if (tickerName.equals(tickerName2)) {
                    result.add(micName);
                }
            }
        }

        Set<String> result2 = new LinkedHashSet<>();

        if (result.contains("XNYS")) {
            result2.add("XNYS");
            result.remove("XNYS");
        }

        if (result.contains("XNAS")) {
            result2.add("XNAS");
            result.remove("XNAS");
        }

        result2.addAll(result);

        if (result2.isEmpty()) {
            // Mostly XNYS is correct. Let's give it a try.
            result2.add("XNYS");
        }

        ticker2Exchanges.put(tickerName, result2);

        return result2;
    }


    public synchronized List<String> getExchanges() {
        if (exchangesCache != null && !exchangesCache.isEmpty())
            return exchangesCache;

        JsonNode response = apiCalls.getExchanges();

        JSONArray results = (JSONArray) response.getObject().get("results");
        for (Object o : results) {
            exchangesCache.add(o.toString());
        }

        return exchangesCache;
    }

    public synchronized Taxonomy extractValueFromCompanyProfile(String instrument, String valueKey, String mic) {
        logger.debug("Querying {} on {}", instrument, mic);


        JsonNode response = apiCalls.getCompanyProfile(instrument, mic);


        JSONObject result = (JSONObject) response.getObject().get("result");
        JSONObject sector = (JSONObject) result.get(valueKey);
        String sectorValue = sector.get("value").toString();
        logger.debug("Found {} for {} on {}", sectorValue, instrument, mic);

        return new DefaultTaxonomy(valueKey, sectorValue, instrument);


    }

    private Map<String, JsonNode> companyProfileCache = new LinkedHashMap<>();

    public synchronized String extractValueFromCompanyProfile2(String instrument, String valueKey, String mic) {
        logger.debug("Querying {} on {}", instrument, mic);

        JsonNode response = companyProfileCache.get(instrument);

        if (response == null) {
            response = apiCalls.getCompanyProfile(instrument, mic);
            companyProfileCache.put(instrument, response);
        }

        if (response != null) {
            JSONObject result = (JSONObject) response.getObject().get("result");
            JSONObject sector = (JSONObject) result.get(valueKey);
            String value = sector.get("value").toString();
            logger.debug("Found {} for {} on {}", value, instrument, mic);

            return value;
        }
        return null;


    }

    public synchronized String findName(String instrument) {

        Set<String> exchange = findExchange(instrument);
        if (exchange != null && !exchange.isEmpty() && !"na".equals(exchange.iterator().next())) {
            logger.debug("Looking for name for {} on {}", instrument, exchange);
            String result = getCompanyName(instrument, exchange.iterator().next());
            if (result != null && !"na".equals(result)) {
                logger.debug("Found {} on {}, name is '{}'", instrument, exchange, result);
                return result;
            }
        }

        logger.debug("Failed to find name for {}", instrument);
        return "na";
    }


    public synchronized String getCompanyName(String instrument, String exchange) {
        String name = companyNameForInstrument.get(instrument);
        if (name == null) {
            JSONArray results = apiCalls.getCompaniesByExchange(exchange);
            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                String ticker = obj.getString("ticker");
                String companyName = obj.getString("companyName");
                if (ticker != null && ticker.equals(instrument.toUpperCase())) {
                    companyNameForInstrument.put(instrument, companyName);
                    logger.debug("Found '{}' for instrument '{}'", name, instrument);
                    return companyName;
                }
            }
        }
        if (name == null) {
            logger.warn("Could not find name for {}", instrument);
            sectorForInstrument.put(instrument, "na");
            return "na";
        }
        logger.debug("Loaded '{}' from cache for instrument '{}'", name, instrument);
        return name;
    }

    public synchronized String findSector(String instrument) {
        String result1 = sectorForInstrument.get(instrument);
        if (result1 == null) {
            logger.info("Querying sector for {}", instrument);
            Set<String> exchange = findExchange(instrument);
            if (exchange != null && !exchange.isEmpty() && !"na".equals(exchange.iterator().next())) {

//                    logger.debug("Looking for {} on {}", instrument, exchange);
                String result = getSectorOnExchange(instrument, exchange.iterator().next());
                if (result != null && !"na".equals(result)) {
                    logger.debug("Found {} on {}, value is '{}'", instrument, exchange, result);
                    sectorForInstrument.put(instrument, result);
                    return result;
                }

            }
        }
        if (result1 == null) {
            logger.warn("Could not find sector for {}", instrument);
            sectorForInstrument.put(instrument, "na");
            return "na";
        }
        logger.debug("Loaded '{}' from cache for instrument '{}'", result1, instrument);
        return result1;
    }

    public synchronized String findIndustry(String instrument) {
        String result1 = industryForInstrument.get(instrument);
        if (result1 == null) {
            logger.info("Querying industry for {}", instrument);
            Set<String> exchange = findExchange(instrument);
            if (exchange != null && !exchange.isEmpty() && !"na".equals(exchange.iterator().next())) {

                logger.debug("Looking for industry for {} on {}", instrument, exchange);
                String result = getIndustryOnExchange(instrument, exchange.iterator().next());
                if (result != null && !"na".equals(result)) {
                    logger.debug("Found {} on {}, value is '{}'", instrument, exchange, result);
                    industryForInstrument.put(instrument, result);
                    return result;
                }

            }
        }
        if (result1 == null) {
            logger.warn("Could not find industry for {}", instrument);
            industryForInstrument.put(instrument, "na");
            return "na";
        }
        logger.debug("Loaded '{}' from cache for instrument '{}'", result1, instrument);
        return result1;
    }

    public Set<String> findExchange(String instrument) {

        Set<String> result = ticker2Exchanges.get(instrument);
        if (result == null || result.isEmpty()) {
            logger.info("Querying exchange for {}", instrument);
            result = findExchangeForTicker(instrument);
        }
        if (result == null || result.isEmpty()) {
            result = new LinkedHashSet<>();
            logger.warn("Could not find {} on any exchange", instrument);
            add2Ticker2Exchanges(instrument, "na");
            result.add("na");
            companyNameForInstrument.put(instrument, "na");
        }
        logger.debug("Loaded '{}' from cache for instrument '{}'", result, instrument);
        return result;
    }

    public synchronized String getSectorOnExchange(String instrument, String exchange) {
        return extractValueFromCompanyProfile2(instrument, "sector", exchange);
    }

    public synchronized String getIndustryOnExchange(String instrument, String exchange) {
        return extractValueFromCompanyProfile2(instrument, "industry", exchange);
    }

    public void addExchange(String instrument, String exchange) {
        add2Ticker2Exchanges(instrument, exchange);

    }
}
