package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.SearchResultsProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
public class SearchResults {
    /**
     * Instance Properties
     */
    @Getter
    @SerializedName(SearchResultsProperty.TotalCount)
    private Integer totalCount = null;
    @Getter
    @SerializedName(SearchResultsProperty.Pagination)
    private Pagination pagination = null;
    @Getter
    @SerializedName(SearchResultsProperty.Results)
    private Result[] results;
}
