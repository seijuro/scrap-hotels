package com.github.seijuro.site.com.agoda;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.search.query.Destination;
import com.github.seijuro.search.query.Lodging;
import com.github.seijuro.site.com.agoda.query.CheckIn;
import com.github.seijuro.site.com.agoda.query.CheckOut;
import com.github.seijuro.writer.HTMLWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
public class AgodaScraper extends AbstractScraper {
    @Getter
    public static final long DefaultSleepMillis = 10L * DateUtils.MILLIS_PER_SECOND;

    @Setter
    HTMLWriter htmlWriter = null;
    @Setter
    private List<Destination> destinations;
    @Setter
    private int adults = /* default */2;
    @Setter
    private List<Integer> childrenAges = null;
    @Setter
    private CheckIn startDate;
    @Setter
    private CheckOut endDate;
    @Setter
    private List<com.github.seijuro.search.query.Sort> sorts;
    @Setter
    private List<Lodging> lodgings;
    @Setter
    private long sleepMillis = getDefaultSleepMillis();

    @Getter
    private Destination currentDestination = null;
    @Getter
    private com.github.seijuro.search.query.Sort currentSort = null;
    @Getter
    private int currentPage = 0;

    public AgodaScraper(WebDriver driver) {
        super(driver);
    }

    public static Map<String, Integer> rank = new HashMap<>();

    public void scrap() throws Exception {
        for (Destination destination : destinations) {
            currentDestination = destination;

            for (com.github.seijuro.search.query.Sort sort : sorts) {
                currentSort = sort;
                currentPage = 1;

                boolean hasNextPage = false;

                do {
                    SearchURL searchURL = getNextSearchURL();

                    //  Log
                    log.debug("destination : {}, sort : {}, url : {}", destination, sort, searchURL.toURL());

                    scrap(searchURL, sleepMillis);

                    WebDriver driver = getDriver();

                    WebElement paginationElement = driver.findElement(By.id("paginationContainer"));

                    if (Objects.nonNull(htmlWriter)) {
                        String pageHTML = driver.getPageSource();

                        htmlWriter.write(searchURL, pageHTML);
                    }

                    ++currentPage;
                } while (hasNextPage);
            }
        }
    }

    @Override
    public String getNextRequestURL() {
        return null;
    }

    @Override
    public SearchURL getNextSearchURL() {
        SearchURL.Builder urlBuilder = new SearchURL.Builder();
        urlBuilder.setDestination(currentDestination);
        urlBuilder.setChildren(childrenAges);
        urlBuilder.setCheckIn(startDate);
        urlBuilder.setCheckOut(endDate);
        urlBuilder.setSort(currentSort);
        urlBuilder.setLodgings(lodgings);
        urlBuilder.setPage(currentPage);

        return urlBuilder.build();
    }
}
