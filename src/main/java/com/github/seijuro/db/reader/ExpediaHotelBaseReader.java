package com.github.seijuro.db.reader;

import com.github.seijuro.db.schema.ExpediaTable;
import com.github.seijuro.snapshot.MySQLConnectionString;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@Log4j2
public class ExpediaHotelBaseReader  implements Closeable {
    @Getter
    private static String SelectDistinctHotelIdSQL;

    static {
        {
            StringBuffer sb = new StringBuffer("SELECT DISTINCT ");
            sb.append(ExpediaTable.Column.Id.getColumnName())
                    .append(" FROM ").append(ExpediaTable.getTableName())
                    .append(" ORDER BY ").append(ExpediaTable.Column.Id.getColumnName()).append(" ASC ");
            SelectDistinctHotelIdSQL = sb.toString();
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
    public ExpediaHotelBaseReader(MySQLConnectionString connString) throws Exception {
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

    public ExpediaHotelBaseReader(Connection conn) throws NullPointerException {
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

    public List<String> readHotelIds() {
        List<String> hotelIds = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = null;

            try {
                //  Log
                log.debug("QUERY : {}", getSelectDistinctHotelIdSQL());

                resultSet = stmt.executeQuery(getSelectDistinctHotelIdSQL());

                while (resultSet.next()) {
                    hotelIds.add(resultSet.getString(1));
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

        return hotelIds;
    }
}