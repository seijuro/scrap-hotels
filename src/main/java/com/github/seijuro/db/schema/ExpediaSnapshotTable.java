package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class ExpediaSnapshotTable {
    @Getter
    private static final String TableName = "ExpediaCom_Snapshot";

    public enum Column implements DBColumn {
        Idx("idx"),
        Domain("domain"),
        HotelId("hotel_id"),
        HotelURL("hotel_url"),
        Parameter1("param1"),
        Parameter2("param2"),
        Parameter3("param3"),
        PageNumber("page"),
        HasReply("yn_reply"),
        HTMLPageSource("html"),
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
