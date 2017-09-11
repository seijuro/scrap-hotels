package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.search.SearchURL;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.*;

@Log4j2
public class TripAdvisorReviewScraper extends AbstractScraper {
    @Getter
    public static final long DefaultSleepMillis = 5L * DateUtils.MILLIS_PER_SECOND;

    public static final long WAIT_MILLIS_1_SECOND = 1000L;
    public static final long WAIT_MILLIS_1_5_SECOND = 1500L;
    public static final long WAIT_MILLIS_2_SECOND = 2000L;
    public static final long WAIT_MILLIS_2_5_SECOND = 2500L;
    public static final long WAIT_MILLIS_3_SECOND = 3000L;

    public static final int MAX_TRY = 3;

    public enum ScrapType {
        SCRAP_THE_SEPCIFIED_PAGE,
        SCRAP_FROM_PAGE
    }

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

    /**
     * Setting check-in/out date on the review pages.
     *
     * @throws InterruptedException
     */
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
                Thread.sleep(WAIT_MILLIS_1_SECOND);
            }

            //  Log
            log.debug("Choose 'Day' (check-in : {})", 15);

            WebElement startDateElement = checkInDatePicker.findElement(By.cssSelector("span.dsdc-cell.dsdc-day[data-date='2017-10-15']"));
            startDateElement.click();
            Thread.sleep(WAIT_MILLIS_1_SECOND);
        }

        //  check-out
        {
            WebElement checkOutDatePicker = webDriver.findElement(By.cssSelector("body span.ui_overlay.ui_popover.arrow_left.date_picker_modal.CHECKOUT"));

            //  Log
            log.debug("Choose 'Day' (check-out : {}).", 16);

            WebElement endDateElement = checkOutDatePicker.findElement(By.cssSelector("span.dsdc-cell.dsdc-day[data-date='2017-10-16']"));
            endDateElement.click();
            Thread.sleep(WAIT_MILLIS_1_SECOND);
        }

        didSetCheckInOut = true;
    }

    /**
     * Save the HTML page source of the site.
     *
     * @param currentDirHierarchy
     * @param filename
     */
    public void saveHTMLPageSource(String[] currentDirHierarchy, String filename) {
        WebDriver webDriver = getDriver();
        if (Objects.nonNull(writer)) {
            log.debug("Saving 'Review' HTML ... result : {}", writer.write(currentDirHierarchy, filename, webDriver.getPageSource())
            );
        }
    }

    /**
     * Basic method to scrap review page(s).
     * Just scrap all pages from the first page.
     *
     * @param searchURL
     * @param sleepMillis
     * @throws Exception
     */
    @Override
    public void scrap(String searchURL, long sleepMillis) throws Exception {
        super.scrap(searchURL, getDefaultSleepMillis());

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        if (!didSetCheckInOut) {
            for (int index = 0; index < MAX_TRY; ++index) {
                try {
                    Thread.sleep(sleepMillis);
                    setCheckInOut();
                } catch (Exception excp) {
                    excp.printStackTrace();

                    //  Log
                    log.debug("Failed to choose check-in/out date ... wait for a second & retry");
                    Thread.sleep(sleepMillis);
                }
            }

            Thread.sleep(sleepMillis);
        }

        if (loadAllPageContent()) {
            //  Log
            log.debug("Loading all content of page is done ...");
        }

        currentPage = 1;
        boolean didReload;
        boolean hasNextPage;

        do {
            didReload = false;

            //  '리뷰 페이지'로 이동
            if (goToReview()) {
                //  '모든 언어'로 설정
                if (!setLocalType()) {
                    //  Log
                    log.error("Setting locale failed ... ");
                }
            }

            //  리뷰 리스트 순회
            do {
                String pageName = String.format("%04d", currentPage);
                String[] currentDirHierarchy = new String[]{hotelId, pageName};

                if (Objects.isNull(writer) ||
                        (Objects.nonNull(writer) && !writer.exists(currentDirHierarchy))) {
                    boolean result = true;

                    try {
                        int scrollY = 0;
                        List<String> reviewSelectorIds = getReviewSelectorIds();

                        log.debug("reviews count : {}", reviewSelectorIds.size());

                        //  리뷰가 없는 경우
                        if (reviewSelectorIds.size() == 0) {
                            //  페이지 저장 후 종료
                            saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                            break;
                        }

                        goToReview();
                        if (setTranslationIfExists()) {
                            didReload = true;
                            break;
                        }

                        goToReview();
                        setSeeMoreIfExist();

                        //  사용자 툴팁 윈도우 스크랩
                        result &= scrapTooltip(currentDirHierarchy);
                    }
                    catch (Exception excp) {
                        excp.printStackTrace();

                        result &= false;
                    }

                    if (!result) {
                        writer.error(String.format("%s:%d", hotelId, currentPage));
                    }

                    //  scrap HTML page source ...
                    log.debug("Scrap 'HTML' page source ...");
                    saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                }

                hasNextPage = false;
                WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS "));
                List<WebElement> pagenationElements = reviewsElement.findElements(By.cssSelector("div.prw_rup.prw_common_north_star_pagination"));

                if (pagenationElements.size() > 0) {
                    WebElement pagenationElement = pagenationElements.get(0);
                    List<WebElement> pageNumberElements = pagenationElement.findElements(By.cssSelector("div.unified.pagination.north_star span.nav.next"));

                    if (pageNumberElements.size() > 0) {
                        WebElement nextButton = pageNumberElements.get(0);

                        if (!nextButton.getAttribute("class").contains("disabled")) {
                            log.debug("navigate to 'next' review page ... (current : {} , next : {})", currentPage, currentPage + 1);
                            hasNextPage = true;
                            ++currentPage;
                            nextButton.click();

                            log.debug("Waiting for reloading & scrolling to the top of reviews ...", currentPage, currentPage + 1);
                            Thread.sleep(WAIT_MILLIS_2_SECOND);
                        }
                    }
                }
            } while (hasNextPage);
        } while (didReload);
    }

    /**
     * scrap Review Page whose page number is specified in param.
     * To use this method, you must calculate the offset from the page number, and offset size per page.
     * Finally, the offset should be descripted in searchURL.
     *
     * @param searchURL
     * @param pageNumber
     * @param sleepMillis
     * @throws Exception
     */
    private void scrapSpecifiedPage(String searchURL, int pageNumber, long sleepMillis) throws Exception {
        super.scrap(searchURL, getDefaultSleepMillis());

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        if (!didSetCheckInOut) {
            for (int index = 0; index < MAX_TRY; ++index) {
                try {
                    Thread.sleep(sleepMillis);
                    setCheckInOut();
                } catch (Exception excp) {
                    excp.printStackTrace();

                    //  Log
                    log.debug("Failed to choose check-in/out date ... wait for a second & retry");
                    Thread.sleep(sleepMillis);
                }
            }

            Thread.sleep(sleepMillis);
        }

        if (loadAllPageContent()) {
            //  Log
            log.debug("Loading all content of page is done ...");
        }

        boolean didReload;

        String pageName = String.format("%04d", pageNumber);
        String[] currentDirHierarchy = new String[]{hotelId, pageName};

        do {
            didReload = false;

            //  '리뷰 페이지'로 이동
            if (goToReview()) {
                //  '모든 언어'로 설정
                if (!setLocalType()) {
                    //  Log
                    log.error("Setting locale failed ... ");
                }
            }

            //  리뷰 리스트 순회
            if (!Objects.nonNull(writer)) {
                boolean result = true;

                try {
                    int scrollY = 0;
                    List<String> reviewSelectorIds = getReviewSelectorIds();

                    log.debug("reviews count : {}", reviewSelectorIds.size());

                    //  리뷰가 없는 경우
                    if (reviewSelectorIds.size() == 0) {
                        //  페이지 저장 후 종료
                        saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                        break;
                    }

                    goToReview();
                    if (setTranslationIfExists()) {
                        didReload = true;
                        break;
                    }

                    goToReview();
                    setSeeMoreIfExist();

                    //  사용자 툴팁 윈도우 스크랩
                    result &= scrapTooltip(currentDirHierarchy);
                }
                catch (Exception excp) {
                    excp.printStackTrace();

                    result &= false;
                }

                if (!result) {
                    writer.error(String.format("%s:%d", hotelId, pageNumber));
                }
            }
        } while (didReload);

        //  scrap HTML page source ...
        log.debug("Scrap 'HTML' page source ...");
        saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
    }

    public boolean scrap(ScrapType type, String searchURL, int pageNumber, long sleepMillis) throws Exception {
        Objects.requireNonNull(type);

        super.scrap(searchURL, getDefaultSleepMillis());

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        if (!didSetCheckInOut) {
            for (int index = 0; index < MAX_TRY; ++index) {
                try {
                    Thread.sleep(sleepMillis);
                    setCheckInOut();
                } catch (Exception excp) {
                    excp.printStackTrace();

                    //  Log
                    log.debug("Failed to choose check-in/out date ... wait for a second & retry");
                    Thread.sleep(sleepMillis);
                }
            }

            Thread.sleep(sleepMillis);
        }

        if (loadAllPageContent()) {
            //  Log
            log.debug("Loading all content of page is done ...");
        }

        currentPage = 1;

        //  '리뷰 페이지'로 이동
        if (goToReview()) {
            //  '모든 언어'로 설정
            if (!setLocalType()) {
                //  Log
                log.error("Setting locale failed ... ");
            }
        }

        goToReview();

        WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS "));
        List<WebElement> pagenationElements = reviewsElement.findElements(By.cssSelector("div.prw_rup.prw_common_north_star_pagination"));

        if (pagenationElements.size() > 0) {
            WebElement pagenationElement = pagenationElements.get(0);
            List<WebElement> pageNumberElements = pagenationElement.findElements(By.cssSelector("div.unified.pagination.north_star div.pageNumbers span.pageNum"));

            for (WebElement pageNumberElement : pageNumberElements) {
                if (!pageNumberElement.getAttribute("class").contains("last")) {
                    continue;
                }

                int lastPageNumber = Integer.parseInt(pageNumberElement.getAttribute("data-page-number"));
                int dataOffset = Integer.parseInt(pageNumberElement.getAttribute("data-offset"));

                assert(lastPageNumber > 1);

                if (pageNumber <= lastPageNumber) {
                    int reviewPageSize = (dataOffset / lastPageNumber - 1);
                    String redirectURL = searchURL.replace("-Reviews-", String.format("-Reviews-or%d-", dataOffset));

                    if (type == ScrapType.SCRAP_THE_SEPCIFIED_PAGE) {
                        //  Log
                        log.info("redirect to review page#{}(offset : {}) -> redirectURL : {}", pageNumber, (pageNumber - 1) * reviewPageSize, redirectURL);
                        scrapSpecifiedPage(redirectURL, pageNumber, sleepMillis);
                    }
                    else if (type == ScrapType.SCRAP_FROM_PAGE) {
                        currentPage = pageNumber;

                        scrapSpecifiedPage(redirectURL, pageNumber, sleepMillis);
                    }

                    return true;
                }
                else {
                    //  Log
                    log.warn("request page#{} is bigger than last page#{}", pageNumber, lastPageNumber);
                    writer.error(String.format("%s:%d", hotelId, pageNumber));

                    return false;
                }
            }

            //  Log
            log.warn("Seems to be some error on navigation elements.");

        }
        else {
            //  Log
            log.warn("There aren't pagination element ...");
        }
        writer.error(String.format("%s:%d", hotelId, pageNumber));
        return false;
    }

    /**
     *
     *
     * @param searchURL
     * @param pageNumber
     * @return
     */
    public String getRedirectURL(String searchURL, int pageNumber) {
        WebDriver webDriver = getDriver();

        WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS "));
        List<WebElement> pagenationElements = reviewsElement.findElements(By.cssSelector("div.prw_rup.prw_common_north_star_pagination"));

        if (pagenationElements.size() > 0) {
            WebElement pagenationElement = pagenationElements.get(0);
            List<WebElement> pageNumberElements = pagenationElement.findElements(By.cssSelector("div.unified.pagination.north_star div.pageNumbers span.pageNum"));

            for (WebElement pageNumberElement : pageNumberElements) {
                if (!pageNumberElement.getAttribute("class").contains("last")) {
                    continue;
                }

                int dataPageNumber = Integer.parseInt(pageNumberElement.getAttribute("data-page-number"));
                int dataOffset = Integer.parseInt(pageNumberElement.getAttribute("data-offset"));

                assert(dataPageNumber > 1);

                int reviewPageSize = (dataOffset / dataPageNumber - 1);
                String redirectURL = searchURL.replace("-Reviews-", String.format("-Reviews-or%d-", dataOffset));

                //  Log
                log.info("redirect to review page#{}(offset : {}) -> redirectURL : {}", pageNumber, (pageNumber - 1) * reviewPageSize, redirectURL);

                return redirectURL;
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Some of reviewer profile has tooltip link.
     * By calling this method, you can check if the reviewer has profiles and if exists, scrap the profiles.
     *
     * @param targetHierarchy
     * @return
     * @throws InterruptedException
     */
    public boolean scrapTooltip(String[] targetHierarchy) throws InterruptedException {
        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        List<String> reviewSelectorIds = getReviewSelectorIds();

        for (String reviewSelecrId : reviewSelectorIds) {
            for (int index = 0; index < MAX_TRY; ++index) {
                try {
                    WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS"));
                    WebElement reviewSelector = reviewsElement.findElement(By.id(reviewSelecrId));

                    String reviewId = reviewSelector.getAttribute("data-reviewid");

                    //  Log
                    log.debug("reviewId : {}", reviewId);

                    List<WebElement> uiColumnGroupElements = reviewSelector.findElements(By.cssSelector("div.review.hsx_review.ui_columns"));
                    if (uiColumnGroupElements.size() > 0) {
                        List<WebElement> uiColumnElements = uiColumnGroupElements.get(0).findElements(By.cssSelector("div.ui_column"));

                        WebElement userInfoColumn = uiColumnElements.get(0);

                        List<WebElement> memberOverlayLinklement = userInfoColumn.findElements(By.cssSelector("div.memberOverlayLink"));

                        if (memberOverlayLinklement.size() > 0) {
                            WebElement memberOverlayLink = memberOverlayLinklement.get(0);

                            if (memberOverlayLink.isDisplayed()) {
                                //  사용자 정보 툴립 팝업 로딩을 위한 마우스 액션
                                Actions mouseOverAction = new Actions(webDriver);
                                mouseOverAction.moveToElement(memberOverlayLink).build().perform();
                                //  사용자 정보 툴립 팝업 로딩
                                Thread.sleep(WAIT_MILLIS_2_5_SECOND);

                                //  scrap tool-tip ...
                                log.debug("Scrap 'tool-tip' ...");

                                List<WebElement> tootipElements = webDriver.findElements(By.cssSelector("body span.ui_overlay.ui_popover.arrow_left"));
                                if (tootipElements.size() > 0) {
                                    WebElement tootipElement = tootipElements.get(0);
                                    String tooltipHTML = tootipElement.getAttribute("innerHTML");

                                    if (Objects.nonNull(writer)) {
                                        log.debug("Saving 'tooltip' HTML ... result : {}", writer.write(new String[]{targetHierarchy[0], targetHierarchy[1], "tooltip"}, String.format("%s.html", reviewId), "<html>" + tooltipHTML + "</html>"));

                                        break;
                                    }
                                }

                                //  사용자 정보 툴팁 닫기 위한 마우스 액션
                                Actions mouseOffAction = new Actions(webDriver);
                                mouseOffAction.moveByOffset(memberOverlayLink.getLocation().getX() * -1, 0).build().perform();
                                Thread.sleep(100);
                            }
                        }
                    }

                    //  다음 리뷰 / 페이지 네비게이션으로 이동
                    int scrollY = reviewSelector.getSize().getHeight();
                    log.debug("Scroll to 'current review'  view ...");
                    for (int i = 0; i < scrollY / 20; i++) {
                        js.executeScript("window.scrollBy(0,20)", "");
                    }

                    //  don't retry!
                    break;
                }
                catch (InterruptedException excp) {
                    throw excp;
                }
                catch (Exception excp) {
                    //  Log
                    log.error("Failed to find review selector (id : {}) ... ({} / {})", reviewSelecrId, index + 1, MAX_TRY);
                    Thread.sleep(WAIT_MILLIS_1_SECOND);
                }
            }   //  retry block
        }

        return true;
    }

    /**
     * Find IDs of all review elements. The review elements can be newly generated depends on events (click some buttons, review page reloading etc ...).
     * Therefore, greping the reference of review elements poses a growing risk with exception, such as broken reference(s).
     *
     * @return
     */
    public List<String> getReviewSelectorIds() {
        WebDriver webDriver = getDriver();
        List<String> reviewSelectorIds = new ArrayList<>();

        WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS"));
        List<WebElement> reviewSelectorElements = reviewsElement.findElements(By.cssSelector("div.ppr_rup.ppr_priv_location_reviews_list div.review-container div.prw_rup.prw_reviews_basic_review_hsx div.reviewSelector"));

        for (WebElement reviewSelector : reviewSelectorElements) {
            reviewSelectorIds.add(reviewSelector.getAttribute("id"));
        }

        return reviewSelectorIds;
    }

    /**
     * If translation button was shown, this method would check which form/input type (ex, true, false) is set.
     * If the form/input elements whose value is 'true', then, send 'click' event on the form/input element whose value is 'false' and make this page reloaded.
     *
     * @return
     * @throws InterruptedException
     */
    public boolean setTranslationIfExists() throws InterruptedException {
        try {
            WebDriver webDriver = getDriver();
            JavascriptExecutor js = (JavascriptExecutor) webDriver;

            WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS"));
            List<WebElement> reviewContainerElements = reviewsElement.findElements(By.cssSelector("div.ppr_rup.ppr_priv_location_reviews_list div.review-container"));
            int scrollY = 0;

            //  '더 보기' & '번역' 버튼이 있는지 검색
            for (WebElement reviewContainer : reviewContainerElements) {
                if (scrollY > 0) {
                    log.debug("Scroll to 'current review'  view ...");
                    for (int i = 0; i < scrollY / 20; i++) {
                        js.executeScript("window.scrollBy(0,20)", "");
                    }
                }

                List<WebElement> uiColumnGroupElements = reviewContainer.findElements(By.cssSelector("div.review.hsx_review.ui_columns"));
                if (uiColumnGroupElements.size() > 0) {

                    List<WebElement> uiColumnElements = uiColumnGroupElements.get(0).findElements(By.cssSelector("div.ui_column"));

                    //  '번역'
                    List<WebElement> translationElements = reviewContainer.findElements(By.cssSelector("div.headers div.prw_rup.prw_reviews_mt_header_hsx div.translation div.translationOptions form.translationOptionForm label input.submitOnClick"));

                    for (WebElement inputElement : translationElements) {
                        String inputType = inputElement.getAttribute("value");
                        if (inputType.contains("false")) {
                            if (Objects.isNull(inputElement.getAttribute("checked"))) {
                                inputElement.click();

                                //  자동으로 전체 페이지 리로딩 됨
                                Thread.sleep(WAIT_MILLIS_2_5_SECOND);

                                return true;
                            }
                        }
                    }
                }

                scrollY = reviewContainer.getSize().getHeight();
            }
        }
        catch (InterruptedException excp) {
            throw excp;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }

    /**
     * If the contents of review was fold, this method make the review unfole & make it shown all contents.
     *
     * @return
     */
    public boolean setSeeMoreIfExist() {
        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS"));
        List<WebElement> reviewContainerElements = reviewsElement.findElements(By.cssSelector("div.ppr_rup.ppr_priv_location_reviews_list div.review-container"));
        int scrollY = 0;

        for (WebElement reviewContainer : reviewContainerElements) {
            if (scrollY > 0) {
                log.debug("Scroll to 'current review'  view ...");
                for (int i = 0; i < scrollY / 20; i++) {
                    js.executeScript("window.scrollBy(0,20)", "");
                }
            }

            try {
                List<WebElement> uiColumnGroupElements = reviewContainer.findElements(By.cssSelector("div.review.hsx_review.ui_columns"));
                if (uiColumnGroupElements.size() > 0) {

                    List<WebElement> uiColumnElements = uiColumnGroupElements.get(0).findElements(By.cssSelector("div.ui_column"));
                    WebElement reviewInfoColumn = uiColumnElements.get(1);

                    //  '더 보기 버튼이 있는 경우
                    List<WebElement> moreButtonElements = reviewInfoColumn.findElements(By.cssSelector("span.taLnk.ulBlueLinks"));
                    if (moreButtonElements.size() > 0 &&
                            moreButtonElements.get(0).getText().contains("보기")) {

                        log.debug("Click 'More' button & wait for loading : {} ms", sleepMillis);
                        moreButtonElements.get(0).click();
                        //  Wait for realoding reviews
                        Thread.sleep(WAIT_MILLIS_2_SECOND);

                        return true;
                    }
                }
            }
            catch (Exception excp) {
                log.error("Checking translation & see more button failed");
            }

            scrollY = reviewContainer.getSize().getHeight();
        }

        return false;
    }

    /**
     * This methods will return the one of two 'Tab Menu' elements which is active at that point.
     *
     * @return
     */
    public WebElement getTabMenu() {
        WebDriver webDriver = getDriver();
        return webDriver.findElement(By.cssSelector("ul.tabs_pers_content.easyClear.tb_stickyElt.ui_container"));
    }

    /**
     * By calling this method, you can traverse the whole page and make all javascript for loading contents called.
     *
     * @return
     * @throws InterruptedException
     */
    public boolean loadAllPageContent() throws InterruptedException {
        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        try {
            List<String> ids = new ArrayList<>();

            //  find all IDs of menu item.
            {
                WebElement TabMenu = getTabMenu();
                List<WebElement> menuItemElements = TabMenu.findElements(By.className("tabs_pers_item"));

                for (WebElement menuItem : menuItemElements) {
                    ids.add(menuItem.getAttribute("id"));
                }
            }

            //  click all menus
            for (String id : ids) {
                WebElement TabMenu = getTabMenu();
                WebElement menuItem = TabMenu.findElement(By.id(id));

                if (menuItem.getAttribute("class").contains("hidden")) {
                    continue;
                }

                menuItem.click();

                Thread.sleep(WAIT_MILLIS_1_5_SECOND);
            }

            return true;
        }
        catch (InterruptedException excp) {
            throw excp;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        //  2159977
        return false;
    }

    /**
     * scroll to 'Review' Tab.
     *
     * @return
     */
    public boolean goToReview() {
        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        try {
            WebElement TabMenu = getTabMenu();

            WebElement tabButton = TabMenu.findElement(By.id("TABS_REVIEWS"));
            tabButton.click();

            Thread.sleep(WAIT_MILLIS_1_SECOND);

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }

    /**
     * On the top of the review tab, the list of locale type exists. By calling this method, you can see the 'all' reviews.
     *
     * @return
     * @throws InterruptedException
     */
    public boolean setLocalType() throws InterruptedException {
        try {
            WebDriver webDriver = getDriver();

            //  '모든 언어'로 설정
            List<WebElement> languageFilterElements = webDriver.findElements(By.cssSelector("div.ui_column.language ul li.filterItem span.toggle input.filterInput"));
            if (languageFilterElements.size() > 0) {
                WebElement allLanguageElement = languageFilterElements.get(0);
                if (Objects.isNull(allLanguageElement.getAttribute("checked"))) {
                    allLanguageElement.click();

                    Thread.sleep(WAIT_MILLIS_1_SECOND);
                }
            }

            return true;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return false;
    }
}
