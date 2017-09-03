package com.github.seijuro.site.com.expedia.query;

import lombok.Getter;

public enum Sort implements com.github.seijuro.search.query.Sort {
    RECOMMANED("추천", "recommended"),
    GUEST_RATING("고객 평점", "guestRating"),
    STAR_RATING("숙박 시설 등급", "starRating");

    @Getter
    private final String label;
    @Getter
    private final String sort;

    public String getQueryParameter() {
        return sort;
    }

    /**
     * Construct
     *
     * @param label
     * @param sort
     */
    Sort(String label, String sort) {
        this.label = label;
        this.sort = sort;
    }
}
