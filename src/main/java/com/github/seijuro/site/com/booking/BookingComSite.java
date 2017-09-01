package com.github.seijuro.site.com.booking;

import com.github.seijuro.site.Site;

public class BookingComSite implements Site {
    @Override
    public String getName() {
        return "Booking.com";
    }

    @Override
    public String getBaseURL() {
        return "https://www.booking.com";
    }
}
