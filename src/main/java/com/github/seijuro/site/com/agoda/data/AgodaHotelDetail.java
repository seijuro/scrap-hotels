package com.github.seijuro.site.com.agoda.data;

import com.github.seijuro.site.common.data.Hotel;
import com.github.seijuro.site.common.data.HotelDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class AgodaHotelDetail extends HotelDetail {
    /**
     * Instance Properties
     */
    @Getter
    private String beachText;
    @Getter
    private boolean taxIncluded;
    @Getter
    private boolean breakfastInclude;
    @Getter
    private int agodaReviewCount;

    public int getReviewPageCount() {
        if (agodaReviewCount > 0) {
            return (agodaReviewCount / 10) + (agodaReviewCount % 10 == 0 ? 0 : 1);
        }

        return 1;
    }

    /**
     * Construct
     *
     * @param builder
     */
    protected AgodaHotelDetail(Builder builder) {
        super(builder);

        beachText = builder.beachText;
        taxIncluded = builder.taxIncluded;
        breakfastInclude = builder.breakfastInclude;
        agodaReviewCount = builder.agodaReviewCount;
    }

    public static class Builder extends HotelDetail.Builder {
        @Setter
        private String beachText = StringUtils.EMPTY;
        @Setter
        private boolean taxIncluded = true;
        @Setter
        private boolean breakfastInclude = false;
        @Setter
        private int agodaReviewCount = 0;

        @Override
        public AgodaHotelDetail build() {
            return new AgodaHotelDetail(this);
        }
    }
}
