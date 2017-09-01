package com.github.seijuro.site.com.booking.query;

import org.joda.time.DateTime;

public class Query {
    protected Query(Builder builder) {

    }

    public static class Builder {
        /**
         * Instance Properties
         */
        int checkInYear;
        int checkInMonth;
        int checkInDay;

        int checkOutYear;
        int checkOutMonth;
        int checkOutDay;

        String destId = null;
        String destType = null;

        String uncheckedFilter = null;

        int groupAdult;
        int groupChildren;
        int noRoom;

        public Builder() {
            DateTime dt = new DateTime();

            checkInYear = dt.getYear();
            checkInMonth = dt.getMonthOfYear();
            checkInDay = dt.getDayOfMonth();

            dt.plusDays(1);

            checkOutYear = dt.getYear();
            checkOutMonth = dt.getMonthOfYear();
            checkOutDay = dt.getDayOfMonth();

            groupAdult = 2;
            groupChildren = 0;
            noRoom = 1;

            uncheckedFilter = "hoteltype";
        }


        /**
         * Builder Pattern class
         *
         * @return
         */
        public Query build() {
            return new Query(this);


        }
    }
}
