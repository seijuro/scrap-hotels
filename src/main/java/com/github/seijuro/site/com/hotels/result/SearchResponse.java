package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.SearchResponseProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
public class SearchResponse {
    @Getter
    @SerializedName(SearchResponseProperty.Data)
    private Data data;
}
