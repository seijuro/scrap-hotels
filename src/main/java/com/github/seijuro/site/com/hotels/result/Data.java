package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.DataProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Data {
    @Getter
    @SerializedName(DataProperty.Body)
    private Body body;
}
