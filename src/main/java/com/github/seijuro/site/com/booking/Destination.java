package com.github.seijuro.site.com.booking;

import lombok.Getter;

public enum  Destination implements com.github.seijuro.search.Destination {
    SEOUL("-716583", "%EC%84%9C%EC%9A%B8", "city", "서울"),
    JEJU("4170", "%EC%A0%9C%EC%A3%BC%ED%8A%B9%EB%B3%84%EC%9E%90%EC%B9%98%EB%8F%84", "region", "제주"),
    BALLY("", null, null, "발리"),
    HAWAI("", "", "", "하와이");

    //  ss=&ssne_untouched=%EC%84%9C%EC%9A%B8
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
