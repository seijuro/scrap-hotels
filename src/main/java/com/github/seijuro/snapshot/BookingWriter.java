package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.BookingTable;
import com.github.seijuro.site.com.booking.data.BookingHotel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class BookingWriter extends AbstractDBWritter<BookingHotel> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(BookingTable.getTableName()).append("(")
                .append(BookingTable.Column.Domain.getColumnName()).append(", ")
                .append(BookingTable.Column.Keyword.getColumnName()).append(", ")
                .append(BookingTable.Column.CheckIn.getColumnName()).append(", ")
                .append(BookingTable.Column.CheckOut.getColumnName()).append(", ")
                .append(BookingTable.Column.Type.getColumnName()).append(", ")
                .append(BookingTable.Column.Sort.getColumnName()).append(", ")
                .append(BookingTable.Column.Rank.getColumnName()).append(", ")
                .append(BookingTable.Column.Id.getColumnName()).append(", ")
                .append(BookingTable.Column.Name.getColumnName()).append(", ")
                .append(BookingTable.Column.Price.getColumnName()).append(", ")
                .append(BookingTable.Column.LinkURL.getColumnName()).append(", ")
                .append(BookingTable.Column.Score.getColumnName()).append(", ")
                .append(BookingTable.Column.ThumbUpIcon.getColumnName()).append(", ")
                .append(BookingTable.Column.Class.getColumnName()).append(", ")
                .append(BookingTable.Column.Tag.getColumnName()).append(", ")
                .append(BookingTable.Column.Conditions.getColumnName()).append(", ")
                .append(BookingTable.Column.Ribbons.getColumnName()).append(", ")
                .append(BookingTable.Column.Deals.getColumnName()).append(", ")
                .append(BookingTable.Column.Banner.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
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
    public BookingWriter(MySQLConnectionString connString) throws Exception {
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
    public BookingWriter(Connection conn) throws Exception {
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
    public void write(BookingHotel[] data, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());
            int currentRank = fromRank;

            for (BookingHotel datum : data) {
                try {
                    StringBuffer condsBuilder = new StringBuffer();
                    for (String condition : datum.getConditions()) {
                        if (condsBuilder.length() > 0) {
                            condsBuilder.append(",");
                        }
                        condsBuilder.append(condition);
                    }

                    StringBuffer ribbonsBuilder = new StringBuffer();
                    for (String ribbon : datum.getRibbon()) {
                        if (ribbonsBuilder.length() > 0) {
                            ribbonsBuilder.append(",");
                        }
                        ribbonsBuilder.append(ribbon);
                    }

                    StringBuffer dealsBuilder = new StringBuffer();
                    for (String deal : datum.getDeals()) {
                        if (dealsBuilder.length() > 0) {
                            dealsBuilder.append(",");
                        }
                        dealsBuilder.append(deal);
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
                    stmt.setString(10, StringUtils.stripToEmpty(datum.getPrice()));
                    stmt.setString(11, StringUtils.stripToEmpty(datum.getLinkURL()));
                    stmt.setString(12, StringUtils.stripToEmpty(datum.getScore()));
                    stmt.setInt(13, datum.isThumbUpIcon() ? 1 : 0);
                    stmt.setString(14, StringUtils.stripToEmpty(datum.getClazz()));
                    stmt.setString(15, StringUtils.stripToEmpty(datum.getTag()));
                    stmt.setString(16, StringUtils.stripToEmpty(condsBuilder.toString()));
                    stmt.setString(17, StringUtils.stripToEmpty(ribbonsBuilder.toString()));
                    stmt.setString(18, StringUtils.stripToEmpty(dealsBuilder.toString()));
                    stmt.setString(19, StringUtils.stripToEmpty(datum.getBanner()));

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