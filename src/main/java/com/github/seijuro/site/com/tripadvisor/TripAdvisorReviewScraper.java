package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.search.SearchURL;
import com.github.seijuro.site.common.writer.HTMLFileWriter;
import com.github.seijuro.writer.HTMLWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Objects;

@Log4j2
public class TripAdvisorReviewScraper extends AbstractScraper {
    @Getter
    public static final long DefaultSleepMillis = 5L * DateUtils.MILLIS_PER_SECOND;

    @Setter @Getter
    private long sleepMillis = getDefaultSleepMillis();
    @Getter
    private int currentPage = 1;
    @Setter
    private String hotelId = null;
    private boolean didSetCheckInOut = false;
    @Setter
    private BasicHTMLFileWriter writer = null;

    /**
     * Construct <code>TripAdvisorSearchResultScraper</code>
     * @param driver
     */
    public TripAdvisorReviewScraper(WebDriver driver) {
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


    public void setCheckInOut() throws InterruptedException {
        //  wait additional ms ...
        WebDriver webDriver = getDriver();

        //  check-in
        {
            WebElement checkInDatePicker = webDriver.findElement(By.cssSelector("body span.ui_overlay.ui_popover.arrow_left.date_picker_modal.CHECKIN"));
            WebElement dateTitle = checkInDatePicker.findElement(By.cssSelector("span div div div div span span.dsdc-month-title"));
            int month = Integer.parseInt(dateTitle.getText().split("\\s+")[1].replaceAll("\\D+", ""));

            //  Log
            log.debug("current month : {}", month);

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

        //  check-out
        {
            WebElement checkOutDatePicker = webDriver.findElement(By.cssSelector("body span.ui_overlay.ui_popover.arrow_left.date_picker_modal.CHECKOUT"));

            //  Log
            log.debug("Choose 'Day' (check-out : {}).", 16);

            WebElement endDateElement = checkOutDatePicker.findElement(By.cssSelector("span.dsdc-cell.dsdc-day[data-date='2017-10-16']"));
            endDateElement.click();
            Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
        }

        didSetCheckInOut = true;
    }

    public void saveHTMLPageSource(String[] currentDirHierarchy, String filename) {
        WebDriver webDriver = getDriver();
        if (Objects.nonNull(writer)) {
            log.debug("Saving 'Review' HTML ... result : {}",
                    writer.write(currentDirHierarchy, filename, webDriver.getPageSource())
            );
        }
    }

    @Override
    public void scrap(String searchURL, long sleepMillis) throws Exception {
        super.scrap(searchURL, 5L * DateUtils.MILLIS_PER_SECOND);

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        int maxTry = 3;

        for (int index = 0; !didSetCheckInOut && (index < maxTry); ++index) {
            try {
                Thread.sleep(sleepMillis);
                setCheckInOut();
            }
            catch (Exception excp) {
                excp.printStackTrace();

                //  Log
                log.debug("Failed to choose check-in/out date ... wait for a second & retry");
                Thread.sleep(5L * DateUtils.MILLIS_PER_SECOND);
            }
        }

        currentPage = 1;
        boolean needReload;
        boolean hasNextPage;

        do {
            WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS "));
            int posY = reviewsElement.getLocation().getY();

            needReload = false;
            hasNextPage = false;

            //  Scroll down to 'review' button
            log.debug("Scroll to 'review container' view ...");
            for (int i = 0; i < posY / 10; i++) {
                js.executeScript("window.scrollBy(0,10)", "");
            }

            //  '모든 언어'로 설정
            List<WebElement> languageFilterElements = webDriver.findElements(By.cssSelector("div.ui_column.language ul li.filterItem span.toggle input.filterInput"));
            if (languageFilterElements.size() > 0) {
                WebElement allLanguageElement = languageFilterElements.get(0);
                if (Objects.isNull(allLanguageElement.getAttribute("checked"))) {
                    allLanguageElement.click();

                    // Wait for reloading all reviews ...
                    log.debug("Chick 'All language', and wait for reloading reviews ... {} ms", sleepMillis);
                    Thread.sleep(sleepMillis);
                }
            }

            do {
                String pageName = String.format("%03d", currentPage);
                String[] currentDirHierarchy = new String[]{hotelId, pageName};

                if (Objects.isNull(writer) ||
                        (Objects.nonNull(writer) && !writer.exists(currentDirHierarchy))) {
                    try {
                        int scrollY = 0;
                        List<WebElement> reviewContainerElements = reviewsElement.findElements(By.cssSelector("div.ppr_rup.ppr_priv_location_reviews_list div.review-container"));
                        log.debug("reviews count : {}", reviewContainerElements.size());

                        //  리뷰가 없는 경우
                        if (reviewContainerElements.size() == 0) {
                            //  페이지 저장 후 종료
                            saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                            break;
                        }

                        //  '더 보기' & '번역' 버튼이 있는지 검색
                        for (WebElement reviewContainer : reviewContainerElements) {
                            if (scrollY > 0) {
                                log.debug("Scroll to 'current review'  view ...");
                                for (int i = 0; i < scrollY / 10; i++) {
                                    js.executeScript("window.scrollBy(0,10)", "");
                                }
                            }
                            List<WebElement> uiColumnElements = reviewContainer.findElements(By.cssSelector("div.review.ui_columns div.ui_column"));
                            WebElement reviewInfoColumn = uiColumnElements.get(1);

                            //  '번역'
                            List<WebElement> translationElements = reviewContainer.findElements(By.cssSelector("div.headers div.prw_rup.prw_reviews_mt_header_hsx div.translation div.translationOptions form.translationOptionForm label input.submitOnClick"));
                            for (WebElement inputElement : translationElements) {
                                String inputType = inputElement.getAttribute("value");
                                if (inputType.contains("false")) {
                                    if (Objects.isNull(inputElement.getAttribute("checked"))) {
                                        inputElement.click();

                                        Thread.sleep(10L * DateUtils.MILLIS_PER_SECOND);
                                        needReload = true;
                                        break;
                                    }
                                }
                            }

                            if (needReload) { break; }

                            //  '더 보기 버튼이 있는 경우
                            List<WebElement> moreButtonElements = reviewInfoColumn.findElements(By.cssSelector("span.taLnk.ulBlueLinks"));
                            if (moreButtonElements.size() > 0 &&
                                    moreButtonElements.get(0).getText().contains("보기")) {

                                log.debug("Click 'More' button & wait for loading : {} ms", sleepMillis);
                                moreButtonElements.get(0).click();
                                Thread.sleep(sleepMillis);
                            }

                            scrollY = reviewContainer.getSize().getHeight();
                            Thread.sleep(500);
                        }

                        if (needReload) { break; }

                        //  로딩 이후 다시 '리뷰 컨테이너'를 검색
                        reviewContainerElements = reviewsElement.findElements(By.cssSelector("div.ppr_rup.ppr_priv_location_reviews_list div.review-container"));

                        //  scroll to head
                        log.debug("Scroll to 'Reviews Container' view ...");
                        js.executeScript("arguments[0].scrollIntoView(true);", reviewsElement);
                        Thread.sleep(sleepMillis);

                        scrollY = 0;
                        for (WebElement reviewContainer : reviewContainerElements) {
                            if (scrollY > 0) {
                                log.debug("Scroll to 'current review'  view ...");
                                for (int i = 0; i < scrollY / 10; i++) {
                                    js.executeScript("window.scrollBy(0,10)", "");
                                }
                            }

                            String reviewId = reviewContainer.getAttribute("data-reviewid");

                            //  Log
                            log.debug("reviewId : {}", reviewId);

                            List<WebElement> uiColumnElements = reviewContainer.findElements(By.cssSelector("div.review.ui_columns div.ui_column"));
                            WebElement userInfoColumn = uiColumnElements.get(0);
                            WebElement reviewInfoColumn = uiColumnElements.get(1);

                            List<WebElement> memberOverlayLinklement = userInfoColumn.findElements(By.cssSelector("div.memberOverlayLink"));

                            if (memberOverlayLinklement.size() > 0) {
                                WebElement memberOverlayLink = memberOverlayLinklement.get(0);

                                while (!memberOverlayLink.isDisplayed()) {
                                    js.executeScript("window.scrollBy(0,-10)", "");
                                }

                                Actions mouseOverAction = new Actions(webDriver);
                                mouseOverAction.moveToElement(memberOverlayLink).build().perform();
                                Thread.sleep(sleepMillis);

                                //  scrap tool-tip ...
                                log.debug("Scrap 'tool-tip' ...");

                                List<WebElement> tootipElements = webDriver.findElements(By.cssSelector("body span.ui_overlay.ui_popover.arrow_left"));
                                if (tootipElements.size() > 0) {
                                    WebElement tootipElement = tootipElements.get(0);
                                    String tooltipHTML = tootipElement.getAttribute("innerHTML");

                                    if (Objects.nonNull(writer)) {
                                        log.debug("Saving 'tooltip' HTML ... result : {}",
                                                writer.write(new String[]{currentDirHierarchy[0], currentDirHierarchy[1], "tooltip"}, reviewId + ".html", "<html>" + tooltipHTML + "</html>")
                                        );
                                    }
                                }

                                Actions mouseOffAction = new Actions(webDriver);
                                mouseOffAction.moveByOffset(memberOverlayLink.getLocation().getX() * -1, 0).build().perform();
                                Thread.sleep(sleepMillis);
                            }

                            scrollY = reviewContainer.getSize().getHeight();
                        }

                        //  scrap HTML page source ...
                        log.debug("Scrap 'HTML' page source ...");
                        saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                    }
                    catch (Exception excp) {
                        excp.printStackTrace();

                        writer.remove(currentDirHierarchy);
                    }
                }

                WebElement similarHotels = webDriver.findElement(By.cssSelector("div.ppr_rup.ppr_priv_hr_btf_similar_hotels"));
                while (!similarHotels.isDisplayed()) {
                    js.executeScript("window.scrollBy(0, 10)", "");
                }

                hasNextPage = false;
                List<WebElement> pagenationElements = reviewsElement.findElements(By.cssSelector("div.prw_rup.prw_common_north_star_pagination"));

                if (pagenationElements.size() > 0) {
                    WebElement pagenationElement = pagenationElements.get(0);
                    List<WebElement> nextButtonElements = pagenationElement.findElements(By.cssSelector("div.unified.pagination.north_star span.nav.next"));

                    if (nextButtonElements.size() > 0) {
                        WebElement nextButtonElement = nextButtonElements.get(0);

                        if (!nextButtonElement.getAttribute("class").contains("disabled")) {
                            log.debug("navigate to 'next' review page ... (current : {} , next : {})", currentPage, currentPage + 1);
                            hasNextPage = true;
                            ++currentPage;
                            nextButtonElement.click();

                            log.debug("Waiting for reloading & scrolling to the top of reviews ...", currentPage, currentPage + 1);
                            Thread.sleep(sleepMillis);
                        }
                    }
                }
            } while (hasNextPage);
        } while (needReload);
    }
}
