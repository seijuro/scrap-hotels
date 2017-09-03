package com.github.seijuro.site.com.expedia.query;

import com.github.seijuro.search.query.QueryParameter;
import lombok.Getter;

public enum Lodging implements com.github.seijuro.search.query.Lodging {
    HOTEL("호텔", "hotel"),
    VILLA("빌라", "villa"),
    HOTEL_RESORT("호텔 리조트", "hotelResort"),
    HOSTEL("호스텔", "hostel"),
    BED_BREAKFAST("B&B", "bedBreakfast");

    @Getter
    private final String label;
    @Getter
    private final String lodging;

    public String getQueryParameter() {
        return lodging;
    }

    Lodging(String label, String lodging) {
        this.label = label;
        this.lodging = lodging;
    }
}
