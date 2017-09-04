package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class BookingTable {
    @Getter
    private static final String TableName = "BookingCom_URL";

    public enum Column implements DBColumn {
        Idx("idx"),
        Domain("domain"),
        Keyword("keyword"),
        CheckIn("check_in"),
        CheckOut("check_out"),
        Type("type"),
        Sort("sort"),
        Rank("rank"),
        ThumbUpIcon("thumbUp"),
        Id("id"),
        Name("name"),
        Price("price"),
        LinkURL("linkURL"),
        Score("score"),
        Class("class"),
        Tag("tag"),
        Conditions("conditions"),
        Ribbons("ribbons"),
        Deals("deals"),
        Banner("banner"),
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
