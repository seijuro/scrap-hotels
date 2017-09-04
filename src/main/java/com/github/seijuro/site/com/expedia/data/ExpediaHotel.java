package com.github.seijuro.site.com.expedia.data;

import com.github.seijuro.site.HotelBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ExpediaHotel extends HotelBase {
    @Getter
    private final String secondaryName;
    @Getter
    private final String rating;
    @Getter
    private final String averagePerNight;
    @Getter
    private final String salePercentOff;
    @Getter
    private final String sale;
    @Getter
    private final String saleTag;
    @Getter
    private final String npp;
    @Getter
    private final String badge;
    @Getter
    private final boolean vip;
    @Getter
    private final String freeCancel;
    @Getter
    private final List<String> discountRibbons;

    /**
     * C'tor
     *
     * @param builder
     */
    protected ExpediaHotel(Builder builder) {
        super(builder);

        secondaryName = builder.secondaryName;
        rating = builder.rating;
        averagePerNight = builder.averagePerNight;
        salePercentOff = builder.salePercentOff;
        sale = builder.sale;
        saleTag = builder.saleTag;
        npp = builder.npp;
        badge = builder.badge;
        vip = builder.vip;
        freeCancel = builder.freeCancel;
        discountRibbons = builder.discountRibbons;
    }

    public static class Builder extends HotelBase.Builder {
        @Setter
        private String secondaryName = StringUtils.EMPTY;
        @Setter
        private String rating = StringUtils.EMPTY;
        @Setter
        private String averagePerNight = StringUtils.EMPTY;
        @Setter
        private String salePercentOff = StringUtils.EMPTY;
        @Setter
        private String sale = StringUtils.EMPTY;
        @Setter
        private String saleTag = StringUtils.EMPTY;
        @Setter
        private String npp = StringUtils.EMPTY;
        @Setter
        private String badge = StringUtils.EMPTY;
        @Setter
        private boolean vip = false;
        @Setter
        private String freeCancel = StringUtils.EMPTY;
        private List<String> discountRibbons = new ArrayList<>();

        public void addDiscountRibbon(String ribbon) {
            if (StringUtils.isNotEmpty(ribbon)) { discountRibbons.add(ribbon); }
        }

        @Override
        public ExpediaHotel build() {
            return new ExpediaHotel(this);
        }
    }
}
