package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class HotelsTable {
    @Getter
    private static final String TableName = "HotelsCom";

    public enum Column implements DBColumn {
        Idx("idx"),
        Domain("domain"),
        Keyword("keyword"),
        CheckIn("check_in"),
        CheckOut("check_out"),
        Type("type"),
        Sort("sort"),
        Rank("rank"),
        Id("id"),
        SupplierHotelId("supplier_hotel_id"),
        Name("name"),
        Address("address"),
        PDPDescriptionURL("url_pdp_description"),
        PDPMapURL("url_pdp_map"),
        PDPReviewURL("url_pdp_reviews"),
        GuestReviewsRating("gueset_reviews_rating"),
        GuestReviewsTotal("gueset_reviews_total"),
        GuestReviewsTotalText("gueset_reviews_total_text"),
        GuestReviewsScale("gueset_reviews_scale"),
        GuestReviewsBadge("gueset_reviews_badge"),
        GuestReviewsBadgeText("gueset_reviews_badgeText"),
        RatePlanPriceCurrent("rate_plan_price_current"),
        RatePlanFreeCancellation("rate_plan_freecancellation"),
        DealsSpecialDeal("deals_speicaldeal"),
        BadgingHotelBadgeType("badging_hotelbadge_type"),
        BadgingHotelBadgeLabel("badging_hotelbadge_label"),
        SponsoredURL("sponsored_url"),
        Lastupdate("lastupdate");

        /**
         * Instance Properties
         */
        private final String columnName;

        /**
         * Construct
         *
         * @param name
         */
        Column (String name) {
            columnName = name;
        }

        @Override
        public String getColumnName() {
            return columnName;
        }
    }
}
