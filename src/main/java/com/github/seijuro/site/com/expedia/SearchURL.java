package com.github.seijuro.site.com.expedia;

import com.github.seijuro.site.com.expedia.query.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchURL implements com.github.seijuro.search.SearchURL {
    public static final String BaseURL = "https://www.expedia.co.kr";
    public static final String SearchSubURL = "Hotel-Search";

    public enum QueryParamter {
        Destination("destination"),
        RegionId("regionId"),
        Sort("sort"),
        Adults("adults"),
        Children("children"),
        Lodging("lodging"),
        StartDate("startDate"),
        EndDate("endDate"),
        Page("page");

        @Getter
        private final String parameter;

        QueryParamter(String param) {
            parameter = param;
        }
    }

    @Getter
    private final Destination destination;
    @Getter
    private final com.github.seijuro.search.query.Sort sort;
    @Getter
    private final Adults adults;
    @Getter
    private final Children children;
    @Getter
    private final Date startDate;
    @Getter
    private final Date endDate;
    @Getter
    private final List<Lodging> lodgings;
    @Getter
    private final int page;

    /**
     * Construct
     *
     * @param builder
     */
    protected SearchURL(Builder builder) {
        destination = builder.destination;
        sort = builder.sort;
        adults = builder.adults;
        children = builder.children;
        startDate = builder.startDate;
        endDate = builder.endDate;
        lodgings = builder.lodgings;
        page = builder.page;
    }

    @Override
    public String toURL() {
        StringBuffer urlBuilder = new StringBuffer(BaseURL);

        urlBuilder.append("/").append(SearchSubURL)
                .append("?").append(QueryParamter.Destination.getParameter()).append("=").append(destination.getQueryParameter())
                .append("&").append(QueryParamter.RegionId.getParameter()).append("=").append(destination.getId())
                .append("&").append(QueryParamter.StartDate.getParameter()).append("=").append(startDate.getQueryParameter())
                .append("&").append(QueryParamter.EndDate.getParameter()).append("=").append(endDate.getQueryParameter())
                .append("&").append(QueryParamter.Adults.getParameter()).append("=").append(adults.getQueryParameter());

        if (StringUtils.isNotEmpty(children.getQueryParameter())) {
            urlBuilder.append("&").append(QueryParamter.Children.getParameter()).append("=").append(children.getQueryParameter());
        }

        if (Objects.nonNull(sort)) {
            urlBuilder.append("&").append(QueryParamter.Sort.getParameter()).append("=").append(sort.getQueryParameter());
        }

        if (lodgings.size() > 0) {
            urlBuilder.append("&").append(QueryParamter.Lodging.getParameter()).append("=").append(lodgings.get(0).getQueryParameter());

            for (int index = 1; index < lodgings.size(); ++index) {
                urlBuilder.append(",").append(lodgings.get(index).getQueryParameter());
            }
        }

        urlBuilder.append("&").append(QueryParamter.Page.getParameter()).append("=").append(page);

        return urlBuilder.toString();
    }

    /**
     * Builder Pattern class
     */
    public static class Builder {
        @Setter
        Destination destination = null;
        @Setter
        com.github.seijuro.search.query.Sort sort = null;
        private Adults adults = new Adults(2);
        private Children children = new Children(Collections.emptyList());
        @Setter
        List<Lodging> lodgings = new ArrayList<>();
        @Setter
        Date startDate = null;
        @Setter
        Date endDate = null;
        @Setter
        int page = 1;

        public void setAdults(int adults) {
            this.adults = new Adults(adults);
        }

        public void setChildren(List<Integer> children) {
            this.children = new Children(children);
        }

        public void setStartDate(int year, int month, int day) {
            startDate = new Date(year, month, day);
        }

        public void setEndDate(int year, int month, int day) {
            endDate = new Date(year, month, day);
        }

        public SearchURL build() {
            return new SearchURL(this);
        }
    }
}
