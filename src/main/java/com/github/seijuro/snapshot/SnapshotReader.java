package com.github.seijuro.snapshot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class SnapshotReader extends AbstractSnapshotDBReader {
    private static String getSelectSQL(int year) {
        StringBuffer sb = new StringBuffer("SELECT ");
        sb.append(SnapshotTable.Column.Idx.getColumnName())
                .append(", ").append(SnapshotTable.Column.URL.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param1.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param2.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param3.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param4.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param5.getColumnName())
                .append(", ").append(SnapshotTable.Column.Result.getColumnName())
                .append(" FROM ").append(SnapshotTable.getTableName(year))
                .append(" WHERE ")
                .append(SnapshotTable.Column.Revision.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.URL.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.Param1.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.Param2.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.Param3.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.Param4.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.Param5.getColumnName()).append("=?");
        return sb.toString();
    }

    private static String getPeekSQL(SnapshotRequest key) {
        StringBuffer sb = new StringBuffer("SELECT ");

        sb.append(SnapshotTable.Column.Revision.getColumnName())
                .append(", ").append(SnapshotTable.Column.URL.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param1.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param2.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param3.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param4.getColumnName())
                .append(", ").append(SnapshotTable.Column.Param5.getColumnName())
                .append(", ").append(SnapshotTable.Column.Result.getColumnName());

        sb.append(" FROM ").append(SnapshotTable.getTableName(key.getYear()))
                .append(" WHERE ")
                .append(SnapshotTable.Column.Revision.getColumnName()).append("=? AND ")
                .append(SnapshotTable.Column.URL.getColumnName()).append("=? ");

        if (key.getParam1() != null) {
            sb.append(" AND ").append(SnapshotTable.Column.Param1.getColumnName()).append("=? ");

            if (key.getParam2() != null) {
                sb.append(" AND ").append(SnapshotTable.Column.Param2.getColumnName()).append("=? ");

                if (key.getParam3() != null) {
                    sb.append(" AND ").append(SnapshotTable.Column.Param3.getColumnName()).append("=? ");

                    if (key.getParam4() != null) {
                        sb.append(" AND ").append(SnapshotTable.Column.Param4.getColumnName()).append("=? ");
                    }
                }
            }
        }

        sb.append(" ORDER BY ").append(SnapshotTable.Column.Idx.getColumnName()).append(" DESC LIMIT 1");

        return sb.toString();
    }


    /**
     * Construct
     *
     * @param connString
     * @throws Exception
     */
    public SnapshotReader(MySQLConnectionString connString) throws Exception {
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

    /**
     * Construct
     *
     * @param conn
     */
    public SnapshotReader(Connection conn) {
        super(conn);

        Objects.requireNonNull(conn);
    }

    @Override
    public SnapshotResult read(SnapshotRequest key) {
        SnapshotResult ret = null;

        try {
            PreparedStatement stmt = getConnection().prepareStatement(getSelectSQL(key.getYear()));

            stmt.setInt(1, key.getRevision());
            stmt.setString(2, key.getUrl());
            stmt.setString(3, key.getParam1());
            stmt.setString(4, key.getParam2());
            stmt.setString(5, key.getParam3());
            stmt.setString(6, key.getParam4());
            stmt.setString(7, key.getParam5());

            ResultSet rs = stmt.executeQuery();
            SnapshotResult.Builder resultBuilder = new SnapshotResult.Builder();

            if (rs.next()) {
                resultBuilder.setIdx(rs.getLong(1));
                resultBuilder.setUrl(rs.getString(2));
                resultBuilder.setParam1(rs.getString(3));
                resultBuilder.setParam2(rs.getString(4));
                resultBuilder.setParam3(rs.getString(5));
                resultBuilder.setParam4(rs.getString(6));
                resultBuilder.setParam5(rs.getString(7));
                resultBuilder.setResult(rs.getString(8));

                ret = resultBuilder.build();
            }

            rs.close();
            stmt.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return ret;
    }
}
