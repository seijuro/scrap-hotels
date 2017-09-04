package com.github.seijuro.site.com.agoda.query;

public class Date implements com.github.seijuro.search.query.Date {
    int year;
    int month;
    int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public String getQueryParameter() {
        return String.format("%d.%02d.%02d", year, month, day);
    }
}

