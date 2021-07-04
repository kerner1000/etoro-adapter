package com.github.kerner1000.etoro.stats.taxonomy.yahoo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YahooAPITest {

    private YahooAPI api;

    private static Properties prop;

    @BeforeAll
    public static void loadProperties() {
        try (InputStream input = YahooAPITest.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() {
        api = new YahooAPI(prop.getProperty("apikey"));

    }

    @AfterEach
    public void tearDown() {
        api = null;
    }

    @Test
    public void testGetSector01() {
        String sector = api.getSector("TSLA");
        assertEquals("Consumer Cyclical", sector);
    }

    @Test
    public void testGetIndustry01() {
        String sector = api.getIndustry("TSLA");
        assertEquals("Auto Manufacturers", sector);
    }

    @Test
    public void testGetIndustry03() {
        String sector = api.getIndustry("TSLA", "US");
        assertEquals("Auto Manufacturers", sector);
    }

    @Test
    public void testGetIndustryWhitespace() throws UnsupportedEncodingException {
        String sector = api.getIndustry("3800.HK");
        assertEquals("Solar", sector);
    }

    @Test
    public void testGetShortName01() {
        String sector = api.getShortName("TSLA");
        assertEquals("Tesla, Inc.", sector);
    }
}
