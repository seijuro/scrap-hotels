package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class TripAdvisorURLTable {
    @Getter
    private static final String TableName = "TripAdvisor_URL";

    public enum Column implements DBColumn {
        Idx("idx"),
        ReviewId("domain"),
        Name("keyword"),
        Year("check_in"),
        Sex("check_out"),
        Country("type"),
        ReviewCount("sort"),
        StartRating5("rank"),
        StartRating4("id"),
        StartRating3("linknURL"),
        Timestamp("lastupdate");

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
