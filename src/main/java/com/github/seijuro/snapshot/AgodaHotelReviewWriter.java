package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.AgodaHotelReviewTable;
import com.github.seijuro.site.com.agoda.data.AgodaHotelReview;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class AgodaHotelReviewWriter extends AbstractDBWritter<AgodaHotelReview> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(AgodaHotelReviewTable.getTableName()).append("(")
                .append(AgodaHotelReviewTable.Column.Id.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.HotelId.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.PostDate.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.StayDate.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.StayDays.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.RoomType.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.Score.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.Title.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.Locale.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.HasResponse.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.Reviewer.getColumnName()).append(", ")
                .append(AgodaHotelReviewTable.Column.Country.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public AgodaHotelReviewWriter(MySQLConnectionString connString) throws Exception {
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

    public AgodaHotelReviewWriter(Connection conn) throws NullPointerException {
        super(conn);
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
    public void write(AgodaHotelReview[] hotelReviews, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());

            for (AgodaHotelReview hotelReview : hotelReviews) {
                try {
                    stmt.setString(1, StringUtils.stripToEmpty(hotelReview.getReviewId()));
                    stmt.setString(2, StringUtils.stripToEmpty(hotelReview.getHotelId()));
                    stmt.setString(3, StringUtils.stripToEmpty(hotelReview.getPostDate()));
                    stmt.setString(4, StringUtils.stripToEmpty(hotelReview.getStayDate()));
                    stmt.setString(5, StringUtils.stripToEmpty(hotelReview.getStayDays()));
                    stmt.setString(6, StringUtils.stripToEmpty(hotelReview.getRoomType()));
                    stmt.setString(7, StringUtils.stripToEmpty(hotelReview.getScore()));
                    stmt.setString(8, StringUtils.stripToEmpty(hotelReview.getTitle()));
                    stmt.setString(9, StringUtils.stripToEmpty(hotelReview.getLocale()));
                    stmt.setInt(10, hotelReview.hasResponse() ? 1 : 0);
                    stmt.setString(11, StringUtils.stripToEmpty(hotelReview.getReviewer()));
                    stmt.setString(12, StringUtils.stripToEmpty(hotelReview.getLocation()));

                    stmt.executeUpdate();
                }
                catch (SQLException excp) {
                    //  Log
                    log.error("(PreparedStatement) binding error -> data : {}", hotelReview.toString());

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