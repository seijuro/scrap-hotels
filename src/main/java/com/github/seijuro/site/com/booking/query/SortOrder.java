package com.github.seijuro.site.com.booking.query;

import lombok.Getter;

public enum SortOrder implements com.github.seijuro.search.SortOrder {
    RECOMMAND("저희가 추천하는 숙소", "popularity"),
    REVIEW_SCORE_AND_PRICE("후기 평점 + 요금 순", "review_score_and_price"),
    CLASS_DESC("성급/호텔 등급 [5->1]", "class"),
    CLASS_ASC("성급/호텔 등급 [1->5]", "class_asc"),
    SCORE("이용 후기 평점", "score");

    /**
     * Instance Properties
     */
    @Getter
    private final String text;
    @Getter
    private final String value;

    /**
     * Construct
     *
     * @param text
     * @param value
     */
    SortOrder(String text, String value) {
        this.text = text;
        this.value = value;
    }

}
