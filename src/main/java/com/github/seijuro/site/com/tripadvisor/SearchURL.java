package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.search.query.Destination;
import com.github.seijuro.search.query.Lodging;
import com.github.seijuro.search.query.Sort;
import com.github.seijuro.site.com.tripadvisor.query.Adults;
import com.github.seijuro.site.com.tripadvisor.query.CheckIn;
import com.github.seijuro.site.com.tripadvisor.query.CheckOut;
import com.github.seijuro.site.com.tripadvisor.query.Children;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchURL implements com.github.seijuro.search.SearchURL {
    @Getter
    public static final String BaseURL = "https://wwww.tripadvisor.co.kr";

    /**
     * Instance Properties
     */
    @Getter
    private final Destination destination;
    @Getter
    private final Sort sort;
    @Getter @Setter
    private int page;
    @Getter
    private final CheckIn startDate;
    @Getter
    private final CheckOut endDate;
    private final Adults adults;
    private final Children children;
    @Getter
    private final List<Lodging> lodgings;

    /**
     * Construct <code>SearchURL</code> instance
     *
     * @param builder
     */
    protected SearchURL(Builder builder) {
        destination = builder.destination;
        sort = builder.sort;
        page = builder.page;
        startDate = builder.checkIn;
        endDate = builder.checkOut;
        adults = builder.adults;
        children = builder.children;
        lodgings = builder.lodgings;
    }

    @Override
    public String toURL() {
        StringBuffer urlBuilder = new StringBuffer(getBaseURL());

        urlBuilder.append("/Hotels-").append(destination.getId())
                .append("-").append(destination.getQueryParameter())
                .append("-Hotels.html");

        return urlBuilder.toString();
    }

    /**
     * Builder Pattern class
     */
    public static class Builder {
        /**
         * Instance Properties
         */
        @Setter
        private Destination destination = null;
        @Setter
        private Sort sort = com.github.seijuro.site.com.tripadvisor.query.Sort.RECOMMENDED;
        @Setter
        private int page = 1;
        private CheckIn checkIn = null;
        private CheckOut checkOut = null;
        private Adults adults = new Adults(2);
        private Children children = new Children(Collections.emptyList());
        @Setter
        private List<Lodging> lodgings = new ArrayList<>();

        public Builder() {
            lodgings.add(com.github.seijuro.site.com.tripadvisor.query.Lodging.HOTEL);
        }

        public void setCheckIn(int year, int month, int day) {
            checkIn = new CheckIn(year, month, day);
        }

        public void setCheckOut(int year, int month, int day) {
            checkOut = new CheckOut(year, month, day);
        }

        public void setChildren(List<Integer> ages) {
            children = new Children(ages);
        }

        /**
         * Builder Pattern method
         *
         * @return
         */
        public SearchURL build() {
            return new SearchURL(this);
        }
    }
}
