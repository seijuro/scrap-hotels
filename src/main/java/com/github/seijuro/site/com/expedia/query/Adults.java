package com.github.seijuro.site.com.expedia.query;

import com.github.seijuro.search.query.QueryParameter;

public class Adults implements QueryParameter {
    int adults;

    /**
     * Construct
     *
     * @param adults
     */
    public Adults(int adults) {
        this.adults = adults;
    }

    @Override
    public String getQueryParameter() {
        return Integer.toString(adults);
    }
}
