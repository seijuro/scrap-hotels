package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.BodyProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Body {
    @Getter
    @SerializedName(BodyProperty.Header)
    private String header = null;
    @Getter
    @SerializedName(BodyProperty.Query)
    private Query query = null;
    @Getter
    @SerializedName(BodyProperty.SearchResults)
    private SearchResults searchResults = null;
}
