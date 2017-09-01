package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.BadgingProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class Badging {
    @ToString
    public class HotelBadge {
        @Getter
        @SerializedName(BadgingProperty.HotelBadgeProperty.Type)
        private String type = StringUtils.EMPTY;
        @Getter
        @SerializedName(BadgingProperty.HotelBadgeProperty.Label)
        private String label = StringUtils.EMPTY;
        @Getter
        @SerializedName(BadgingProperty.HotelBadgeProperty.TooltipTitle)
        private String tooltipTitle = StringUtils.EMPTY;
        @Getter
        @SerializedName(BadgingProperty.HotelBadgeProperty.TooltipText)
        private String tooltipText = StringUtils.EMPTY;
    }

    @Getter
    @SerializedName(BadgingProperty.HotelBadge)
    private HotelBadge hotelBadge = null;
}
