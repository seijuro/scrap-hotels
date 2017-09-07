package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class ExpediaHotelDetailTable {
    @Getter
    private static final String TableName = "ExpediaHotelDetail";

    /*
    CREATE TABLE `ExpediaHotelDetail` (
      `id` varchar(10) NOT NULL,
      `name` VARCHAR(255) NOT NULL,
      `address` varchar(255) NOT NULL,
      `construct_year` varchar(10) NOT NULL,
      `floor` varchar(10) NOT NULL,
      `rooms` varchar(10) NOT NULL,
      `beach` varchar(20) NOT NULL,
      `class` varchar(10) NOT NULL,
      `guest_rating` varchar(10) NOT NULL,
      `has_meeting_room` TINYINT(1) NOT NULL DEFAULT '0',
      `has_restaurant` tinyint(1) NOT NULL DEFAULT '0',
      `has_pool` tinyint(1) NOT NULL DEFAULT '0',
      `has_fitness` tinyint(1) NOT NULL DEFAULT '0',
      `has_casino` tinyint(1) NOT NULL DEFAULT '0',
      `breakfast_included` tinyint(1) NOT NULL DEFAULT '0',
      `reviews` int(10) NOT NULL DEFAULT 0,
      `lastupdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`),
      KEY `IDX_LASTUPDATE` (`lastupdate`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8
     */
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
        HasMeetingRoom("has_meeting_room"),
        HasRestaurant("has_restaurant"),
        HasPool("has_pool"),
        HasFitness("has_fitness"),
        HasCasino("has_casino"),
        BreakfastInclude("breakfast_included"),
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
