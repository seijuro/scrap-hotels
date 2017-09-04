package com.github.seijuro.site.com.agoda.query;

import com.github.seijuro.search.query.QueryParameter;
import lombok.Getter;

public enum Lodging implements com.github.seijuro.search.query.Lodging {
    HOTEL("34", "호텔"),
    RESORT("122", "리조트 빌라");

    @Getter
    private final String id;
    @Getter
    private final String label;

    @Override
    public String getQueryParameter() {
        return id;
    }

    Lodging(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
