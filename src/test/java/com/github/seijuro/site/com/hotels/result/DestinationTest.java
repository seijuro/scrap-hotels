package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.data.Destination;
import com.google.gson.Gson;
import org.junit.Test;

public class DestinationTest {
    @Test
    public void test() {
        final String testText = "{\"id\":\"759818\",\"value\":\"Seoul, South Korea\",\"shortName\":\"Seoul\",\"resolvedLocation\":\"CITY:759818:UNKNOWN:UNKNOWN\",\"destinationType\":\"CITY\"}";

        Gson gson = new Gson();
        Destination destination = gson.fromJson(testText, Destination.class);

        System.out.println(destination.toString());
    }
}
