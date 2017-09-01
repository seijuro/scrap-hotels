package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.SponsoredProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class Sponsored {
    @Getter
    @SerializedName(SponsoredProperty.ImpressionTrackingUrl)
    private String impressionTrackingUrl = StringUtils.EMPTY;
}
