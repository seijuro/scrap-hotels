package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.SnapshotTable;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Objects;

public class SnapshotWriter extends AbstractSnapshotDBWriter {
    private static String getUpsertSQL(int year) {
        StringBuffer sb = new StringBuffer("INSERT INTO ");
        sb.append(SnapshotTable.getTableName(year)).append("(")
                .append(SnapshotTable.Column.URL.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Param1.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Param2.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Param3.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Param4.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Param5.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Response.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Result.getColumnName()).append(", ")
                .append(SnapshotTable.Column.Revision.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ")
                .append("ON DUPLICATE KEY UPDATE ")
                .append(SnapshotTable.Column.Response.getColumnName()).append("=?, ")
                .append(SnapshotTable.Column.Result.getColumnName()).append("=?");
        return sb.toString();
    }

    public SnapshotWriter(MySQLConnectionString connString) throws Exception {
        super(null);

        Objects.requireNonNull(connString);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connString.toConnectionString(), connString.getUser(), connString.getPassword());
            connection.setAutoCommit(false);

            setConnection(connection);
        }
        catch (Exception excp) {
            throw excp;
        }
    }

    public SnapshotWriter(Connection conn) throws Exception {
        super(conn);

        Objects.requireNonNull(conn);
    }

    @Override
    public int write(SnapshotRequest request) {
        try {
            String sql = getUpsertSQL(request.getYear());
            PreparedStatement stmt = getConnection().prepareStatement(sql);

            stmt.setString(1, request.getUrl());
            stmt.setString(2, StringUtils.stripToEmpty(request.getParam1()));
            stmt.setString(3, StringUtils.stripToEmpty(request.getParam2()));
            stmt.setString(4, StringUtils.stripToEmpty(request.getParam3()));
            stmt.setString(5, StringUtils.stripToEmpty(request.getParam4()));
            stmt.setString(6, StringUtils.stripToEmpty(request.getParam5()));
            stmt.setString(7, request.getResponse());
            stmt.setString(8, request.getResult());
            stmt.setInt(9, request.getRevision());
            stmt.setString(10, request.getResponse());
            stmt.setString(11, request.getResult());

            return stmt.executeUpdate();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return Integer.MIN_VALUE;
    }
}
