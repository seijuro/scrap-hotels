package com.github.seijuro.scrap;

import com.github.seijuro.search.SearchURL;

public interface Scraper {
    public void scrap(String requestURL, long sleepMillis) throws Exception;

    default public void scrap(SearchURL searchURL, long sleepMillis) throws Exception {
        scrap(searchURL.toURL(), sleepMillis);
    }
}
