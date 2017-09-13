package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.TripAdvisorReviewerProfileTable;
import com.github.seijuro.site.com.tripadvisor.data.TripAdvisorReviewerProfile;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class TripAdvisorReviewerProfileWriter extends AbstractDBWritter<TripAdvisorReviewerProfile> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(TripAdvisorReviewerProfileTable.getTableName()).append("(")
                .append(TripAdvisorReviewerProfileTable.Column.ReviewId.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.Name.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.Year.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.Sex.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.Country.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.ReviewCount.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.StartRating5.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.StartRating4.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.StartRating3.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.StartRating2.getColumnName()).append(", ")
                .append(TripAdvisorReviewerProfileTable.Column.StartRating1.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public TripAdvisorReviewerProfileWriter(MySQLConnectionString connString) throws Exception {
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

    public TripAdvisorReviewerProfileWriter(Connection conn) throws NullPointerException {
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
    public void write(TripAdvisorReviewerProfile[] hotelDetails, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());

            for (TripAdvisorReviewerProfile hotelReview : hotelDetails) {
                try {
                    stmt.setString(1, StringUtils.stripToEmpty(hotelReview.getReviewId()));
                    stmt.setString(2, StringUtils.stripToEmpty(hotelReview.getReviewerName()));
                    stmt.setString(3, StringUtils.stripToEmpty(hotelReview.getAge()));
                    stmt.setString(4, StringUtils.stripToEmpty(hotelReview.getSex()));
                    stmt.setString(5, StringUtils.stripToEmpty(hotelReview.getCountry()));
                    stmt.setInt(6, hotelReview.getNumberOfPosts());
                    stmt.setInt(7, hotelReview.getGuestRating_Star5());
                    stmt.setInt(8, hotelReview.getGuestRating_Star4());
                    stmt.setInt(9, hotelReview.getGuestRating_Star3());
                    stmt.setInt(10, hotelReview.getGuestRating_Star2());
                    stmt.setInt(11, hotelReview.getGuestRating_Star1());

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
