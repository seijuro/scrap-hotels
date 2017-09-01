package com.github.seijuro.site.com.hotels.result;

import com.github.seijuro.site.com.hotels.property.PaginationProperty;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@EqualsAndHashCode
public class Pagination {
    @Getter
    @SerializedName(PaginationProperty.CurrentPage)
    private Integer currentPage = null;
    @Getter
    @SerializedName(PaginationProperty.PageGroup)
    private String pageGroup = StringUtils.EMPTY;
    @Getter
    @SerializedName(PaginationProperty.NextPageUrl)
    private String nextPageUrl = StringUtils.EMPTY;
}
