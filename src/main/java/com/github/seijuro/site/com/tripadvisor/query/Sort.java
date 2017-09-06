package com.github.seijuro.site.com.tripadvisor.query;

import lombok.Getter;

public enum Sort implements com.github.seijuro.search.query.Sort {
    POPULARITY("popularity", "여행자 평가"),
    RECOMMENDED("recommended", "가성비 최고"),
    PRICE_LOW("priceLow", "최저가"),
    DIST_LOW("distLow", "거리");

    @Getter
    private final String queryParameter;
    @Getter
    private final String label;

    Sort(String query, String label) {
        this.queryParameter = query;
        this.label = label;
    }
}

