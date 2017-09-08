package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.ExpediaHotelReviewTable;
import com.github.seijuro.site.com.expedia.data.ExpediaHotelReview;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class ExpediaHotelReviewWriter extends AbstractDBWritter<ExpediaHotelReview> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(ExpediaHotelReviewTable.getTableName()).append("(")
                .append(ExpediaHotelReviewTable.Column.Id.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.HotelId.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.PostDate.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.Score.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.Title.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.Locale.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.HasResponse.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.Reviewer.getColumnName()).append(", ")
                .append(ExpediaHotelReviewTable.Column.Country.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public ExpediaHotelReviewWriter(MySQLConnectionString connString) throws Exception {
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

    public ExpediaHotelReviewWriter(Connection conn) throws NullPointerException {
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
    public void write(ExpediaHotelReview[] hotelDetails, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());

            for (ExpediaHotelReview hotelReview : hotelDetails) {
                try {
                    stmt.setString(1, StringUtils.stripToEmpty(hotelReview.getReviewId()));
                    stmt.setString(2, StringUtils.stripToEmpty(hotelReview.getHotelId()));
                    stmt.setString(3, StringUtils.stripToEmpty(hotelReview.getPostDate()));
                    stmt.setString(4, StringUtils.stripToEmpty(hotelReview.getScore()));
                    stmt.setString(5, StringUtils.stripToEmpty(hotelReview.getTitle()));
                    stmt.setString(6, StringUtils.stripToEmpty(hotelReview.getLocale()));
                    stmt.setInt(7, hotelReview.hasResponse() ? 1 : 0);
                    stmt.setString(8, StringUtils.stripToEmpty(hotelReview.getReviewer()));
                    stmt.setString(9, StringUtils.stripToEmpty(hotelReview.getLocation()));

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
