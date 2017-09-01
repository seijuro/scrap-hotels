package com.github.seijuro.site.com.hotels.result;

import com.google.gson.Gson;
import org.junit.Test;

public class RatePlanTest {
    @Test
    public void test() {
        final String testText1 = "{\"price\":{\"current\":\"₩75,339\",\"exactCurrent\":75339.0,\"old\":\"₩142,149\",\"fromPrice\":false},\"features\":{\"paymentPreference\":false,\"instalmentsMessage\":\"\",\"freeCancellation\":false},\"type\":\"EC\"}";
        final String testText2 = "{\"price\":{\"current\":\"₩72,001\",\"exactCurrent\":72001.0,\"old\":\"₩79,844\",\"fromPrice\":false},\"features\":{\"paymentPreference\":true,\"instalmentsMessage\":\"\",\"freeCancellation\":true},\"type\":\"EC\"}";
        final String testText3 = "{\"price\":{\"current\":\"₩72,001\",\"exactCurrent\":72001.0,\"old\":\"₩79,844\",\"fromPrice\":false},\"features\":{},\"type\":\"EC\"}";

        Gson gson = new Gson();
        RatePlan ratePlan1 = gson.fromJson(testText1, RatePlan.class);
        RatePlan ratePlan2 = gson.fromJson(testText2, RatePlan.class);
        RatePlan ratePlan3 = gson.fromJson(testText3, RatePlan.class);

        System.out.println(ratePlan1.toString());
        System.out.println(ratePlan2.toString());
        System.out.println(ratePlan3.toString());
    }
}
