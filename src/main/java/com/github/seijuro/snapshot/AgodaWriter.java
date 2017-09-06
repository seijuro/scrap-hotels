package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.AgodaTable;
import com.github.seijuro.site.com.agoda.data.AgodaHotel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class AgodaWriter extends AbstractDBWritter<AgodaHotel> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(AgodaTable.getTableName()).append("(")
                .append(AgodaTable.Column.Domain.getColumnName()).append(", ")
                .append(AgodaTable.Column.Keyword.getColumnName()).append(", ")
                .append(AgodaTable.Column.CheckIn.getColumnName()).append(", ")
                .append(AgodaTable.Column.CheckOut.getColumnName()).append(", ")
                .append(AgodaTable.Column.Type.getColumnName()).append(", ")
                .append(AgodaTable.Column.Sort.getColumnName()).append(", ")
                .append(AgodaTable.Column.Rank.getColumnName()).append(", ")
                .append(AgodaTable.Column.Id.getColumnName()).append(", ")
                .append(AgodaTable.Column.Name.getColumnName()).append(", ")
                .append(AgodaTable.Column.Price.getColumnName()).append(", ")
                .append(AgodaTable.Column.LinkURL.getColumnName()).append(", ")
                .append(AgodaTable.Column.Score.getColumnName()).append(", ")
                .append(AgodaTable.Column.ThumbUpIcon.getColumnName()).append(", ")
                .append(AgodaTable.Column.Class.getColumnName()).append(", ")
                .append(AgodaTable.Column.Currency.getColumnName()).append(", ")
                .append(AgodaTable.Column.ReviewCount.getColumnName()).append(", ")
                .append(AgodaTable.Column.CouponDiscount.getColumnName()).append(", ")
                .append(AgodaTable.Column.DiscountRibbon.getColumnName()).append(", ")
                .append(AgodaTable.Column.Award.getColumnName()).append(", ")
                .append(AgodaTable.Column.FreeCancellation.getColumnName()).append(", ")
                .append(AgodaTable.Column.Option.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        UpsertSQL = sb.toString();
    }

    /**
     * C'tor
     *
     * @param connString
     * @throws Exception
     */
    public AgodaWriter(MySQLConnectionString connString) throws Exception {
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

    public AgodaWriter(Connection conn) throws NullPointerException {
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
    public void write(AgodaHotel[] hotels, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());
            int currentRank = fromRank;

            for (AgodaHotel hotel : hotels) {
                /**
                 .append(AgodaTable.Column.DiscountRibbon.getColumnName()).append(", ")
                 .append(AgodaTable.Column.Award.getColumnName()).append(", ")
                 .append(AgodaTable.Column.FreeCancellation.getColumnName()).append(", ")
                 .append(AgodaTable.Column.Option.getColumnName()).append(") ")
                 */

                try {
                    StringBuffer optionsBuilder = new StringBuffer();
                    for (String option : hotel.getOptions()) {
                        if (optionsBuilder.length() > 0) {
                            optionsBuilder.append(",");
                        }
                        optionsBuilder.append(option);
                    }

                    stmt.setString(1, StringUtils.stripToEmpty(domain));
                    stmt.setString(2, StringUtils.stripToEmpty(query));
                    stmt.setString(3, StringUtils.stripToEmpty(checkIn));
                    stmt.setString(4, StringUtils.stripToEmpty(checkOut));
                    stmt.setString(5, StringUtils.stripToEmpty(type));
                    stmt.setString(6, StringUtils.stripToEmpty(sort));
                    stmt.setInt(7, currentRank++);
                    stmt.setString(8, StringUtils.stripToEmpty(hotel.getId()));
                    stmt.setString(9, StringUtils.stripToEmpty(hotel.getName()));
                    stmt.setString(10, StringUtils.stripToEmpty(hotel.getPrice()));
                    stmt.setString(11, StringUtils.stripToEmpty(hotel.getLinkURL()));
                    stmt.setString(12, StringUtils.stripToEmpty(hotel.getReviewsScore()));
                    stmt.setInt(13, hotel.isThumbUp() ? 1 : 0);
                    stmt.setString(14, StringUtils.stripToEmpty(hotel.getStarRating()));
                    stmt.setString(15, StringUtils.stripToEmpty(hotel.getCurrency()));
                    stmt.setInt(16, hotel.getReviewCount());
                    stmt.setString(17, StringUtils.stripToEmpty(hotel.getCouponDiscount()));
                    stmt.setString(18, StringUtils.stripToEmpty(hotel.getDiscountRibbon()));
                    stmt.setString(19, StringUtils.stripToEmpty(hotel.getAward()));
                    stmt.setInt(20, hotel.isFreeCancellation() ? 1 : 0);
                    stmt.setString(21, StringUtils.stripToEmpty(optionsBuilder.toString()));

                    stmt.executeUpdate();
                }
                catch (SQLException excp) {
                    //  Log
                    log.error("(PreparedStatement) binding error -> data : {}", hotel.toString());

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
