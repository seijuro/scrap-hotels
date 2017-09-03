package com.github.seijuro.site.com.booking.data;

import com.github.seijuro.CSVConvertable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ToString
public class HotelData implements CSVConvertable {
    @Getter
    private final String domain;
    @Getter
    private final String destination;
    @Getter
    private final String sort;
    @Getter
    private final String startDate;
    @Getter
    private final String endDate;
    @Getter
    private final boolean thumbUpIcon;
    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final String price;
    @Getter
    private final String score;
    @Getter
    private final String linkURL;
    @Getter
    private final String clazz;
    @Getter
    private final String tag;
    @Getter
    private final List<String> conditions;
    @Getter
    private final List<String> ribbon;
    @Getter
    private final List<String> deal;
    @Getter
    private final String banner;

    /**
     * Constructor <code>HotelData</code> for Builder Pattern
     *
     * @param builder
     */
    public HotelData(Builder builder) {
        domain = builder.domain;
        destination = builder.destination;
        sort = builder.sort;
        startDate = builder.startDate;
        endDate = builder.endDate;

        thumbUpIcon = builder.thumbUpIcon;
        conditions = builder.conditions;
        id = builder.id;
        name = builder.name;
        price = builder.price;
        score = builder.score;
        linkURL = builder.linkURL;
        clazz = builder.clazz;
        tag = builder.tag;
        ribbon = builder.ribbons;
        deal = builder.deals;
        banner = builder.banner;
    }

    @Override
    public String recordSeperator() {
        return System.lineSeparator();
    }

    @Override
    public String columnSeperator() {
        return "|";
    }

    public String toCSV() {
        StringBuffer sb = new StringBuffer();

        sb.append(domain)
                .append(columnSeperator()).append(destination)
                .append(columnSeperator()).append(sort)
                .append(columnSeperator()).append(startDate)
                .append(columnSeperator()).append(endDate)
                .append(columnSeperator()).append(id)
                .append(columnSeperator()).append(name)
                .append(columnSeperator()).append(price)
                .append(columnSeperator()).append(score)
                .append(columnSeperator()).append(clazz)
                .append(columnSeperator()).append(tag)
                .append(columnSeperator()).append(thumbUpIcon)
                .append(columnSeperator()).append(linkURL);

        //  conditions
        sb.append(columnSeperator());
        if (conditions.size() > 0) { sb.append(conditions.get(0)); }
        for (int index = 1; index < conditions.size(); ++index) { sb.append(",").append(conditions.get(index)); }
        //  ribbons
        sb.append(columnSeperator());
        if (ribbon.size() > 0) { sb.append(ribbon.get(0)); }
        for (int index = 1; index < ribbon.size(); ++index) { sb.append(",").append(ribbon.get(index)); }
        //  deals
        sb.append(columnSeperator());
        if (deal.size() > 0) { sb.append(deal.get(0)); }
        for (int index = 1; index < deal.size(); ++index) { sb.append(",").append(deal.get(index)); }

        return sb.toString();
    }

    public static class Builder {
        @Setter
        private String domain = StringUtils.EMPTY;
        @Setter
        private String destination = StringUtils.EMPTY;
        @Setter
        private String sort = StringUtils.EMPTY;
        @Setter
        private String startDate = StringUtils.EMPTY;
        @Setter
        private String endDate = StringUtils.EMPTY;

        @Setter
        private boolean thumbUpIcon = false;
        @Setter
        private String id = StringUtils.EMPTY;
        @Setter
        private String name = StringUtils.EMPTY;
        @Setter
        private String price = StringUtils.EMPTY;
        @Setter
        private String score = StringUtils.EMPTY;
        @Setter
        private String linkURL = StringUtils.EMPTY;
        @Setter
        private String clazz = StringUtils.EMPTY;
        @Setter
        private String tag = StringUtils.EMPTY;
        private List<String> conditions = new ArrayList<>();
        private List<String> ribbons = new ArrayList<>();
        private List<String> deals = new ArrayList<>();
        @Setter
        private String banner = StringUtils.EMPTY;

        public void addCondition(String condition) {
            if (StringUtils.isNotEmpty(condition)) {
                conditions.add(condition.trim());
            }
        }

        public void addRibbon(String ribbon) {
            if (StringUtils.isNotEmpty(ribbon)) {
                ribbons.add(ribbon.trim());
            }
        }

        public void addDeal(String deal) {
            if (StringUtils.isNotEmpty(deal)) {
                deals.add(deal.trim());
            }
        }

        public HotelData build() {
            return new HotelData(this);
        }
    }
}
