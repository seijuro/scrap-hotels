package com.github.seijuro.site.com.agoda.query;

import lombok.Getter;

public enum Sort implements com.github.seijuro.search.query.Sort {
    RECOMMEND("1", "agodaRecommended", "추천 상품"),
    SCRETE_DEALS("1", "secretDeals", "특가상품(아고다 시크릿 상품)"),
    STAR_DESC("1", "star5To1", "성급"),
    REVIEW("1", "reviewAll", "투숙객 평가")
    ;

    @Getter
    private final String id;
    @Getter
    private final String queryParameter;
    @Getter
    private final String label;

    Sort(String id, String query, String label) {
        this.id = id;
        this.queryParameter = query;
        this.label = label;
    }
}
