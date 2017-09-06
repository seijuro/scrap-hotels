package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

/**
   DROP TABLE AgodaHotelBase;
   CREATE TABLE AgodaHotelBase (
   idx INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
   domain VARCHAR(255) NOT NULL,
   keyword VARCHAR(20) NOT NULL,
   check_in VARCHAR(10) NOT NULL,
   check_out VARCHAR(10) NOT NULL,
   type VARCHAR(20) NOT NULL,
   sort VARCHAR(20) NOT NULL,
   rank INT(10) NOT NULL,
   thumbUp TINYINT(1) NOT NULL DEFAULT 0,
   id VARCHAR(10) NOT NULL,
   name VARCHAR(255) NOT NULL,
   price VARCHAR(100) NOT NULL,
   linkURL VARCHAR(4000) NOT NULL,
   score VARCHAR(10) NOT NULL,
   thumbUp TINYINT(1) NOT NULL DEFAULT 0,
   class VARCHAR(10) NOT NULL,
   currency VARCHAR(10) NOT NULL,
   review_count INT(6) NOT NULL DEFAULT 0,
   coupon_discount VARCHAR(50) NOT NULL,
   discount_ribbon VARCHAR(50) NOT NULL,
   award VARCHAR(10) NOT NULL,
   freeCancellation TINYINT(1) NOT NULL DEFAULT 0,
   options VARCHAR(100) NOT NULL,
   lastupdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`idx`),
   KEY `IDX_LASTUPDATE` (`lastupdate`)
 ) ENGINE=MyISAM CHARSET=utf8;
 */
public class AgodaTable {
    @Getter
    private static final String TableName = "AgodaHotelBase";

    public enum Column implements DBColumn {
        Idx("idx"),
        Domain("domain"),
        Keyword("keyword"),
        CheckIn("check_in"),
        CheckOut("check_out"),
        Type("type"),
        Sort("sort"),
        Rank("rank"),
        Id("id"),
        Name("name"),
        Price("price"),
        LinkURL("linkURL"),
        Score("score"),
        ThumbUpIcon("thumbUp"),
        Class("class"),
        Currency("currency"),
        ReviewCount("review_count"),
        CouponDiscount("coupon_discount"),
        DiscountRibbon("discount_ribbon"),
        Award("award"),
        FreeCancellation("freeCancellation"),
        Option("options"),
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
