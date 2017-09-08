package com.github.seijuro.site.com.expedia.data;

import com.github.seijuro.site.common.data.Hotel;
import com.github.seijuro.site.common.data.HotelDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
@ToString(callSuper = true)
public class ExpediaHotelDetail extends HotelDetail {
    /**
     * Instance Properties
     */
    @Getter
    private String guestRating;
    @Getter
    private String refProductPrice;
    @Getter
    private boolean taxIncluded;
    @Getter
    private boolean breakfastInclude;
    @Getter
    private  boolean freeCancellation;
    @Getter
    private int reviewCount;

    /**
     * Construct
     *
     * @param builder
     */
    protected ExpediaHotelDetail(Builder builder) {
        super(builder);

        guestRating = builder.guestRating;
        refProductPrice = builder.refProductPrice;
        taxIncluded = builder.taxIncluded;
        breakfastInclude = builder.breakfastInclude;
        reviewCount = builder.reviewCount;
        freeCancellation = builder.freeCancellation;
    }

    @ToString(callSuper = true)
    public static class Builder extends HotelDetail.Builder {
        @Setter
        private String guestRating = StringUtils.EMPTY;;
        @Setter
        private String refProductPrice = StringUtils.EMPTY;
        @Setter
        private boolean taxIncluded = true;
        @Setter
        private boolean breakfastInclude = false;
        @Setter
        private boolean freeCancellation = false;
        @Setter
        private int reviewCount = 0;

        @Override
        public ExpediaHotelDetail build() {
            return new ExpediaHotelDetail(this);
        }
    }
}