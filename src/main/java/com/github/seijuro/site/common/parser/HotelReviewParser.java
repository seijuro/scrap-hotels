package com.github.seijuro.site.common.parser;

import com.github.seijuro.site.common.data.HotelReview;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

public abstract class HotelReviewParser {
    @Getter
    private final String hotelId;

    /**
     * Construct <code>HotelReviewParser</code>
     *
     * @param hotelId
     */
    public HotelReviewParser(String hotelId) {
        Objects.requireNonNull(hotelId);

        this.hotelId = hotelId;
    }

    public abstract <T extends HotelReview> List<T> parse(String html);
}
