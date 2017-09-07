package com.github.seijuro.site.com.agoda.data;

import com.github.seijuro.site.common.data.HotelReview;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString(callSuper = true)
public class AgodaHotelReview extends HotelReview {
    @Getter
    private final String stayDate;
    @Getter
    private final String stayDays;
    @Getter
    private final String roomType;

    /**
     * Construct
     *
     * @param builder
     */
    protected AgodaHotelReview(Builder builder) {
        super(builder);

        stayDate = builder.stayDate;
        stayDays = builder.stayDays;
        roomType = builder.roomType;
    }

    public static class Builder extends HotelReview.Builder {
        @Setter
        private String stayDate = StringUtils.EMPTY;
        @Setter
        private String stayDays = StringUtils.EMPTY;
        @Setter
        private String roomType = StringUtils.EMPTY;

        @Override
        public AgodaHotelReview build() {
            return new AgodaHotelReview(this);
        }
    }
}
