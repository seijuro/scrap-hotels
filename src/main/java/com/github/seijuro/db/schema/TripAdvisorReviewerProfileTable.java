package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class TripAdvisorReviewerProfileTable {
    @Getter
    private static final String TableName = "TripAdvisor_ReviewerProfile";

    public enum Column implements DBColumn {
        Idx("idx"),
        ReviewId("review_id"),
        Name("name"),
        Year("year"),
        Sex("sex"),
        Country("country"),
        ReviewCount("review_count"),
        StartRating5("reviewer_rating_5"),
        StartRating4("reviewer_rating_4"),
        StartRating3("reviewer_rating_3"),
        StartRating2("reviewer_rating_2"),
        StartRating1("reviewer_rating_1"),
        Timestamp("ts");

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
