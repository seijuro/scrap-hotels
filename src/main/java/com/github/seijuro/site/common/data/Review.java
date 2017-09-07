package com.github.seijuro.site.common.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public abstract class Review {
    @Getter
    private final String reviewId;
    @Getter
    private final String postDate;
    @Getter
    private final String score;
    @Getter
    private final String title;
    @Getter
    private final String locale;
    private final boolean hasResponse;
    @Getter
    private final String reviewer;
    @Getter
    private final String location;

    public boolean hasResponse() {
        return hasResponse;
    }

    protected Review(Builder builder) {
        reviewId = builder.reviewId;
        postDate = builder.postDate;
        score = builder.score;
        title = builder.title;
        locale = builder.locale;
        hasResponse = builder.hasResponse;
        reviewer = builder.reviewer;
        location = builder.location;
    }

    /**
     * Builder Pattner class
     */
    public static abstract class Builder {
        @Setter
        private String reviewId = null;
        @Setter
        private String postDate = StringUtils.EMPTY;
        @Setter
        private String score = StringUtils.EMPTY;
        @Setter
        private String title = StringUtils.EMPTY;
        @Setter
        private String locale = StringUtils.EMPTY;
        @Setter
        private boolean hasResponse = false;
        @Setter
        private String reviewer = StringUtils.EMPTY;
        @Setter
        private String location = StringUtils.EMPTY;

        /**
         * Builder Pattern method
         * @return
         */
        public abstract Review build();
    }
}
