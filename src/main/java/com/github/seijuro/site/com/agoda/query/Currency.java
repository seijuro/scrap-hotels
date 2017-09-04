package com.github.seijuro.site.com.agoda.query;

import com.github.seijuro.search.query.QueryParameter;
import lombok.Getter;

public enum Currency implements QueryParameter{
    KRW("KRW");

    @Override
    public String getQueryParameter() {
        return currency;
    }

    @Getter
    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }
}
