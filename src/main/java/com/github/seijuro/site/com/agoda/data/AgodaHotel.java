package com.github.seijuro.site.com.agoda.data;

import com.github.seijuro.site.common.data.Hotel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class AgodaHotel extends Hotel {
    @Getter
    private String starRating = StringUtils.EMPTY;
    @Getter
    private Set<String> options = new HashSet<>();
    @Getter
    private String reviewsScore = StringUtils.EMPTY;
    @Getter
    private int reviewCount;
    @Getter
    private String currency;
    @Getter
    private boolean thumbUp = false;
    @Getter
    private String couponDiscount = StringUtils.EMPTY;
    @Getter
    private String award = StringUtils.EMPTY;
    @Getter
    private String discountRibbon = StringUtils.EMPTY;
    @Getter
    private boolean freeCancellation;


    /**
     * AgodaHotel
     *
     * @param builder
     */
    protected AgodaHotel(Builder builder) {
        super(builder);

        starRating = builder.starRating;
        options = builder.options;
        reviewsScore = builder.reviewsScore;
        reviewCount = builder.reviewCount;
        currency = builder.currency;
        thumbUp = builder.thumbUp;
        couponDiscount = builder.couponDiscount;
        award = builder.award;
        discountRibbon = builder.discountRibbon;
        freeCancellation = builder.freeCancellation;
    }

    public static class Builder extends Hotel.Builder {
        @Setter
        private String starRating = StringUtils.EMPTY;
        private Set<String> options = new HashSet<>();
        @Setter
        private String reviewsScore = StringUtils.EMPTY;
        @Setter
        private int reviewCount = 0;
        @Setter
        private String currency = StringUtils.EMPTY;
        @Setter
        private boolean thumbUp = false;
        @Setter
        private String couponDiscount = StringUtils.EMPTY;
        @Setter
        private String award = StringUtils.EMPTY;
        @Setter
        private String discountRibbon = StringUtils.EMPTY;
        @Setter
        private boolean freeCancellation = false;

        public void setOption(String option) {
            options.add(StringUtils.normalizeSpace(option));
        }

        @Override
        public AgodaHotel build() {
            return new AgodaHotel(this);
        }
    }
}
