package com.github.seijuro.site.com.tripadvisor.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@EqualsAndHashCode
public class TripAdvisorReviewerProfile {
    public enum Sex {
        UNKNOWN(""),
        MALE("남성"),
        FEMALE("여성");

        private final String text;

        public String toText() {
            return text;
        }

        Sex(String text) {
            this.text = text;
        }
    }

    @Getter
    private final String reviewId;
    @Getter
    private final String reviewerName;
    @Getter
    private final int level;
    @Getter
    private final String registration;
    @Getter
    private final boolean contributor;
    @Getter
    private final int numberOfPosts;
    @Getter
    private final int numberOfCityVisited;
    @Getter
    private final int numberOfLike;
    @Getter
    private final int numberOfPhotos;

    @Getter
    private final String age;
    private final Sex sex;
    @Getter
    private final String country;

    @Getter
    private final int guestRating_Star5;
    @Getter
    private final int guestRating_Star4;
    @Getter
    private final int guestRating_Star3;
    @Getter
    private final int guestRating_Star2;
    @Getter
    private final int guestRating_Star1;

    public String getSex() {
        return sex.toText();
    }


    protected TripAdvisorReviewerProfile(Builder builder) {
        reviewId = builder.reviewId;
        reviewerName = builder.reviewerName;
        level = builder.level;
        registration = builder.registration;
        contributor = builder.contributor;
        numberOfPosts = builder.numberOfPosts;
        numberOfCityVisited = builder.numberOfCityVisited;
        numberOfLike = builder.numberOfLike;
        numberOfPhotos = builder.numberOfPhotos;

        age = builder.age;
        sex = builder.sex;
        country = builder.country;;
        guestRating_Star5 = builder.guestRating_Star5;;
        guestRating_Star4 = builder.guestRating_Star4;
        guestRating_Star3 = builder.guestRating_Star3;
        guestRating_Star2 = builder.guestRating_Star2;
        guestRating_Star1 = builder.guestRating_Star1;
    }

    public static class Builder {
        @Setter
        private String reviewId = StringUtils.EMPTY;
        @Setter
        private String reviewerName = StringUtils.EMPTY;
        @Setter
        private String registration = StringUtils.EMPTY;
        @Setter
        private int level = 0;
        @Setter
        private boolean contributor = false;
        @Setter
        private int numberOfPosts = 0;
        @Setter
        private int numberOfCityVisited = 0;
        @Setter
        private int numberOfLike = 0;
        @Setter
        private int numberOfPhotos = 0;

        @Setter
        private String age = StringUtils.EMPTY;
        @Setter
        private Sex sex = Sex.UNKNOWN;
        @Setter
        private String country = StringUtils.EMPTY;

        @Setter
        private int guestRating_Star5 = 0;
        @Setter
        private int guestRating_Star4 = 0;
        @Setter
        private int guestRating_Star3 = 0;
        @Setter
        private int guestRating_Star2 = 0;
        @Setter
        private int guestRating_Star1 = 0;

        public TripAdvisorReviewerProfile build() {
            return new TripAdvisorReviewerProfile(this);
        }
    }
}
