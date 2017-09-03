package com.github.seijuro.site.com.booking.query;

import lombok.Getter;

import java.net.URLEncoder;

public enum Destination implements com.github.seijuro.search.query.Destination {
    SEOUL("-716583", "서울", "city", "서울"),
    JEJU("4170", "제주", "region", "제주도"),
    BALLY("", "발리", null, "발리"),
    HAWAI("", "하와이", "", "하와이");

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

    @Override
    public String getLabel() {
        return text;
    }

    @Override
    public String getQueryParameter() {
        try {
            return URLEncoder.encode(query, "UTF-8");
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return query;
    }

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
