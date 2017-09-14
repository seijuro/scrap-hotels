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
    public static final long WAIT_MILLIS_3_5_SECOND = 3500L;
    public static final long WAIT_MILLIS_4_SECOND = 4000L;

    public static final int MAX_TRY = 3;

    private enum ScrapType {
        SCRAP_THE_ONLY_SPECIFIED_PAGE,
        SCRAP_FROM_PAGE
    }

    @Setter @Getter
    private long sleepMillis = getDefaultSleepMillis();
    @Getter
    private int currentPage = 1;
    @Setter
    private String hotelId = null;
    private boolean didSetCheckInOut = false;
    private boolean didMakeTranslationDisabled = false;
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

        if (!didSetCheckInOut) {
            for (int index = 0; index < MAX_TRY; ++index) {
                try {
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
                    break;
                }
                catch (InterruptedException excp) {
                    throw excp;
                }
                catch (Exception excp) {
                    excp.printStackTrace();
                }

                Thread.sleep(getDefaultSleepMillis());
            }
        }
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
        scrap(ScrapType.SCRAP_FROM_PAGE, searchURL, 1, sleepMillis);
    }

    private WebElement getNextReviewPageLinkIfExists() {
        WebDriver webDriver = getDriver();

        WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS "));
        List<WebElement> pagenationElements = reviewsElement.findElements(By.cssSelector("div.prw_rup.prw_common_north_star_pagination"));

        if (pagenationElements.size() > 0) {
            WebElement pagenationElement = pagenationElements.get(0);
            List<WebElement> pageNumberElements = pagenationElement.findElements(By.cssSelector("div.unified.pagination.north_star span.nav.next"));

            if (pageNumberElements.size() > 0) {
                WebElement nextButton = pageNumberElements.get(0);

                if (!nextButton.getAttribute("class").contains("disabled")) {
                    log.debug("navigate to 'next' review page ... (current : {} , next : {})", currentPage, currentPage + 1);

                    return nextButton;
                }
            }
        }

        return null;
    }

    private void scrap(ScrapType scrapType, String searchURL, int pageNumber, long sleepMillis) throws Exception {
        super.scrap(searchURL, getDefaultSleepMillis());

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        setCheckInOut();

        if (loadAllPageContent()) {
            //  Log
            log.debug("Loading all content of page is done ...");
        }

        currentPage = pageNumber;
        boolean didReload;
        boolean hasNextPage;

        try {
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
                    hasNextPage = false;
                    String pageName = String.format("%04d", currentPage);
                    String[] currentDirHierarchy = new String[]{hotelId, pageName};
                    boolean result = true;

                    try {
                        List<String> reviewSelectorIds = getReviewSelectorIds();

                        log.debug("reviews count : {}", reviewSelectorIds.size());

                        //  리뷰가 없는 경우
                        if (reviewSelectorIds.size() == 0) {
                            //  페이지 저장 후 종료
                            saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                            break;
                        }

                        goToReview();
                        if (disableTranslationIfNot()) {
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

                    //  error log에 기록
                    //  에러 형식은 아래와 같음.
                    //
                    //  $hotel-id ':' $page# [ ':' $type ]
                    //
                    if (result) {
                        //  scrap HTML page source ...
                        log.debug("Scrap 'HTML' page source ...");
                        saveHTMLPageSource(currentDirHierarchy, String.format("%s.html", pageName));
                    }
                    else {
                        if (Objects.nonNull(writer)) { writer.error(String.format("%s:%d", hotelId, currentPage)); }
                    }

                    if (scrapType == ScrapType.SCRAP_FROM_PAGE) {
                        //  load next reviews if exists.
                        WebElement nextReviewPageLink = getNextReviewPageLinkIfExists();
                        if (Objects.nonNull(nextReviewPageLink)) {
                            hasNextPage = true;

                            ++currentPage;
                            nextReviewPageLink.click();

                            log.debug("Waiting for reloading & scrolling to the top of reviews ...", currentPage, currentPage + 1);
                            Thread.sleep(WAIT_MILLIS_2_SECOND);
                        }
                    }
                } while (hasNextPage);
            } while (didReload);
        }
        catch (Exception excp) {
            excp.printStackTrace();

            //  Log
            log.error("error msg : {}", excp.getMessage());

            if (Objects.nonNull(writer)) { writer.error(String.format("%s:%d:r", hotelId, currentPage)); }
        }
    }

    public void scrapOnly(String searchURL, int pageNumber, long sleepMillis) throws Exception {
        super.scrap(searchURL, getDefaultSleepMillis());

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        setCheckInOut();

        //  전체 페이지 컨텐츠 로딩
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

        if (pageNumber > 1) {
            String redirectURL = getRedirectURL(searchURL, pageNumber);

            if (StringUtils.isNotEmpty(redirectURL)) {
                scrap(ScrapType.SCRAP_THE_ONLY_SPECIFIED_PAGE, redirectURL, pageNumber, sleepMillis);
            }
            else {
                //  Log
                log.error("Can not redirect to the speified page (hotel-id : {}, page# : {}).", hotelId, pageNumber);

                writer.error(String.format("#recover[failed] -> %s:%d", hotelId, pageNumber));
            }
        }
        else {
            scrap(ScrapType.SCRAP_THE_ONLY_SPECIFIED_PAGE, searchURL, 1, sleepMillis);
        }
    }

    public void scrapFrom(String searchURL, int pageNumber, long sleepMillis) throws Exception {
        super.scrap(searchURL, getDefaultSleepMillis());

        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor)webDriver;

        setCheckInOut();

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
        String redirectURL = getRedirectURL(searchURL, pageNumber);

        if (StringUtils.isNotEmpty(redirectURL)) {
            scrap(ScrapType.SCRAP_FROM_PAGE, redirectURL, pageNumber, sleepMillis);
        }
        else {
            //  Log
            log.error("Can not redirect to the speified page (hotel-id : {}, page# : {}).", hotelId, pageNumber);

            writer.error(String.format("#recover[failed] -> %s:%d", hotelId, pageNumber));
        }
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

                int reviewPageSize = (dataOffset / (dataPageNumber - 1));
                int targetOffset = reviewPageSize * (pageNumber - 1);
                String redirectURL = searchURL.replace("-Reviews-", String.format("-Reviews-or%d-", targetOffset));

                //  Log
                log.info("redirect to review page#{}(offset : {}) -> redirectURL : {}", pageNumber, targetOffset, redirectURL);

                return redirectURL;
            }

            //  Log
            log.warn("can't find 'last' page number element.");
        }
        else {
            log.warn("There aren't pagination element.");
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
        boolean result = true;

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
                                Thread.sleep(WAIT_MILLIS_1_SECOND);

                                //  scrap tool-tip ...
                                log.debug("Scrap 'tool-tip' ...");

                                for (int index2 = 0; index2 < MAX_TRY; ++index2) {
                                    try {
                                        List<WebElement> tootipElements = webDriver.findElements(By.cssSelector("body span.ui_overlay.ui_popover.arrow_left"));
                                        if (tootipElements.size() > 0) {
                                            WebElement tootipElement = tootipElements.get(0);
                                            String tooltipHTML = tootipElement.getAttribute("innerHTML");

                                            if (StringUtils.normalizeSpace(tootipElement.getText()).length() == 0) {
                                                //  log
                                                log.debug("seems to be loading isn't finished yet.");

                                                Thread.sleep((index2 + 1L) * DateUtils.MILLIS_PER_SECOND);

                                                continue;
                                            }

                                            if (Objects.nonNull(writer)) {
                                                log.debug("Saving 'tooltip' HTML ... result : {}", writer.write(new String[]{targetHierarchy[0], targetHierarchy[1], "tooltip"}, String.format("%s.html", reviewId), "<html>" + tooltipHTML + "</html>"));
                                            }
                                        }

                                        break;
                                    }
                                    catch (Exception excp) {
                                        if ((index2 + 1) == MAX_TRY) {
                                            log.error("failed to scrap tooltip (reviewer's profile). ");

                                            if (Objects.nonNull(writer)) {
                                                writer.error(String.format("%s:%s%s", hotelId, currentPage, System.lineSeparator()));
                                            }

                                            throw excp;
                                        }
                                    }

                                    Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
                                }

                                //  사용자 정보 툴팁 닫기 위한 마우스 액션
                                Actions mouseOffAction = new Actions(webDriver);
                                mouseOffAction.moveByOffset(memberOverlayLink.getLocation().getX() * -1, 0).build().perform();
                                Thread.sleep(100);
                            }
                        }
                    }

                    //  다음 리뷰 or 페이지 네비게이션으로 이동
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

                //  MAX_TRY 시도 이내에 툴팁을 스크랩하지 못 한 경우, 재수집을 위한 실패로 간주.
                if ((index + 1) == MAX_TRY) {
                    return false;
                }
            }   //  retry block
        }

        return result;
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
    private boolean disableTranslationIfNot() throws Exception {
        if (!didMakeTranslationDisabled) {
            try {
                WebDriver webDriver = getDriver();
                JavascriptExecutor js = (JavascriptExecutor) webDriver;

                WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS"));
                List<WebElement> reviewContainerElements = reviewsElement.findElements(By.cssSelector("div.ppr_rup.ppr_priv_location_reviews_list div.review-container"));
                int scrollY = 0;

                //  '번역' 버튼이 있는지 검색
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

                                    didMakeTranslationDisabled = true;

                                    //  자동으로 전체 페이지 리로딩 됨
                                    Thread.sleep(WAIT_MILLIS_3_SECOND);

                                    return true;
                                }
                            }
                        }
                    }

                    scrollY = reviewContainer.getSize().getHeight();
                }
            } catch (Exception excp) {
                throw excp;
            }
        }

        return false;
    }

    /**
     * If the contents of review was fold, this method make the review unfold & make it shown all contents.
     *
     * @return
     */
    public boolean setSeeMoreIfExist() throws Exception {
        WebDriver webDriver = getDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        int scrollY = 0;

        List<String> reviewSelectorIds = getReviewSelectorIds();

        for (String reviewSelectorId : reviewSelectorIds) {
            WebElement reviewsElement = webDriver.findElement(By.cssSelector("div#REVIEWS"));

            if (scrollY > 0) {
                log.debug("Scroll to 'current review'  view ...");
                for (int i = 0; i < scrollY / 20; i++) {
                    js.executeScript("window.scrollBy(0,20)", "");
                }
            }

            for (int index = 0; index < MAX_TRY; ++index) {
                try {
                    WebElement reviewSelector = reviewsElement.findElement(By.id(reviewSelectorId));
                    List<WebElement> uiColumnGroupElements = reviewSelector.findElements(By.cssSelector("div.review.hsx_review.ui_columns"));
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
                            Thread.sleep(WAIT_MILLIS_4_SECOND);

                            return true;
                        }
                        //  '더 보기' 버튼이 없는 경우
                        else {
                            scrollY = reviewSelector.getSize().getHeight();
                            break;
                        }
                    }
                }
                catch (Exception excp) {
                    log.error("Checking translation & see more button failed");
                }

                if ((index + 1) == MAX_TRY) {
                    throw new Exception("Failed to check if the button 'see more' exists.");
                }

                //  로딩 자체가 덜 되어서 엘러멘트 생성이 안 된 경우.
                Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
            }
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

            Thread.sleep(WAIT_MILLIS_1_5_SECOND);

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
