package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class ExpediaHotelReviewTable {
    @Getter
    private static final String TableName = "ExpediaHotelReviewBase";

    /*
    DROP VIEW ExpediaHotelReview;
    CREATE VIEW ExpediaHotelReview
    AS
    SELECT
        vt_review.*,
        vt_hotel.name AS hotel_name,
        vt_userid.id AS reviewer_id
    FROM
        ExpediaHotelReviewBase AS vt_review
        LEFT JOIN ExpediaHotelDetail AS vt_hotel
        ON vt_review.hotel_id=vt_hotel.id
        LEFT JOIN UserId AS vt_userid
        ON vt_userid.domain='Expedia.com' AND vt_review.reviewer=vt_userid.reviewer AND vt_review.country=vt_userid.country;
     */

    /*
    select
      `vt_review`.`id` AS `id`,
      `vt_review`.`hotel_id` AS `hotel_id`,
      `vt_review`.`post_date` AS `post_date`,
      `vt_review`.`stay_date` AS `stay_date`,
      `vt_review`.`stay_days` AS `stay_days`,
      `vt_review`.`room_type` AS `room_type`,
      `vt_review`.`score` AS `score`,
      `vt_review`.`title` AS `title`,
      `vt_review`.`locale` AS `locale`,
      `vt_review`.`has_response` AS `has_response`,
      `vt_review`.`reviewer` AS `reviewer`,
      `vt_review`.`country` AS `country`,
      `vt_review`.`lastupdate` AS `lastupdate`,
      `vt_userid`.`id` AS `reviewer_id`
     from
       AgodaHotelReviewBase AS vt_review
       LEFT JOIN Agoda
       LEFT JOIN UserId AS vt_userid
       on vt_userid.domain='Agoda.com' AND vt_review.reviewer = vt_user.reviewer AND vt_review.country=vt_review.country
     */


    /*
    CREATE TABLE `UserId` (
      `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `domain` VARCHAR(255) NOT NULL,
      `reviewer` varchar(255) NOT NULL,
      `country` varchar(50) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `UIDX_REVIEWER` (`domain`, `reviewer`,`country`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
     */

    public enum Column implements DBColumn {
        Id("id"),
        HotelId("hotel_id"),
        PostDate("post_date"),
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
