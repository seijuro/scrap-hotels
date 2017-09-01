package com.github.seijuro.site.com.hotels;

import lombok.Getter;

public enum SortOrder implements com.github.seijuro.search.SortOrder {
    BEST_SELLER("BEST_SELLER", "최고인기"),
    STAR_RATING_HIGHEST_FIRST("STAR_RATING_HIGHEST_FIRST", "호텔등급"),
    GUEST_RATING("GUEST_RATING", "고객평점");

    /**
     * Instance Properties
     */
    @Getter
    private final String text;
    @Getter
    private final String value;

    /**
     * C'tor
     *
     * @param value
     * @param text
     */
    SortOrder(String value, String text) {
        this.value = value;
        this.text = text;
    }
}