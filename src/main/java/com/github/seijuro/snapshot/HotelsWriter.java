package com.github.seijuro.snapshot;

import com.github.seijuro.db.schema.HotelsTable;
import com.github.seijuro.site.com.hotels.result.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Log4j2
public class HotelsWriter extends AbstractDBWritter<Result> {
    @Getter
    private static String UpsertSQL;

    static {
        StringBuffer sb = new StringBuffer("INSERT IGNORE INTO ");
        sb.append(HotelsTable.getTableName()).append("(")
                .append(HotelsTable.Column.Domain.getColumnName()).append(", ")
                .append(HotelsTable.Column.Keyword.getColumnName()).append(", ")
                .append(HotelsTable.Column.CheckIn.getColumnName()).append(", ")
                .append(HotelsTable.Column.CheckOut.getColumnName()).append(", ")
                .append(HotelsTable.Column.Type.getColumnName()).append(", ")
                .append(HotelsTable.Column.Sort.getColumnName()).append(", ")
                .append(HotelsTable.Column.Rank.getColumnName()).append(", ")
                .append(HotelsTable.Column.Id.getColumnName()).append(", ")
                .append(HotelsTable.Column.SupplierHotelId.getColumnName()).append(", ")
                .append(HotelsTable.Column.Name.getColumnName()).append(", ")
                .append(HotelsTable.Column.Address.getColumnName()).append(", ")
                .append(HotelsTable.Column.PDPDescriptionURL.getColumnName()).append(", ")
                .append(HotelsTable.Column.PDPMapURL.getColumnName()).append(", ")
                .append(HotelsTable.Column.PDPReviewURL.getColumnName()).append(", ")
                .append(HotelsTable.Column.GuestReviewsRating.getColumnName()).append(", ")
                .append(HotelsTable.Column.GuestReviewsTotal.getColumnName()).append(", ")
                .append(HotelsTable.Column.GuestReviewsTotalText.getColumnName()).append(", ")
                .append(HotelsTable.Column.GuestReviewsScale.getColumnName()).append(", ")
                .append(HotelsTable.Column.GuestReviewsBadge.getColumnName()).append(", ")
                .append(HotelsTable.Column.GuestReviewsBadgeText.getColumnName()).append(", ")
                .append(HotelsTable.Column.RatePlanPriceCurrent.getColumnName()).append(", ")
                .append(HotelsTable.Column.RatePlanFreeCancellation.getColumnName()).append(", ")
                .append(HotelsTable.Column.DealsSpecialDeal.getColumnName()).append(", ")
                .append(HotelsTable.Column.BadgingHotelBadgeType.getColumnName()).append(", ")
                .append(HotelsTable.Column.BadgingHotelBadgeLabel.getColumnName()).append(", ")
                .append(HotelsTable.Column.SponsoredURL.getColumnName()).append(") ")
                .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
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
    public HotelsWriter(MySQLConnectionString connString) throws Exception {
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
    public HotelsWriter(Connection conn) throws Exception {
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
    public void write(Result[] data, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank) {
        try {
            PreparedStatement stmt = connection.prepareStatement(getUpsertSQL());
            int currentRank = fromRank;

            for (Result datum : data) {
                try {
                    Sponsored sponsored = datum.getSponsored();
                    Badging.HotelBadge hotelBadge = Objects.nonNull(datum.getBadging()) ? datum.getBadging().getHotelBadge() : null;
                    Deals.SpecialDeal specialDeal = Objects.nonNull(datum.getDeals()) ? datum.getDeals().getSpecialDeal() : null;
                    RatePlan.Price price = Objects.nonNull(datum.getRatePlan()) ? datum.getRatePlan().getPrice() : null;
                    RatePlan.Features features = Objects.nonNull(datum.getRatePlan()) ? datum.getRatePlan().getFeatures() : null;
                    boolean freeCancellation = Objects.nonNull(features.getFreeCancellation()) ? features.getFreeCancellation() : false;
                    GuestReviews guestReviews = datum.getGuestReviews();
                    URLs urls = datum.getUrLs();


                    stmt.setString(1, StringUtils.stripToEmpty(domain));
                    stmt.setString(2, StringUtils.stripToEmpty(query));
                    stmt.setString(3, StringUtils.stripToEmpty(checkIn));
                    stmt.setString(4, StringUtils.stripToEmpty(checkOut));
                    stmt.setString(5, StringUtils.stripToEmpty(type));
                    stmt.setString(6, StringUtils.stripToEmpty(sort));
                    stmt.setInt(7, currentRank++);
                    stmt.setInt(8, datum.getId());
                    stmt.setInt(9, datum.getSupplierHotelId());
                    stmt.setString(10, StringUtils.stripToEmpty(datum.getName()));
                    stmt.setString(11, StringUtils.stripToEmpty(datum.getAddress().toText()));
                    stmt.setString(12, Objects.nonNull(urls) ? StringUtils.stripToEmpty(datum.getUrLs().getPdpDescription()) : StringUtils.EMPTY);
                    stmt.setString(13, Objects.nonNull(urls) ? StringUtils.stripToEmpty(datum.getUrLs().getPdpMap()) : StringUtils.EMPTY);
                    stmt.setString(14, Objects.nonNull(urls) ? StringUtils.stripToEmpty(datum.getUrLs().getPdpReviews()) : StringUtils.EMPTY);
                    stmt.setString(15, Objects.nonNull(guestReviews) ? StringUtils.stripToEmpty(datum.getGuestReviews().getRating()) : StringUtils.EMPTY);
                    stmt.setInt(16, Objects.nonNull(guestReviews) ? datum.getGuestReviews().getTotal() : 0);
                    stmt.setString(17, Objects.nonNull(guestReviews) ? StringUtils.stripToEmpty(datum.getGuestReviews().getTotalText()) : StringUtils.EMPTY);
                    stmt.setInt(18, Objects.nonNull(guestReviews) ? datum.getGuestReviews().getScale() : 0);
                    stmt.setString(19, Objects.nonNull(guestReviews) ? StringUtils.stripToEmpty(datum.getGuestReviews().getBadge()) : StringUtils.EMPTY);
                    stmt.setString(20, Objects.nonNull(guestReviews) ? StringUtils.stripToEmpty(datum.getGuestReviews().getBadgeText()) : StringUtils.EMPTY);
                    stmt.setString(21, Objects.nonNull(price) ? StringUtils.stripToEmpty(price.getCurrent()) : StringUtils.EMPTY);
                    stmt.setInt(22, freeCancellation ? 1 : 0);
                    stmt.setString(23, Objects.nonNull(specialDeal) ? StringUtils.stripToEmpty(specialDeal.getDealText()) : StringUtils.EMPTY);
                    stmt.setString(24, Objects.nonNull(hotelBadge) ? StringUtils.stripToEmpty(hotelBadge.getType()) : StringUtils.EMPTY);
                    stmt.setString(25, Objects.nonNull(hotelBadge) ? StringUtils.stripToEmpty(hotelBadge.getLabel()) : StringUtils.EMPTY);
                    stmt.setString(26, Objects.nonNull(sponsored) ? StringUtils.stripToEmpty(sponsored.getImpressionTrackingUrl()) : StringUtils.EMPTY);

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
