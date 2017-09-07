package com.github.seijuro.parser;

import com.github.seijuro.site.HotelBase;

import java.util.List;

public interface HTMLPageParser<T> {
    public abstract List<T> parse(String html);
}
