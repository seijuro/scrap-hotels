package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.ExpediaHotelDetailTable;
import com.github.seijuro.site.com.expedia.data.ExpediaHotelDetail;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class ExpediaHotelDetailWriter extends  AbstractDBWritter<ExpediaHotelDetail> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(ExpediaHotelDetailTable.getTableName()).append("(")
                .append(ExpediaHotelDetailTable.Column.Id.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.Name.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.Address.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.ConstructedYear.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.Floor.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.Rooms.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.Beach.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.StarRating.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.GuestRating.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.HasMeetingRoom.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.HasRestaurant.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.HasPool.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.HasFitness.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.HasCasino.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.BreakfastInclude.getColumnName()).append(", ")
                .append(ExpediaHotelDetailTable.Column.ReviewCount.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public ExpediaHotelDetailWriter(MySQLConnectionString connString) throws Exception {
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

    public ExpediaHotelDetailWriter(Connection conn) throws NullPointerException {
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
    public void write(ExpediaHotelDetail[] hotelDetails, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());

            for (ExpediaHotelDetail hotelDetail : hotelDetails) {
                try {
                    stmt.setString(1, StringUtils.stripToEmpty(hotelDetail.getId()));
                    stmt.setString(2, StringUtils.stripToEmpty(hotelDetail.getName()));
                    stmt.setString(3, StringUtils.stripToEmpty(hotelDetail.getAddress()));
                    stmt.setString(4, StringUtils.stripToEmpty(hotelDetail.getBuiltDate()));
                    stmt.setString(5, StringUtils.stripToEmpty(hotelDetail.getFloors()));
                    stmt.setString(6, StringUtils.stripToEmpty(hotelDetail.getRooms()));
                    stmt.setString(7, StringUtils.stripToEmpty(hotelDetail.getBeach().getDescription()));
                    stmt.setString(8, StringUtils.stripToEmpty(hotelDetail.getStarRating()));
                    stmt.setString(9, StringUtils.stripToEmpty(hotelDetail.getBeach().getDescription()));
                    stmt.setInt(10, hotelDetail.hasMeetingRoom() ? 1 : 0);
                    stmt.setInt(11, hotelDetail.hasRestaurant() ? 1 : 0);
                    stmt.setInt(12, hotelDetail.hasPool() ? 1 : 0);
                    stmt.setInt(13, hotelDetail.hasFitness() ? 1 : 0);
                    stmt.setInt(14, hotelDetail.hasCasino() ? 1 : 0);
                    stmt.setInt(15, hotelDetail.isBreakfastInclude() ? 1 : 0);
                    stmt.setInt(16, hotelDetail.getReviewCount());

                    stmt.executeUpdate();
                }
                catch (SQLException excp) {
                    //  Log
                    log.error("(PreparedStatement) binding error -> data : {}", hotelDetail.toString());

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
