package com.github.seijuro.site.com.hotels.result;

import com.google.gson.Gson;
import org.junit.Test;

public class DealsTest {
    @Test
    public void test() {
        final String testText1 = "{\"greatRate\":true,\"priceReasoning\":\"GR\"}";
        final String testText2 = "{\"specialDeal\":{\"dealText\":\"조기 예약 시 5% 할인\"},\"priceReasoning\":\"DRR-448\"}";
        final String testText3 = "{\"specialDeal\":{\"dealText\":\"25% 할인\"},\"priceReasoning\":\"DRR-446\"}";

        Gson gson = new Gson();
        Deals deals1 = gson.fromJson(testText1, Deals.class);
        Deals deals2 = gson.fromJson(testText2, Deals.class);
        Deals deals3 = gson.fromJson(testText3, Deals.class);

        System.out.println(deals1.toString());
        System.out.println(deals2.toString());
        System.out.println(deals3.toString());
    }
}
