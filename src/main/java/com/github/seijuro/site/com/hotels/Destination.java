package com.github.seijuro.site.com.hotels;

import lombok.Getter;

public enum Destination {
    SEOUL("759818", "%EC%84%9C%EC%9A%B8,+%ED%95%9C%EA%B5%AD", "CITY:759818:UNKNOWN:UNKNOWN", "서울"),
    JEJU("1644457", "%EC%A0%9C%EC%A3%BC%EB%8F%84,+%ED%95%9C%EA%B5%AD", "CITY:1644457:UNKNOWN:UNKNOWN", "제주"),
    BALLY("1633553", "%EB%B0%9C%EB%A6%AC,+%EC%9D%B8%EB%8F%84%EB%84%A4%EC%8B%9C%EC%95%84", "CITY:1633553:UNKNOWN:UNKNOWN", "발리"),
    HAWAI("", "", "", "하와이");

    /**
     * Instance Properties
     */
    @Getter
    private final String id;
    @Getter
    private final String query;
    @Getter
    private final String location;
    @Getter
    private final String text;

    /**
     * C'tor
     *
     * @param id
     * @param query
     * @param location
     * @param text
     */
    Destination(String id, String query, String location, String text) {
        this.id = id;
        this.query = query;
        this.location = location;
        this.text = text;
    }
}