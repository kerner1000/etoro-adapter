package com.github.kerner1000.etoro.stats.api;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests without cache
 */
class MorningstarAPITest {

    private MorningstarAPI api;

    private static Properties prop;

    @BeforeAll
    public static void loadProperties() {
        try (InputStream input = MorningstarAPITest.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() {
        api = new MorningstarAPI(prop.getProperty("apikey"));

    }

    @AfterEach
    public void tearDown() {
        api = null;
    }


    @Test
    void testListExchanges() {
        List<String> result = api.getExchanges();
        assertFalse(result.isEmpty());
    }

    @Test
    void findExchangeForTicker() {
        Set<String> exchange = api.findExchangeForTicker("TSLA");
        assertEquals("XNYS",exchange.iterator().next());
    }

    @Test
    void findExchangeForTicker02() {
        Set<String> exchange = api.findExchangeForTicker("PLTR");
        assertEquals("XNYS",exchange.iterator().next());
    }

    @Test
    void testFindName() {
        String result = api.findName("TSLA");
        assertEquals("Tesla Inc", result);
    }

    @Test
    void testFindSector() {
        String result = api.findSector("AAPL");
        assertEquals("Technology", result);
    }

    @Test
    void testFindIndustry() {
        String result = api.findIndustry("AAPL");
        assertEquals("Consumer Electronics", result);
    }
}