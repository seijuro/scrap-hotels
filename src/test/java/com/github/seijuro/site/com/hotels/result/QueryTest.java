package com.github.seijuro.site.com.hotels.result;

import com.google.gson.Gson;
import org.junit.Test;

public class QueryTest {
    @Test
    public void test() {
        final String testText = "{\"destination\":{\"id\":\"759818\",\"value\":\"Seoul, South Korea\",\"shortName\":\"Seoul\",\"resolvedLocation\":\"CITY:759818:UNKNOWN:UNKNOWN\",\"destinationType\":\"CITY\"}}";

        Gson gson = new Gson();
        Query query = gson.fromJson(testText, Query.class);

        System.out.println(query.toString());
    }
}
