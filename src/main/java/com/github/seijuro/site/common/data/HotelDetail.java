package com.github.seijuro.site.common.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString(callSuper = true)
public class HotelDetail {
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
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final String address;
    @Getter
    private final String builtDate;
    @Getter
    private final String floors;
    @Getter
    private final String rooms;
    @Getter
    private final String starRating;
    @Getter
    private final BeachInfo beach;
    private final boolean hasRestaurant;
    private final boolean hasPool;
    private final boolean hasFitness;
    private final boolean hasCasino;
    private final boolean hasMeetingRoom;
    
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
     * Contruct <code>HotelDetail</code>
     *
     * @param builder
     */
    protected HotelDetail(Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        builtDate = builder.builtDate;
        floors = builder.floors;
        rooms = builder.rooms;
        starRating = builder.starRating;
        beach = builder.beach;
        hasRestaurant = builder.hasRestaurant;
        hasPool = builder.hasPool;
        hasFitness = builder.hasFitness;
        hasCasino = builder.hasCasino;
        hasMeetingRoom = builder.hasMeetingRoom;
    }

    /**
     * Builder Pattern class
     */
    @ToString(callSuper = true)
    public static abstract class Builder {
        /**
         * Instance Properties
         */
        @Setter
        private String id = null;
        @Setter
        private String name = StringUtils.EMPTY;
        @Setter
        private String address = StringUtils.EMPTY;
        @Setter
        private String builtDate = StringUtils.EMPTY;
        @Setter
        private String floors = StringUtils.EMPTY;
        @Setter
        private String rooms = StringUtils.EMPTY;
        @Setter
        private String starRating = StringUtils.EMPTY;
        @Setter
        private BeachInfo beach = null;
        @Setter
        private boolean hasRestaurant = false;
        @Setter
        private boolean hasPool = false;
        @Setter
        private boolean hasFitness = false;
        @Setter
        private boolean hasCasino = false;
        @Setter
        private boolean hasMeetingRoom = false;

        /**
         * Builder Pattern method
         *
         * @return
         */
        public abstract HotelDetail build();
    }
}
