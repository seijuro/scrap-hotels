package com.github.seijuro.site.common.data;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public abstract class Hotel {
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
    private final String id ;
    @Getter
    private final String name;
    @Getter
    private final String price;
    @Getter
    private final String linkURL;

    protected Hotel(Builder builder) {
        domain = builder.domain;
        destination = builder.destination;
        sort = builder.sort;
        startDate = builder.startDate;
        endDate = builder.endDate;
        id = builder.id;
        name = builder.name;
        price = builder.price;
        linkURL = builder.linkURL;
    }

    public static abstract class Builder {
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
        private String id = StringUtils.EMPTY;
        @Setter
        private String name = StringUtils.EMPTY;
        @Setter
        private String price = StringUtils.EMPTY;
        @Setter
        private String linkURL = StringUtils.EMPTY;

        public abstract Hotel build();
    }
}
