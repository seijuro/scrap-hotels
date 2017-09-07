package com.github.seijuro.site.com.agoda.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class AgodaHotelReview {
    @Getter
    private final String reviewId;
    @Getter
    private final String hotelId;
    @Getter
    private final String postDate;
    @Getter
    private final String stayDate;
    @Getter
    private final String stayDays;
    @Getter
    private final String roomType;
    @Getter
    private final String score;
    @Getter
    private final String title;
    @Getter
    private final String locale;
    private final boolean hasResponse;
    @Getter
    private final String reviewerName;
    @Getter
    private final String country;

    public boolean hasResponse() {
        return hasResponse;
    }

    /**
     * Construct
     *
     * @param builder
     */
    protected AgodaHotelReview(Builder builder) {
        reviewId = builder.reviewId;
        hotelId =  builder.hotelId;
        postDate = builder.postDate;
        stayDate = builder.stayDate;
        stayDays = builder.stayDays;
        roomType = builder.roomType;
        score = builder.score;
        title = builder.title;
        locale = builder.locale;
        hasResponse = builder.hasResponse;
        reviewerName = builder.reviewerName;
        country = builder.country;
    }

    public static class Builder {
        @Setter
        private String reviewId = null;
        @Setter
        private String hotelId = null;
        @Setter
        private String postDate = StringUtils.EMPTY;
        @Setter
        private String stayDate = StringUtils.EMPTY;
        @Setter
        private String stayDays = StringUtils.EMPTY;
        @Setter
        private String roomType = StringUtils.EMPTY;
        @Setter
        private String score = StringUtils.EMPTY;
        @Setter
        private String title = StringUtils.EMPTY;
        @Setter
        private String locale = StringUtils.EMPTY;
        @Setter
        private boolean hasResponse = false;
        @Setter
        private String reviewerName = StringUtils.EMPTY;
        @Setter
        private String country = StringUtils.EMPTY;


        public AgodaHotelReview build() {
            return new AgodaHotelReview(this);
        }
    }
}
