package com.github.seijuro.site.com.tripadvisor.query;

import lombok.Getter;

public enum Lodging implements com.github.seijuro.search.query.Lodging {
    HOTEL("1", "호텔", "HOTEL"),
    RESORT("2", "모텔 / B&B", "BB"),
    OTHER("3", "기타 숙박시설", "OTHER"),
    SPECIAL_OFFERS("4", "스폐셜 프로모션 호텔", "SpecialOffers");

    @Getter
    private final String id;
    @Getter
    private final String label;
    @Getter
    private final String queryParameter;

    Lodging(String id, String label, String param) {
        this.id = id;
        this.label = label;
        this.queryParameter = param;
    }
}

