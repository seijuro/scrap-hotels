package com.github.seijuro.site.com.tripadvisor.query;

import lombok.Getter;

public enum Destination implements com.github.seijuro.search.query.Destination {
    SEOUL("g294197", "서울", "Seoul"),
    JEJU("g983296", "제주도", "Jeju_Island"),
    BALLY("g294226", "발리", "Bali"),
    HAWAII("g28932", "하와이", "Hawaii");

    @Getter
    private final String id;
    @Getter
    private final String label;
    @Getter
    private final String queryParameter;

    Destination(String id, String label, String param) {
        this.id = id;
        this.label = label;
        this.queryParameter = param;
    }
}
