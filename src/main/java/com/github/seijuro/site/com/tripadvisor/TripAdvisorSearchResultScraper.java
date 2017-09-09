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
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.util.*;

@Log4j2
public class TripAdvisorSearchResultScraper extends AbstractScraper {
    @Getter
    public static final long DefaultSleepMillis = 5L * DateUtils.MILLIS_PER_SECOND;

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

    public static Map<String, Integer> rank = new HashMap<>();

    /**
     * Construct <code>TripAdvisorSearchResultScraper</code>
     * @param driver
     */
    public TripAdvisorSearchResultScraper(WebDriver driver) {
        super(driver);
    }


    public void setDestination(Destination destination) {
        destinations = Collections.singletonList(destination);
    }

    public void setSort(Sort sort) {
        sorts = Collections.singletonList(sort);
    }

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

            Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);

            WebElement checkInElement = webDriver.findElement(By.cssSelector("span.unified-picker[data-datetype='CHECKIN']"));
            checkInElement.click();
            Thread.sleep(3L * DateUtils.MILLIS_PER_SECOND);

            //  check date-picker shown ...
            WebElement checkInDatePicker = webDriver.findElement(By.cssSelector("span.ui_overlay.ui_popover.arrow_top.date_picker_modal.CHECKIN"));
            if (Objects.nonNull(checkInDatePicker)) {
                WebElement dateTitle = checkInDatePicker.findElement(By.cssSelector("span div div div div span span.dsdc-month-title"));

                int month = Integer.parseInt(dateTitle.getText().split("\\s+")[1].replaceAll("\\D+", ""));

                //  Log
                log.debug("target month : {}", month);

                //  next month button
                WebElement nextMonthElement = checkInDatePicker.findElement(By.cssSelector("div.dsdc-next.ui_icon.single-chevron-right-circle"));
                for (; month < 11; ++month) {
                    log.debug("Click 'Next month' button ...");

                    nextMonthElement.click();
                    Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
                }

                //  Log
                log.debug("Choose 'Day' (check-in : {})", 15);

                WebElement startDateElement = checkInDatePicker.findElement(By.cssSelector("span.dsdc-cell.dsdc-day[data-date='2017-10-15']"));
                startDateElement.click();
                Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
            }

            WebElement checkOutDatePicker = webDriver.findElement(By.cssSelector("body span.ui_overlay.ui_popover.arrow_top.date_picker_modal.CHECKOUT"));
            {
                //  Log
                log.debug("Choose 'Day' (check-out : {}).", 15);

                WebElement endDateElement = checkOutDatePicker.findElement(By.cssSelector("span.dsdc-cell.dsdc-day[data-date='2017-10-16']"));
                endDateElement.click();
                Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
            }

            //  log
            log.debug("Click 'Search' ...");

            WebElement searchButtonElement = webDriver.findElement(By.cssSelector("button#SUBMIT_HOTELS"));
            searchButtonElement.click();

            //  wait for page loading ...
            //  Log
            log.debug("Wait for loading ... (waiting time : {} ms)", 5L * DateUtils.MILLIS_PER_SECOND);
            Thread.sleep(sleepMillis);

            for (Sort sort : sorts) {
                boolean hasNextPage = false;
                currentSort = sort;
                currentPage = 1;

                WebElement sortBarElement = webDriver.findElement(By.cssSelector("div.sort_bar"));
                String tabSelector = "ul.ui_tabs li.ui_tab[data-sortorder='" + currentSort.getQueryParameter() + "'], ul.ui_tab_bar li.ui_tab[data-currentsort='" + currentSort.getQueryParameter() + "']";
                WebElement tabElement = sortBarElement.findElement(By.cssSelector(tabSelector));

                WebElement headElement = webDriver.findElement(By.cssSelector("div.masthead"));

                //  scroll to head
                log.debug("Scroll to 'HEAD' view ...");
                js.executeScript("arguments[0].scrollIntoView(true);", headElement);
                Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);

                //  click tab
                log.debug("Click '{} (key : {})' Tab", sort.getLabel(), sort.getQueryParameter());
                tabElement.click();

                //  wait for loading data ...
                log.debug("Wait for loading ... (waiting time : {} ms)", sleepMillis);
                Thread.sleep(sleepMillis);

                WebElement footerElement = webDriver.findElement(By.xpath("//div[@class='foot']"));

                do {
                    int posY = footerElement.getLocation().getY();

                    //  Scroll down to 'bottom' button
                    log.debug("Scroll to 'footer' view ...");
                    for(int i = 0; i < posY / 10; i++) {
                        js.executeScript("window.scrollBy(0,10)", "");
                    }

                    //  Log
                    log.info("Scrap HTML page source ...");
                    if (Objects.nonNull(htmlWriter)) {
//                        String pageSource = webDriver.findElement(By.tagName("html")).getAttribute("innerHTML");
                        String pageSource = (String)js.executeScript("return arguments[0].innerHTML", webDriver.findElement(By.tagName("html")));
                        pageSource = "<html>" + pageSource + "</html>";

                        htmlWriter.write(webDriver.getCurrentUrl(), pageSource, destination, sort, Integer.valueOf(currentPage));
                    }

                    WebElement navigationNext = webDriver.findElement(By.cssSelector("div.unified.pagination.standard_pagination a.nav.next.ui_button.primary, span.nav.next.ui_button.disabled"));

                    //  initialize 'hasNextPage' ...
                    hasNextPage = false;

                    //  check if next page exists
                    if (Objects.nonNull(navigationNext) &&
                            !navigationNext.getAttribute("class").contains("disabled")) {
                        //  Log
                        log.debug("Click 'Next' button ... (current page : {}, next page : {})", currentPage, currentPage + 1);

                        hasNextPage = true;
                        navigationNext.click();
                        //  increment page number
                        ++currentPage;

                        //  wait for page loading & scroll ...
                        //  Log
                        log.debug("Wait for loading & scrolling ... (waiting time : {} ms)", sleepMillis);
                        Thread.sleep(sleepMillis);
                    }
                    else  {
                        //  Log
                        log.debug("No more page(s) ...");
                    }
                } while (hasNextPage);
            }
        }
    }

    @Override
    public String getNextRequestURL() {
        return null;
    }

    @Override
    public SearchURL getNextSearchURL() { return null; }
}
