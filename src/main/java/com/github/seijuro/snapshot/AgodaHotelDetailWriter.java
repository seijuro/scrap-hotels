package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.AgodaDetailTable;
import com.github.seijuro.site.com.agoda.data.AgodaHotelDetail;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class AgodaHotelDetailWriter extends AbstractDBWritter<AgodaHotelDetail> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(AgodaDetailTable.getTableName()).append("(")
                .append(AgodaDetailTable.Column.Id.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.Name.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.Address.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.ConstructedYear.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.Floor.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.Rooms.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.Beach.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.HasRestaurant.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.HasPool.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.HasFitness.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.HasCasino.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.TaxIncluded.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.BreakfastInclude.getColumnName()).append(", ")
                .append(AgodaDetailTable.Column.AgodaReviewCount.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public AgodaHotelDetailWriter(MySQLConnectionString connString) throws Exception {
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

    public AgodaHotelDetailWriter(Connection conn) throws NullPointerException {
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
    public void write(AgodaHotelDetail[] hotelDetails, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());

            for (AgodaHotelDetail hotelDetail : hotelDetails) {
                try {
                    stmt.setString(1, StringUtils.stripToEmpty(hotelDetail.getId()));
                    stmt.setString(2, StringUtils.stripToEmpty(hotelDetail.getName()));
                    stmt.setString(3, StringUtils.stripToEmpty(hotelDetail.getAddress()));
                    stmt.setString(4, StringUtils.stripToEmpty(hotelDetail.getBuiltDate()));
                    stmt.setString(5, StringUtils.stripToEmpty(hotelDetail.getFloors()));
                    stmt.setString(6, StringUtils.stripToEmpty(hotelDetail.getRooms()));
                    stmt.setString(7, StringUtils.stripToEmpty(hotelDetail.getBeachText()));
                    stmt.setInt(8, hotelDetail.hasRestaurant() ? 1 : 0);
                    stmt.setInt(9, hotelDetail.hasPool() ? 1 : 0);
                    stmt.setInt(10, hotelDetail.hasFitness() ? 1 : 0);
                    stmt.setInt(11, hotelDetail.hasCasino() ? 1 : 0);
                    stmt.setInt(12, hotelDetail.isTaxIncluded() ? 1 : 0);
                    stmt.setInt(13, hotelDetail.isBreakfastInclude() ? 1 : 0);
                    stmt.setInt(14, hotelDetail.getAgodaReviewCount());

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