package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.DealsProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class Deals {
    @ToString
    public class SpecialDeal {
        @Getter
        @SerializedName(DealsProperty.SpecialDealProperty.DealText)
        private String dealText = StringUtils.EMPTY;
    }

    /**
     * Instance Properties
     */
    @Getter
    @SerializedName(DealsProperty.SpecialDeal)
    private SpecialDeal specialDeal = null;
    @Getter
    @SerializedName(DealsProperty.GreatRate)
    private Boolean greatRate = null;
    @Getter
    @SerializedName(DealsProperty.PriceReasoning)
    private String priceReasoning = StringUtils.EMPTY;
}
