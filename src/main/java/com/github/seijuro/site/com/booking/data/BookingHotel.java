package com.github.seijuro.site.com.booking.data;

import com.github.seijuro.CSVConvertable;
import com.github.seijuro.TSVConvertable;
import com.github.seijuro.site.common.data.Hotel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ToString
public class BookingHotel extends Hotel implements CSVConvertable, TSVConvertable {
    @Getter
    private final boolean thumbUpIcon;
    @Getter
    private final String score;
    @Getter
    private final String clazz;
    @Getter
    private final String tag;
    @Getter
    private final List<String> conditions;
    @Getter
    private final List<String> ribbon;
    @Getter
    private final List<String> deals;
    @Getter
    private final String banner;

    /**
     * Constructor <code>HotelData</code> for Builder Pattern
     *
     * @param builder
     */
    public BookingHotel(Builder builder) {
        super(builder);

        thumbUpIcon = builder.thumbUpIcon;
        conditions = builder.conditions;
        score = builder.score;
        clazz = builder.clazz;
        tag = builder.tag;
        ribbon = builder.ribbons;
        deals = builder.deals;
        banner = builder.banner;
    }

    protected String format(String columnSeperator, String elementSeperator) {
        StringBuffer sb = new StringBuffer();

        sb.append(getDomain())
                .append(columnSeperator).append(getDestination())
                .append(columnSeperator).append(getSort())
                .append(columnSeperator).append(getStartDate())
                .append(columnSeperator).append(getEndDate())
                .append(columnSeperator).append(getId())
                .append(columnSeperator).append(getName())
                .append(columnSeperator).append(getPrice())
                .append(columnSeperator).append(score)
                .append(columnSeperator).append(clazz)
                .append(columnSeperator).append(tag)
                .append(columnSeperator).append(thumbUpIcon)
                .append(columnSeperator).append(getLinkURL());

        //  conditions
        sb.append(columnSeperator);
        if (conditions.size() > 0) { sb.append(conditions.get(0)); }
        for (int index = 1; index < conditions.size(); ++index) { sb.append(elementSeperator).append(conditions.get(index)); }
        //  ribbons
        sb.append(columnSeperator);
        if (ribbon.size() > 0) { sb.append(ribbon.get(0)); }
        for (int index = 1; index < ribbon.size(); ++index) { sb.append(elementSeperator).append(ribbon.get(index)); }
        //  deals
        sb.append(columnSeperator);
        if (deals.size() > 0) { sb.append(deals.get(0)); }
        for (int index = 1; index < deals.size(); ++index) { sb.append(elementSeperator).append(deals.get(index)); }

        return sb.toString();
    }

    @Override
    public String toTSV() {
        return format("\t", ",");
    }

    public String toCSV() {
        return format(",", "\t");
    }

    public static class Builder extends Hotel.Builder {
        @Setter
        private boolean thumbUpIcon = false;
        @Setter
        private String score = StringUtils.EMPTY;
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

        public BookingHotel build() {
            return new BookingHotel(this);
        }
    }
}
