package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.WelcomeRewardsProperty;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class WelcomeRewards {
    @SerializedName(WelcomeRewardsProperty.Info)
    private String info = StringUtils.EMPTY;
}
