package com.github.seijuro.site.com.hotels.result;

import com.google.gson.Gson;
import org.junit.Test;

public class BadgingTest {
    @Test
    public void test() {
        final String testText1 = "{\"hotelBadge\":{\"type\":\"topHotel\",\"label\":\"인기 호텔\"}}";
        final String testText2 = "{\"hotelBadge\":{\"type\":\"lovedByGuests\",\"label\":\"고객 사랑\"}}";
        final String testText3 = "{\"hotelBadge\":{\"type\":\"lovedByGuests\",\"label\":\"고객 사랑\",\"tooltipTitle\":\"2017 골드 어워드 수상 호텔!\",\"tooltipText\":\"이 숙박 시설은 저희 고객들로부터 꾸준히 '탁월함으로 최고 평점을 받앗습니다\"}}";

        Gson gson = new Gson();
        Badging badging1 = gson.fromJson(testText1, Badging.class);
        Badging badging2 = gson.fromJson(testText2, Badging.class);
        Badging badging3 = gson.fromJson(testText3, Badging.class);

        System.out.println(badging1.toString());
        System.out.println(badging2.toString());
        System.out.println(badging3.toString());
    }
}
