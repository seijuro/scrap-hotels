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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.*;

@Log4j2
public class AgodaScraper extends AbstractScraper {
    @Getter
    public static final String BaseURL = "https://www.agoda.com";
    @Getter
    public static final long DefaultSleepMillis = 6L * DateUtils.MILLIS_PER_SECOND;

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

    public void scrapDetailsNReviews(AgodaHotelReviewHTMLWriter writer, String hotelId, String requestURL) {
        try {
            WebDriver driver = getDriver();
            JavascriptExecutor js = (JavascriptExecutor)driver;
            int startPosY = 0;
            int currentPage = 1;

            scrap(requestURL, sleepMillis);

            WebElement footerElement = driver.findElement(By.className("footer"));
            int posY = footerElement.getLocation().getY();

            //  Scroll down to 'bottom' button
            for(int i = 0; i < (posY - startPosY) / 10; i++) {
                js.executeScript("window.scrollBy(0,10)", "");
            }

            do {
                if (driver.findElements(By.id("customer-reviews-panel")).size() > 0) {
                    WebElement reviewPanelElement = driver.findElement(By.xpath("//div[@id='customer-reviews-panel']"));
                    startPosY = reviewPanelElement.getLocation().getY();

                    js.executeScript("arguments[0].scrollIntoView(true);", reviewPanelElement);
                    Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);

                    WebElement nextPageElement = null;
                    boolean hasNextPage = false;
                    List<WebElement> paginationElements = driver.findElements(By.className("pagination"));

                    if (paginationElements.size() > 0) {
                        List<WebElement> nextPageElements = driver.findElements(By.id("next-page"));
                        List<WebElement> pageNumberElements = driver.findElements(By.className("pagination-number"));

                        if (nextPageElements.size() > 0) {
                            nextPageElement = nextPageElements.get(0);
                            hasNextPage = nextPageElement.getAttribute("class").contains("disabled") ? false : true;
                        }

                        for (WebElement element : pageNumberElements) {
                            if (element.getAttribute("class").contains("active") &&
                                    StringUtils.isNotEmpty(element.getText())) {
                                currentPage = Integer.parseInt(element.getText());
                            }
                        }
                    }

                    //  Log
                    log.debug("current-page : {}, hasNextPage : {}", currentPage, hasNextPage);

                    //  Save html to file.
                    if (Objects.nonNull(writer)) {
                        String pageHTML = driver.getPageSource();

                        writer.write(hotelId, currentPage, pageHTML);
                    }

                    if (hasNextPage) {
                        nextPageElement.click();
                        Thread.sleep(sleepMillis / 2);

                        //  Scroll down to 'bottom' button
                        {
                            posY = footerElement.getLocation().getY();

                            for(int i = 0; i < (posY - startPosY) / 10; i++) {
                                js.executeScript("window.scrollBy(0,10)", "");
                            }
                        }

                        continue;
                    }
                }
                else {
                    //  Save html to file.
                    if (Objects.nonNull(writer)) {
                        String pageHTML = driver.getPageSource();

                        writer.write(hotelId, currentPage, pageHTML);
                    }
                }

                break;
            } while (true);
        }
        catch (Exception excp) {
            excp.printStackTrace();

            writer.error(hotelId, requestURL);
        }
    }

    public void scrap() throws Exception {
        for (Destination destination : destinations) {
            currentDestination = destination;

            //  Log
            log.debug("set current-destination : {}", currentDestination);

            for (com.github.seijuro.search.query.Sort sort : sorts) {
                currentSort = sort;
                currentPage = 1;

                //  Log
                log.debug("set current-sort : {}, current-page : {}", currentSort, currentPage);

                boolean hasNextPage = false;
                SearchURL searchURL = getNextSearchURL();

                //  Log
                log.debug("Scrap ... requestURL : {}", searchURL);

                scrap(searchURL, sleepMillis / 2);
                WebDriver driver = getDriver();
                JavascriptExecutor js = (JavascriptExecutor)driver;

                do {
                    WebElement footerElement = driver.findElement(By.className("footer"));

                    //  Scroll down to 'bottom' button
                    {
                        int posY = footerElement.getLocation().getY();

                        for(int i = 0; i < posY / 4; i++) {
                            js.executeScript("window.scrollBy(0,4)", "");
                        }
                    }

                    //  Log
                    log.debug("Write html into file");
                    searchURL.setPage(currentPage);

                    //  Save html to file.
                    if (Objects.nonNull(htmlWriter)) {
                        String pageHTML = driver.getPageSource();

                        htmlWriter.write(searchURL, pageHTML);
                    }

                    //  Log
                    log.debug("Check pagination ... ");

                    //  Pagination
                    WebElement paginationContainerElement = driver.findElement(By.id("paginationContainer"));
                    WebElement nextButtonElement = paginationContainerElement.findElement(By.id("paginationNext"));
                    hasNextPage = paginationContainerElement.getAttribute("class").contains("hide") ? false : true;

                    if (hasNextPage) {
                        //  Log
                        log.debug("Nagate to 'Next' page({}) ... ", currentPage + 1);
                        ++currentPage;

                        js.executeScript("arguments[0].click();", nextButtonElement);
                        Thread.sleep(sleepMillis);
                    }
                    else {
                        break;
                    }
                } while (true);
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
