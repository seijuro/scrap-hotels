package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class AgodaHotelReviewTable {
    /*
    DROP TABLE AgodaHotelReview;
    CREATE TABLE AgodaHotelReview (
        id INT(10) NOT NULL PRIMARY KEY,
        hotel_id VARCHAR(10) NOT NULL,
        post_date VARCHAR(20) NOT NULL,
        stay_date VARCHAR(20) NOT NULL,
        stay_days VARCHAR(10) NOT NULL,
        room_type VARCHAR(50) NOT NULL,
        score VARCHAR(10) NOT NULL,
        title VARCHAR(255) NOT NULL,
        locale VARCHAR(20) NOT NULL,
        has_response TINYINT(1) NOT NULL DEFAULT 0,
        reviewer VARCHAR(255) NOT NULL,
        country VARCHAR(50) NOT NULL,
        lastupdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX `IDX_HOTELID` (`hotel_id`),
        INDEX `IDX_LASTUPDATE` (`lastupdate`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
     */

    @Getter
    private static final String TableName = "AgodaHotelReviewDetail";

    public enum Column implements DBColumn {
        Id("id"),
        HotelId("hotel_id"),
        PostDate("post_date"),
        StayDate("stay_date"),
        StayDays("stay_days"),
        RoomType("room_type"),
        Score("score"),
        Title("title"),
        Locale("locale"),
        HasResponse("has_response"),
        Reviewer("reviewer"),
        Country("country"),
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
