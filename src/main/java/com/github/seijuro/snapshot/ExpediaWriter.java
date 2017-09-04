package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.BookingTable;
import com.github.seijuro.db.schema.ExpediaTable;
import com.github.seijuro.site.com.booking.data.BookingHotel;
import com.github.seijuro.site.com.expedia.data.ExpediaHotel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class ExpediaWriter extends AbstractDBWritter<ExpediaHotel> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(ExpediaTable.getTableName()).append("(")
                .append(ExpediaTable.Column.Domain.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Keyword.getColumnName()).append(", ")
                .append(ExpediaTable.Column.CheckIn.getColumnName()).append(", ")
                .append(ExpediaTable.Column.CheckOut.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Type.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Sort.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Rank.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Id.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Name.getColumnName()).append(", ")
                .append(ExpediaTable.Column.SecondaryName.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Price.getColumnName()).append(", ")
                .append(ExpediaTable.Column.LinkURL.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Rating.getColumnName()).append(", ")
                .append(ExpediaTable.Column.AveragePerNight.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Sale.getColumnName()).append(", ")
                .append(ExpediaTable.Column.SalePercentOff.getColumnName()).append(", ")
                .append(ExpediaTable.Column.SaleTag.getColumnName()).append(", ")
                .append(ExpediaTable.Column.NPP.getColumnName()).append(", ")
                .append(ExpediaTable.Column.Badge.getColumnName()).append(", ")
                .append(ExpediaTable.Column.VIP.getColumnName()).append(", ")
                .append(ExpediaTable.Column.FreeCancellation.getColumnName()).append(", ")
                .append(ExpediaTable.Column.DiscountRibbons.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * Instance Properties
     */
    private Connection connection;

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public ExpediaWriter(MySQLConnectionString connString) throws Exception {
        super(null);

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

    /**
     * C'tor
     *
     * @param conn
     * @throws Exception
     */
    public ExpediaWriter(Connection conn) throws Exception {
        super(conn);

        if (conn == null) {
            throw new NullPointerException("Param, conn, is a null object.");
        }

        connection = conn;
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

    @Override
    public void write(ExpediaHotel[] data, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());
            int currentRank = fromRank;

            for (ExpediaHotel datum : data) {
                try {
                    StringBuffer ribbonsBuilder = new StringBuffer();
                    for (String ribbon : datum.getDiscountRibbons()) {
                        if (ribbonsBuilder.length() > 0) {
                            ribbonsBuilder.append(",");
                        }
                        ribbonsBuilder.append(ribbon);
                    }

                    stmt.setString(1, StringUtils.stripToEmpty(domain));
                    stmt.setString(2, StringUtils.stripToEmpty(query));
                    stmt.setString(3, StringUtils.stripToEmpty(checkIn));
                    stmt.setString(4, StringUtils.stripToEmpty(checkOut));
                    stmt.setString(5, StringUtils.stripToEmpty(type));
                    stmt.setString(6, StringUtils.stripToEmpty(sort));
                    stmt.setInt(7, currentRank++);
                    stmt.setString(8, StringUtils.stripToEmpty(datum.getId()));
                    stmt.setString(9, StringUtils.stripToEmpty(datum.getName()));
                    stmt.setString(10, StringUtils.stripToEmpty(datum.getSecondaryName()));
                    stmt.setString(11, StringUtils.stripToEmpty(datum.getPrice()));
                    stmt.setString(12, StringUtils.stripToEmpty(datum.getLinkURL()));
                    stmt.setString(13, StringUtils.stripToEmpty(datum.getRating()));
                    stmt.setString(14, StringUtils.stripToEmpty(datum.getAveragePerNight()));
                    stmt.setString(15, StringUtils.stripToEmpty(datum.getSale()));
                    stmt.setString(16, StringUtils.stripToEmpty(datum.getSalePercentOff()));
                    stmt.setString(17, StringUtils.stripToEmpty(datum.getSaleTag()));
                    stmt.setString(18, StringUtils.stripToEmpty(datum.getNpp()));
                    stmt.setString(19, StringUtils.stripToEmpty(datum.getBadge()));
                    stmt.setInt(20, datum.isVip() ? 1 : 0);
                    stmt.setString(21, StringUtils.stripToEmpty(datum.getFreeCancel()));
                    stmt.setString(22, StringUtils.stripToEmpty(ribbonsBuilder.toString()));

                    stmt.executeUpdate();
                }
                catch (SQLException excp) {
                    //  Log
                    log.error("(PreparedStatement) binding error -> data : {}", datum.toString());

                    excp.printStackTrace();
                }
            }

            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}