package com.github.seijuro.db.schema;

import com.github.seijuro.snapshot.DBColumn;
import lombok.Getter;

public class ExpediaTable {
    @Getter
    private static final String TableName = "ExpediaCom_URL";

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
        Name("name"),
        SecondaryName("secondaryName"),
        Price("price"),
        LinkURL("linkURL"),
        Rating("rating"),
        AveragePerNight("avgPerNight"),
        Sale("sale"),
        SalePercentOff("salePercentOff"),
        SaleTag("saleTag"),
        NPP("npp"),
        VIP("vip"),
        Badge("badge"),
        FreeCancellation("freeCancel"),
        DiscountRibbons("discountRibbons"),
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
        Column(String name) {
            columnName = name;
        }

        @Override
        public String getColumnName() {
            return columnName;
        }
    }
}
