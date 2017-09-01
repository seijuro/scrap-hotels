package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.URLsProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
public class URLs {
    @Getter
    @SerializedName(URLsProperty.PdpDescription)
    private String pdpDescription;
    @Getter
    @SerializedName(URLsProperty.PdpMap)
    private String pdpMap;
    @Getter
    @SerializedName(URLsProperty.PdpReviews)
    private String pdpReviews;
}
