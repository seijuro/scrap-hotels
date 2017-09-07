package com.github.seijuro.site.com.expedia.data;

import com.github.seijuro.site.common.data.HotelReview;
import lombok.ToString;

@ToString(callSuper = true)
public class ExpediaHotelReview extends HotelReview {
    /**
     * Construct
     *
     * @param builder
     */
    protected ExpediaHotelReview(Builder builder) {
        super(builder);
    }

    /**
     * Builder Pattern class
     * extends <code>HotelReview.Builder</code>
     */
    public static class Builder extends HotelReview.Builder {
        @Override
        public ExpediaHotelReview build() {
            return new ExpediaHotelReview(this);
        }
    }
}
