package com.github.kerner1000.etoro.stats.taxonomy.prototypes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class APICallsProto {

    protected static final int DEFAULT_API_REST_TIME = 700;

    protected static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
}
