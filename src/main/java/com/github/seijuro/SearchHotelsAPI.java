package com.github.seijuro;

import com.github.seijuro.http.rest.RestfulAPI;
import lombok.Getter;

public class SearchHotelsAPI extends RestfulAPI {
    @Getter
    public static String BaseURL = "https://kr.hotels.com/search/listings.json";

    /**
     * C'tor
     *
     * @param url
     */
    public SearchHotelsAPI(String url) {
        super(RequestMethod.GET, url);

        setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        setRequestProperty("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
        setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
    }
}
