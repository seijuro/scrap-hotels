package com.github.seijuro.site.com.expedia.data;

import com.github.seijuro.site.HotelBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
@ToString
public class ExpediaHotelDetail extends HotelBase {
    public enum BeachInfo {
        NEAR("해변근처"),
        EXIST("해변에 위치"),
        NONE("해변없음");

        @Getter
        private final String description;

        BeachInfo(String description) {
            this.description = description;
        }
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
    private String starRating;
    @Getter
    private String guestRating;
    @Getter
    private BeachInfo beach;
    private boolean hasRestaurant;
    private boolean hasPool;
    private boolean hasFitness;
    private boolean hasCasino;
    private boolean hasMeetingRoom;
    @Getter
    private boolean taxIncluded;
    @Getter
    private boolean breakfastInclude;
    @Getter
    private int reviewCount;

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

    public boolean hasMeetingRoom() {
        return hasMeetingRoom;
    }


    /**
     * Construct
     *
     * @param builder
     */
    protected ExpediaHotelDetail(Builder builder) {
        super(builder);

        address = builder.address;
        constructedYear = builder.builtYear;
        floor = builder.floor;
        rooms = builder.rooms;
        starRating = builder.starRating;
        guestRating = builder.guestRating;
        beach = builder.beach;
        hasRestaurant = builder.hasRestaurant;
        hasPool = builder.hasPool;
        hasFitness = builder.hasFitness;
        hasCasino = builder.hasCasino;
        hasMeetingRoom = builder.hasMeetingRoom;
        taxIncluded = builder.taxIncluded;
        breakfastInclude = builder.breakfastInclude;
        reviewCount = builder.reviewCount;
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
        private BeachInfo beach = BeachInfo.NONE;
        @Setter
        private String starRating = StringUtils.EMPTY;
        @Setter
        private String guestRating = StringUtils.EMPTY;;
        @Setter
        private boolean hasRestaurant = false;
        @Setter
        private boolean hasPool = false;
        @Setter
        private boolean hasMeetingRoom = false;
        @Setter
        private boolean hasFitness = false;
        @Setter
        private boolean hasCasino = false;
        @Setter
        private boolean taxIncluded = true;
        @Setter
        private boolean breakfastInclude = false;
        @Setter
        private int reviewCount = 0;

        @Override
        public ExpediaHotelDetail build() {
            return new ExpediaHotelDetail(this);
        }
    }
}