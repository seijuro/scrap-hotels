package com.github.seijuro.site.common.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
public abstract class HotelReview extends Review {
    @Getter
    private final String hotelId;

    /**
     * Construct <code>HotelReview</code>.
     *
     * @param builder
     */
    protected HotelReview(Builder builder) {
        super(builder);

        hotelId = builder.hotelId;
    }

    /**
     * Builder Pattner class
     * extends <code>Review.Builder</code>.
     */
    public static abstract class Builder extends Review.Builder {
        @Setter
        private String hotelId = null;

        @Override
        public abstract HotelReview build();
    }
}
