package com.github.seijuro.scrap;

import com.github.seijuro.search.SearchURL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

@Log4j2
public abstract class AbstractScraper implements Scraper {
    public void createNewTab() {
    }
    /**
     * Instance Properties
     */
    @Getter(AccessLevel.PROTECTED)
    private WebDriver driver;

    /**
     * Constrcut <code>AbstractScraper</code> object.
     *
     * @param driver
     */
    public AbstractScraper(WebDriver driver) {
        Objects.requireNonNull(driver);

        this.driver = driver;
    }

    /**
     * get next request URL, if exists.
     * Otherwise, return null.
     *
     * @return
     */
    public abstract String getNextRequestURL();

    public abstract SearchURL getNextSearchURL();

    @Override
    public void scrap(String requestURL, long sleepMillis) throws Exception {
        Objects.requireNonNull(driver);

        if (Objects.nonNull(requestURL)) {
            //  Log
            log.debug("requestURL : {}", requestURL);

            driver.navigate().to(requestURL);
        }

        Thread.sleep(sleepMillis);
    }
}
