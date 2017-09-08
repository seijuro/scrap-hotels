package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.search.query.Date;
import com.github.seijuro.search.query.Destination;
import com.github.seijuro.search.query.Lodging;
import com.github.seijuro.search.query.Sort;
import com.github.seijuro.writer.HTMLWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

@Log4j2
public class TripAdvisorScraper extends AbstractScraper {
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

    public TripAdvisorScraper(WebDriver driver) {
        super(driver);
    }

    public static Map<String, Integer> rank = new HashMap<>();

    public void scrap() throws Exception {
        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        for (Destination destination : destinations) {
            currentDestination = destination;

            webDriver.get("https://www.tripadvisor.co.kr");
            //  wait for loading
            Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);

            //  log
            log.debug("sendKey -> destination : {}", destination.getLabel());

            WebElement destinationElement = webDriver.findElement(By.cssSelector("input.typeahead_input[tabindex='1']"));
            destinationElement.sendKeys(destination.getLabel());

            Thread.sleep(3L * DateUtils.MILLIS_PER_SECOND);

            WebElement checkInElement = webDriver.findElement(By.cssSelector("span.unified-picker[data-datetype='CHECKIN']"));
            checkInElement.click();
            Thread.sleep(3L * DateUtils.MILLIS_PER_SECOND);

            //  check date-picker shown ...
            WebElement datePicker = webDriver.findElement(By.cssSelector("span.ui_overlay.ui_popover.arrow_top"));
            if (Objects.nonNull(datePicker)) {
                WebElement dateTitle = datePicker.findElement(By.cssSelector("span div div div div span span.dsdc-month-title"));

                int month = Integer.parseInt(dateTitle.getText().split("\\s+")[1].replaceAll("\\D+", ""));

                //  Log
                log.debug("month : {}", month);

                //  next month button
                WebElement nextMonthElement = datePicker.findElement(By.cssSelector("div.dsdc-next.ui_icon.single-chevron-right-circle"));
                for (; month < 11; ++month) {
                    nextMonthElement.click();
                    Thread.sleep(2L * DateUtils.MILLIS_PER_SECOND);
                }
            }

            Thread.sleep(3L * DateUtils.MILLIS_PER_SECOND);

            //  log
            log.debug("click search ...");
            WebElement searchButtonElement = webDriver.findElement(By.cssSelector("button#SUBMIT_HOTELS"));
            searchButtonElement.click();

            Thread.sleep(3L * DateUtils.MILLIS_PER_SECOND);

//            for (Sort sort : sorts) {
//                boolean hasNextPage = false;
//                currentSort = sort;
//                currentPage = 1;
//
//                SearchURL searchURL = getNextSearchURL();
//
//                //  Log
//                log.debug("requestURL : {}", searchURL.toURL());
//
//                webDriver.get(searchURL.toURL());
//                //  wait for loading ...
//                Thread.sleep(10L * DateUtils.MILLIS_PER_SECOND);
//
//                //  Log
//                log.debug("check-in : {}, check-out : {}", checkInElement.getText(), checkOutElement.getText());
//
//                try {

//                }
//                catch (NoSuchElementException excp) {
//                    excp.printStackTrace();
//                }
//
//                WebElement tabBarElement = webDriver.findElement(By.xpath("//ul[@class='ui_tabs']"));
//                WebElement tabElement = tabBarElement.findElement(By.xpath("//li[@data-currentsort='" + sort.getQueryParameter() + "']"));
//
//                //  scroll to tab
//                js.executeScript("arguments[0].click();", tabElement);
//                Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
//
//                //  click tab
//                tabElement.click();
//                //  wait for loading data ...
//                Thread.sleep(3L * DateUtils.MILLIS_PER_SECOND);
//
//                WebElement footerElement = webDriver.findElement(By.xpath("//div[@class='foot']"));
//
//                do {
//
//                    //  Scroll down to 'bottom' button
//                    int posY = footerElement.getLocation().getY();
//                    for(int i = 0; i < posY / 20; i++) {
//                        js.executeScript("window.scrollBy(0,20)", "");
//                    }
//
//                    WebElement navigationNext = webDriver.findElement(By.cssSelector("div.unified.paginaiton.standard_pagination a.nav.next.ui_button.prinary"));
//
//                    //  initialize 'hasNextPage' ...
//                    hasNextPage = false;
//
//                    //  check if next page exists
//                    if (Objects.nonNull(navigationNext)) {
//                        hasNextPage = true;
//                        navigationNext.click();
//                        //  wait for scrolling & loading ...
//                        Thread.sleep(5L * DateUtils.MILLIS_PER_SECOND);
//                    }
//                } while (hasNextPage);
//            }
        }
    }

    @Override
    public String getNextRequestURL() {
        return null;
    }

    @Override
    public SearchURL getNextSearchURL() {
        com.github.seijuro.site.com.tripadvisor.SearchURL.Builder urlBuilder = new SearchURL.Builder();
        urlBuilder.setDestination(currentDestination);
//        urlBuilder.setAdults(adults);
        urlBuilder.setChildren(childrenAges);
//        urlBuilder.setStartDate(startDate);
//        urlBuilder.setEndDate(endDate);
        urlBuilder.setSort(currentSort);
        urlBuilder.setLodgings(lodgings);
        urlBuilder.setPage(currentPage);

        return urlBuilder.build();
    }
}
