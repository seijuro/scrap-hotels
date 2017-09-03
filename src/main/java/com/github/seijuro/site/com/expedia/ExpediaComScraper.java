package com.github.seijuro.site.com.expedia;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.writer.HTMLWriter;
import com.github.seijuro.site.com.expedia.query.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

@Log4j2
public class ExpediaComScraper extends AbstractScraper {
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
    private Date startDate;
    @Setter
    private Date endDate;
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


    public ExpediaComScraper(WebDriver driver) {
        super(driver);
    }

    public void scrap() throws Exception {
        for (Destination destination : destinations) {
            currentDestination = destination;
            for (com.github.seijuro.search.query.Sort sort : sorts) {
                currentSort = sort;
                currentPage = 1;

                boolean hasNextPage = false;

                do {
                    SearchURL searchURL = getNextSearchURL();

                    scrap(searchURL, sleepMillis);

                    WebDriver driver = getDriver();

                    WebElement paginationElement = driver.findElement(By.id("paginationContainer"));
                    WebElement searchResultElement = driver.findElement(By.id("resultsContainer"));
                    Document paginationDocument = Jsoup.parse(paginationElement.getAttribute("outerHTML"));

                    Elements pageElements = paginationDocument.select("button.pagination-label");

                    int currentPageIndex = Integer.parseInt(paginationDocument.select("button.pagination-label.selected").get(0).attr("value"));
                    int lastPageIndex = Integer.parseInt(pageElements.last().attr("value"));

                    //  Log
                    log.debug("destination : {}, sort : {}, current page : {}, last page : {}",
                            getCurrentDestination().getDestination(),
                            getCurrentSort().getQueryParameter(),
                            currentPageIndex,
                            lastPageIndex);

                    if (Objects.nonNull(htmlWriter)) {
                        String pageHTML = searchResultElement.getAttribute("outerHTML");
                        htmlWriter.write(searchURL, pageHTML);
                    }

                    hasNextPage = currentPageIndex != lastPageIndex;
                    currentPage = currentPageIndex + 1;
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
        urlBuilder.setAdults(adults);
        urlBuilder.setChildren(childrenAges);
        urlBuilder.setStartDate(startDate);
        urlBuilder.setEndDate(endDate);
        urlBuilder.setSort(currentSort);
        urlBuilder.setLodgings(lodgings);
        urlBuilder.setPage(currentPage);

        return urlBuilder.build();
    }
}
