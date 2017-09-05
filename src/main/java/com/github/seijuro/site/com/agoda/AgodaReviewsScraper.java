package com.github.seijuro.site.com.agoda;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.search.SearchURL;
import lombok.Getter;
import org.openqa.selenium.WebDriver;

public class AgodaReviewsScraper extends AbstractScraper {
    @Getter
    public static final String BaseURL = "https://www.agoda.com";

    /**
     * Constrcut <code>AbstractScraper</code> object.
     *
     * @param driver
     */
    public AgodaReviewsScraper(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getNextRequestURL() {
        return null;
    }

    @Override
    public SearchURL getNextSearchURL() {
        return null;
    }
}
