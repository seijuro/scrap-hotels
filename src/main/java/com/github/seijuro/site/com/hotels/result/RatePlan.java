package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.RatePlanProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class RatePlan {
    @ToString
    public class Price {
        @Getter
        @SerializedName(RatePlanProperty.PriceProperty.Current)
        private String current = StringUtils.EMPTY;
        @Getter
        @SerializedName(RatePlanProperty.PriceProperty.ExactCurrent)
        private String exactCurrent = StringUtils.EMPTY;
        @Getter
        @SerializedName(RatePlanProperty.PriceProperty.Old)
        private String old = StringUtils.EMPTY;
        @Getter
        @SerializedName(RatePlanProperty.PriceProperty.FromPrice)
        private String fromPrice = StringUtils.EMPTY;
    }

    @ToString
    public class Features {
        @Getter
        @SerializedName(RatePlanProperty.FeaturesProperty.PaymentPreference)
        private Boolean paymentPreference = Boolean.FALSE;
        @Getter
        @SerializedName(RatePlanProperty.FeaturesProperty.InstalmentsMessage)
        private String instalmentsMessage = StringUtils.EMPTY;
        @Getter
        @SerializedName(RatePlanProperty.FeaturesProperty.FreeCancellation)
        private Boolean freeCancellation = Boolean.FALSE;
    }

    /**
     * Instance Properties
     */
    @Getter
    @SerializedName(RatePlanProperty.Price)
    private Price price = null;
    @Getter
    @SerializedName(RatePlanProperty.Features)
    private Features features = null;
    @Getter
    @SerializedName(RatePlanProperty.Type)
    private String type = null;
}
