package com.github.seijuro.site.com.agoda.data;

import com.github.seijuro.site.HotelBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
public class AgodaHotelDetail extends HotelBase {
    public enum BeachInfo {
        NEAR,
        EXIST,
        NONE
    }

    /**
     * Instance Properties
     */
    @Getter
    private String address;
    @Getter
    private String constructedYear;
    @Getter
    private String floor;
    @Getter
    private String rooms;
    @Getter
    private String beach;
    private boolean hasRestaurant;
    private boolean hasPool;
    private boolean hasFitness;
    private boolean hasCasino;
    @Getter
    private boolean taxIncluded;
    @Getter
    private boolean breakfastInclude;
    @Getter
    private int agodaReviewCount;

    public boolean hasRestaurant() {
        return hasRestaurant;
    }

    public boolean hasPool() {
        return hasPool;
    }

    public boolean hasFitness() {
        return hasFitness;
    }

    public boolean hasCasino() {
        return hasCasino;
    }

    public int getReviewPageCount() {
        if (agodaReviewCount > 0) {
            return (agodaReviewCount / 10) + (agodaReviewCount % 10 == 0 ? 0 : 1);
        }

        return 1;
    }

    /**
     * Construct
     *
     * @param builder
     */
    protected AgodaHotelDetail(Builder builder) {
        super(builder);

        address = builder.address;
        constructedYear = builder.builtYear;
        floor = builder.floor;
        rooms = builder.rooms;
        beach = builder.beach;
        hasRestaurant = builder.hasRestaurant;
        hasPool = builder.hasPool;
        hasFitness = builder.hasFitness;
        hasCasino = builder.hasCasino;
        taxIncluded = builder.taxIncluded;
        breakfastInclude = builder.breakfastInclude;
        agodaReviewCount = builder.agodaReviewCount;
    }

    public static class Builder extends HotelBase.Builder {
        @Setter
        private String address = StringUtils.EMPTY;
        @Setter
        private String builtYear = StringUtils.EMPTY;
        @Setter
        private String floor = StringUtils.EMPTY;
        @Setter
        private String rooms = StringUtils.EMPTY;
        @Setter
        private String beach = StringUtils.EMPTY;
        @Setter
        private boolean hasRestaurant = false;
        @Setter
        private boolean hasPool = false;
        @Setter
        private boolean hasFitness = false;
        @Setter
        private boolean hasCasino = false;
        @Setter
        private boolean taxIncluded = true;
        @Setter
        private boolean breakfastInclude = false;
        @Setter
        private int agodaReviewCount = 0;

        @Override
        public AgodaHotelDetail build() {
            return new AgodaHotelDetail(this);
        }
    }
}
