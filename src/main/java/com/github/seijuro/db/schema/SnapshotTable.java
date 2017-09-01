package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class SnapshotTable {
    private static final String TablNamePrefix = "Snapshot";

    public static String getTableName(int year) {
        return String.format("%s%4d", TablNamePrefix, year);
    }

    public enum Column implements DBColumn {
        Idx("idx"),
        URL("url"),
        Param1("param1"),
        Param2("param2"),
        Param3("param3"),
        Param4("param4"),
        Param5("param5"),
        Response("response"),
        Result("result"),
        Revision("revision"),
        LastUpdate("lastupdate");

        @Getter
        private final String columnName;

        Column(String name) {
            columnName = name;
        }
    }
}
