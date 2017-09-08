package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class ExpediaHotelDetailTable {
    @Getter
    private static final String TableName = "ExpediaHotelDetail";

    public enum Column implements DBColumn {
        Id("id"),
        Name("name"),
        Address("address"),
        ConstructedYear("construct_year"),
        Floor("floor"),
        Rooms("rooms"),
        Beach("beach"),
        StarRating("class"),
        GuestRating("guest_rating"),
        RefProductPrice("ref_product_price"),
        HasMeetingRoom("has_meeting_room"),
        HasRestaurant("has_restaurant"),
        HasPool("has_pool"),
        HasFitness("has_fitness"),
        HasCasino("has_casino"),
        BreakfastInclude("breakfast_included"),
        FreeCancellation("free_cancellation"),
        ReviewCount("reviews"),
        Lastupdate("lastupdate");

        /**
         * Instance Properties
         */
        private final String columnName;

        /**
         * Construct
         *
         * @param name
         */
        Column(String name) {
            columnName = name;
        }

        @Override
        public String getColumnName() {
            return columnName;
        }
    }
}
