package com.github.seijuro.site.com.hotels.result;

import com.google.gson.Gson;
import org.junit.Test;

public class PaginationTest {
    @Test
    public void test() {
        final String testText = "{\"currentPage\":2,\"pageGroup\":\"EXPEDIA_IN_POLYGON\",\"nextPageUrl\":\"?destination-id\\u003d759818\\u0026q-check-out\\u003d2017-11-16\\u0026q-destination\\u003dSeoul,+South+Korea\\u0026q-room-0-adults\\u003d2\\u0026pg\\u003d1\\u0026q-rooms\\u003d1\\u0026start-index\\u003d20\\u0026q-check-in\\u003d2017-11-15\\u0026resolved-location\\u003dCITY:759818:UNKNOWN:UNKNOWN\\u0026q-room-0-children\\u003d0\\u0026pn\\u003d3\"}";

        Gson gson = new Gson();
        Pagination pagination = gson.fromJson(testText, Pagination.class);

        System.out.println(pagination.toString());
    }
}
