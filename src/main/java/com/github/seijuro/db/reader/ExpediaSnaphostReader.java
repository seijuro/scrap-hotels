package com.github.seijuro.db.reader;

import com.github.seijuro.db.schema.ExpediaSnapshotTable;
import com.github.seijuro.snapshot.MySQLConnectionString;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@Log4j2
public class ExpediaSnaphostReader implements Closeable{
    @Getter
    private static String SelectHTMLPageSQL;
    @Getter
    private static String SelectPageNumbersSQL;

    static {
        {
            StringBuffer sb = new StringBuffer("SELECT ");
            sb.append(ExpediaSnapshotTable.Column.HTMLPageSource.getColumnName())
                    .append(" FROM ").append(ExpediaSnapshotTable.getTableName())
                    .append(" WHERE ").append(ExpediaSnapshotTable.Column.Domain.getColumnName()).append("=? ")
                    .append(" AND ").append(ExpediaSnapshotTable.Column.HotelId.getColumnName()).append("=? ")
                    .append(" AND ").append(ExpediaSnapshotTable.Column.PageNumber.getColumnName()).append("=? ")
                    .append(" AND ").append(ExpediaSnapshotTable.Column.HasReply.getColumnName()).append("=? ")
                    .append(" ORDER BY ").append(ExpediaSnapshotTable.Column.PageNumber.getColumnName()).append(" ASC ");
            SelectHTMLPageSQL = sb.toString();
        }

        {
            StringBuffer sb = new StringBuffer("SELECT ");
            sb.append(ExpediaSnapshotTable.Column.PageNumber.getColumnName())
                    .append(" FROM ").append(ExpediaSnapshotTable.getTableName())
                    .append(" WHERE ").append(ExpediaSnapshotTable.Column.Domain.getColumnName()).append("=? ")
                    .append(" AND ").append(ExpediaSnapshotTable.Column.HotelId.getColumnName()).append("=? ")
                    .append(" AND ").append(ExpediaSnapshotTable.Column.HasReply.getColumnName()).append("=? ")
                    .append(" ORDER BY ").append(ExpediaSnapshotTable.Column.PageNumber.getColumnName()).append(" ASC ");
            SelectPageNumbersSQL = sb.toString();
        }
    }

    /**
     * Instance Proeperties
     */
    private Connection connection = null;

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public ExpediaSnaphostReader(MySQLConnectionString connString) throws Exception {
        if (connString == null) {
            throw new NullPointerException("Param, connString, is a null object.");
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connString.toConnectionString(), connString.getUser(), connString.getPassword());
            connection.setAutoCommit(false);
        }
        catch (Exception excp) {
            throw excp;
        }
    }

    public ExpediaSnaphostReader(Connection conn) throws NullPointerException {
        this.connection = conn;
    }

    @Override
    public void close() throws IOException {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
        finally {
            connection = null;
        }
    }

    public void commit() {
        try {
            if (connection != null) { connection.commit(); }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    @Getter
    private final static int DefaultCommitBlockSize = 200;

    public List<Integer> readPageNumbers(String domain, String hotelId, boolean replyYN) {
        List<Integer> pageNumbers = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(getSelectPageNumbersSQL());
            ResultSet resultSet = null;

            try {
                stmt.setString(1, domain);
                stmt.setString(2, hotelId);
                stmt.setInt(3, replyYN ? 1 : 0);

                resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    pageNumbers.add(resultSet.getInt(1));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (Objects.nonNull(resultSet)) { resultSet.close(); }
                if (Objects.nonNull(resultSet)) { stmt.close(); }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return pageNumbers;
    }

    public String readHTMLPageSource(String domain, String hotelId, int pageNumber, boolean replyYN) {
        String html = null;

        try {
            PreparedStatement stmt = connection.prepareStatement(getSelectHTMLPageSQL());
            ResultSet resultSet = null;

            try {
                stmt.setString(1, domain);
                stmt.setString(2, hotelId);
                stmt.setInt(3, pageNumber);
                stmt.setInt(4, replyYN ? 1 : 0);

                resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    html = new String(resultSet.getBytes(1), "UTF-8");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (Objects.nonNull(resultSet)) { resultSet.close(); }
                if (Objects.nonNull(resultSet)) { stmt.close(); }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return html;
    }
}