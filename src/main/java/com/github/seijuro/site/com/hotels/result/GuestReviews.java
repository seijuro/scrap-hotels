package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.GuestReviewsProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class GuestReviews {
    @Getter
    @SerializedName(GuestReviewsProperty.Rating)
    private String rating = StringUtils.EMPTY;
    @Getter
    @SerializedName(GuestReviewsProperty.Total)
    private Integer total = null;
    @Getter
    @SerializedName(GuestReviewsProperty.TotalText)
    private String totalText = StringUtils.EMPTY;
    @Getter
    @SerializedName(GuestReviewsProperty.Scale)
    private Integer scale = null;
    @Getter
    @SerializedName(GuestReviewsProperty.Badge)
    private String badge = StringUtils.EMPTY;
    @Getter
    @SerializedName(GuestReviewsProperty.BadgeText)
    private String badgeText = StringUtils.EMPTY;
}
