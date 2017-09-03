package com.github.seijuro.site.com.booking;

import com.github.seijuro.scrap.AbstractScraper;
import com.github.seijuro.writer.HTMLWriter;
import com.github.seijuro.search.SearchURL;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Objects;

@Log4j2
public class BookingComScraper extends AbstractScraper {
    @Getter
    public static final String BaseURL = "https://www.booking.com";
    public static final String SearchResultsURL_KR = "https://www.booking.com/searchresults.ko.html";
    @Getter
    public static final long DefaultSleepMillis = 10L * DateUtils.MILLIS_PER_SECOND;

    @Getter
    public static final String SeoulSubURL = "/searchresults.ko.html?aid=304142&label=gen173nr-1FCAEoggJCAlhYSDNiBW5vcmVmaH2IAQGYARe4AQfIAQzYAQHoAQH4AQySAgF5qAID&sid=c884fec6a8953c6e5fb833a9bc4e5f45&checkin_month=11&checkin_monthday=15&checkin_year=2017&checkout_month=11&checkout_monthday=16&checkout_year=2017&city=-716583&class_interval=1&group_adults=2&group_children=0&label_click=undef&lsf=ht_id%7C204%7C436&nflt=ht_id%3D204%3B&no_rooms=1&room1=A%2CA&sb_price_type=total&ss=%EC%84%9C%EC%9A%B8&ssb=empty&ssne=%EC%84%9C%EC%9A%B8&ssne_untouched=%EC%84%9C%EC%9A%B8&track_SRKF=1&unchecked_filter=hoteltype";
    @Getter
    public static final String JejuSubURL = "/searchresults.ko.html?aid=304142&label=gen173nr-1FCAEoggJCAlhYSDNiBW5vcmVmaH2IAQGYARe4AQfIAQzYAQHoAQH4AQySAgF5qAID&sid=bf69be3c0ce4d882a0a256489f069706&checkin_month=11&checkin_monthday=15&checkin_year=2017&checkout_month=11&checkout_monthday=16&checkout_year=2017&city=-714191&class_interval=1&clear_ht_id=1&dest_id=4170&dest_type=region&group_adults=2&group_children=0&label_click=undef&lsf=ht_id%7C206%7C85&nflt=ht_id%3D204%3Bht_id%3D206%3B&no_rooms=1&raw_dest_type=region&room1=A%2CA&sb_price_type=total&search_selected=1&ss=제주도%2C%20대한민국&ss_raw=제주도&ssb=empty&ssne_untouched=제주시&unchecked_filter=hoteltype&unchecked_filter=hoteltype&unchecked_filter=hoteltype";
    @Getter
    public static final String BallySubURL = "/searchresults.ko.html?aid=304142&label=gen173nr-1FCAEoggJCAlhYSDNiBW5vcmVmaH2IAQGYARe4AQfIAQzYAQHoAQH4AQySAgF5qAID&sid=c884fec6a8953c6e5fb833a9bc4e5f45&checkin_month=11&checkin_monthday=15&checkin_year=2017&checkout_month=11&checkout_monthday=16&checkout_year=2017&class_interval=1&dest_id=835&dest_type=region&group_adults=2&group_children=0&label_click=undef&lsf=ht_id%7C206%7C358&nflt=ht_id%3D204%3Bht_id%3D206%3B&no_rooms=1&raw_dest_type=region&room1=A%2CA&sb_price_type=total&ss=발리%2C%20인도네시아&ss_all=0&ssb=empty&sshis=0&ssne_untouched=발리&unchecked_filter=hoteltype&unchecked_filter=hoteltype";
    @Getter
    public static final String HwaiSubURL = "/searchresults.ko.html?aid=304142&label=gen173nr-1FCAEoggJCAlhYSDNiBW5vcmVmaH2IAQGYARe4AQfIAQzYAQHoAQH4AQySAgF5qAID&sid=13097a84f3cd95d4970fba391f3f1b33&checkin_month=11&checkin_monthday=15&checkin_year=2017&checkout_month=11&checkout_monthday=16&checkout_year=2017&class_interval=1&dest_id=2996&dest_type=region&group_adults=2&group_children=0&label_click=undef&no_rooms=1&raw_dest_type=region&room1=A%2CA&sb_price_type=total&ss=하와이&ss_all=0&ssb=empty&sshis=0&ssne_untouched=발리&unchecked_filter=hoteltype&unchecked_filter=hoteltype";

    public enum Region {
        SEOUL("SEOUL", 50, SeoulSubURL),
        JEJU("JEJU", 15, JejuSubURL),
        BALLY("BALLY", 15, BallySubURL),
        HAWAI("HAWAI", 15, HwaiSubURL);

        @Getter
        private final String region;
        @Getter
        private final int pageSize;
        @Getter
        private final String subURL;

        Region(String region, int pageSize, String subURL) {
            this.region = region;
            this.pageSize = pageSize;
            this.subURL = subURL;
        }
    }

    public enum SortOrder {
        RECOMMAND("popularity"),
        REVIEW_SCORE_AND_PRICE("review_score_and_price"),
        CLASS("class"),
        SCORE("score");

        @Getter
        private final String order;

        SortOrder(String order) {
            this.order = order;
        }
    }

    /**
     * Instance Properties
     */
    @Getter @Setter
    private int rows = Region.JEJU.getPageSize();
    private int nextPage = 1;
    @Setter
    private SortOrder sortOrder = SortOrder.RECOMMAND;
    @Getter
    private final HTMLWriter writer;
    protected boolean hasNext = true;
    protected String subURL = Region.JEJU.getSubURL();
    @Setter
    protected long sleepMillis = getDefaultSleepMillis();
    private boolean isFirst;

    public BookingComScraper(WebDriver driver, HTMLWriter writer) {
        super(driver);

        this.writer = writer;

    }

    public void scrapUntilLast() throws Exception {
        do {
            String requestURL = getNextRequestURL();

            if (StringUtils.isEmpty(requestURL)) break;

            scrap(requestURL, sleepMillis);

        } while (true);
    }

    @Override
    public void scrap(String requestURL, long millis) throws Exception {
        super.scrap(requestURL, millis);

        WebDriver driver = getDriver();
        WebElement searchRegion = driver.findElement(By.id("ss"));
        WebElement elementHotels = driver.findElement(By.id("hotellist_inner"));
        WebElement elementPagination = driver.findElement(By.className("results-paging"));

        String region = searchRegion.getAttribute("value");
        String paginationHTMl = elementPagination.getAttribute("outerHTML");
        Document document = Jsoup.parse(paginationHTMl);
        Elements elementPaging = document.select("li.sr_pagination_item");

        int currentPageIndex = Integer.parseInt(document.select("li.sr_pagination_item.current").first().text());
        int lastPageIndex = Integer.parseInt(elementPaging.last().text());

        if (currentPageIndex == lastPageIndex) {
            hasNext = false;
        }
        else {
            nextPage = currentPageIndex + 1;
        }

        System.out.println("current page index : " + currentPageIndex);
        System.out.println("last page index : " + lastPageIndex);

        if (Objects.nonNull(writer)) {
            String pageHTML = driver.getPageSource();

//            writer.write(region, String.format("%03d", currentPageIndex), pageHTML);
        }

        //  Log
//        log.debug("pageHTML : {}", pageHTML);
    }

    @Override
    public String getNextRequestURL() {
        if (isFirst) {
            isFirst = false;
            return getBaseURL();
        }
        else {
            if (hasNext) {
                StringBuffer urlBuilder = new StringBuffer(getBaseURL());
                urlBuilder.append(subURL)
                        .append("&order=").append(sortOrder.getOrder())
                        .append("&rows=").append(rows);

                int offset = rows * (nextPage - 1);

                if (offset > 0) {
                    urlBuilder.append("&offset=").append(offset);
                }

                return urlBuilder.toString();
            }
        }

        return null;
    }

    @Override
    public SearchURL getNextSearchURL() {
        return null;
    }
}
