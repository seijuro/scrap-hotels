package com.github.seijuro.site.com.agoda.query;

import lombok.Getter;

public enum Destination implements com.github.seijuro.search.query.Destination {
    SEOUL("14690", "서울"),
    JEJU("16901", "제주도"),
    BALLY("17193", "발리"),
    OAHU_HAWAI("513639", "오하우 하와이"),
    MAUI_HAWAI("513640", "마우이 하와이");

    @Getter
    private final String id;
    @Getter
    private final String label;

    @Override
    public String getQueryParameter() {
        return id;
    }

    Destination(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
