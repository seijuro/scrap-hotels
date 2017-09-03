package com.github.seijuro;

import com.github.seijuro.http.rest.IURLEncoder;
import com.github.seijuro.http.rest.RestfulAPIResponse;
import com.github.seijuro.search.query.Sort;
import com.github.seijuro.site.com.booking.BookingHTMLPageParser;
import com.github.seijuro.site.com.expedia.ExpediaComScraper;
import com.github.seijuro.site.com.expedia.ExpediaHTMLPageParser;
import com.github.seijuro.site.com.expedia.query.Date;
import com.github.seijuro.site.com.expedia.query.Lodging;
import com.github.seijuro.site.com.hotels.Destination;
import com.github.seijuro.site.com.hotels.property.query.QueryProperty;
import com.github.seijuro.site.com.hotels.result.*;
import com.github.seijuro.snapshot.*;
import com.github.seijuro.writer.CSVFileWriter;
import com.github.seijuro.writer.TSVFileWriter;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Log4j2
public class MainApp {
    @Getter
    public static final String Domain_HotelsCom = "https://kr.hotels.com";
    @Getter
    public static final String Domain_BookingCom = "https://www.booking.com";
    @Getter
    public static final String BaseURL_HotelsCom = "https://kr.hotels.com/search/listings.json";
    @Getter
    public static final String BaseURL_BookingCom = "https://www.booking.com/searchresults.ko.html";

    public static Sort[] getSortOrders_HotelsCom() {
        Sort[] sortOrders = new Sort[] {
                com.github.seijuro.site.com.hotels.SortOrder.BEST_SELLER,
                com.github.seijuro.site.com.hotels.SortOrder.GUEST_RATING,
                com.github.seijuro.site.com.hotels.SortOrder.STAR_RATING_HIGHEST_FIRST
        };

        return sortOrders;
    }

    public static Sort[] getSortOrders_BookingCom() {
        Sort[] sortOrders = new Sort[] {
                com.github.seijuro.site.com.booking.query.Sort.RECOMMAND,
                com.github.seijuro.site.com.booking.query.Sort.REVIEW_SCORE_AND_PRICE,
                com.github.seijuro.site.com.booking.query.Sort.SCORE,
                com.github.seijuro.site.com.booking.query.Sort.CLASS_DESC
        };

        return sortOrders;
    }

    public static Destination[] getDestinations_HotelsCom() {
        Destination[] destinations = {
                com.github.seijuro.site.com.hotels.Destination.SEOUL,
                com.github.seijuro.site.com.hotels.Destination.JEJU,
                com.github.seijuro.site.com.hotels.Destination.BALLY};
        return destinations;
    }

    public static com.github.seijuro.search.query.Destination[] getDestinations_BookingCom() {
        Destination[] destinations = {
                com.github.seijuro.site.com.hotels.Destination.SEOUL,
                com.github.seijuro.site.com.hotels.Destination.JEJU,
                com.github.seijuro.site.com.hotels.Destination.BALLY,
                com.github.seijuro.site.com.hotels.Destination.HAWAI
        };
        return destinations;
    }

    public static String getBeginURL_HotelsCom(Destination dest, Sort sortOrder, String checkIn, String checkOut, int rooms, int adult, int children) {
        StringBuffer urlBuilder = new StringBuffer(getBaseURL_HotelsCom());
        urlBuilder.append("?").append(QueryProperty.DestinationId).append("=").append(dest.getId())
                .append("&").append(QueryProperty.QueryDestination).append("=").append(dest.getQuery())
                .append("&").append(QueryProperty.ReservedLocation).append("=").append(dest.getLocation())
                .append("&").append(QueryProperty.SortOrder).append("=").append(sortOrder.getQueryParameter())
                .append("&").append(QueryProperty.QueryCheckIn).append("=").append(checkIn)
                .append("&").append(QueryProperty.QueryCheckOut).append("=").append(checkOut)
                .append("&").append(QueryProperty.QueryRooms).append("=").append(rooms)
                .append("&").append(QueryProperty.QueryAdult).append("=").append(adult)
                .append("&").append(QueryProperty.QueryChildren).append("=").append(children)
                .append("&").append(QueryProperty.PageNumber).append("=").append(1)
                .append("&").append(QueryProperty.StartIndex).append("=").append(1)
                .append("&").append(QueryProperty.AccId).append("=").append("3,1");


        return urlBuilder.toString();
    }

    @Getter
    public static final IURLEncoder ParameterEncoder = s -> URLEncoder.encode(s, "UTF-8");

    static String convert(String str, String encoding) throws IOException {
        ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
        requestOutputStream.write(str.getBytes(encoding));
        return requestOutputStream.toString(encoding);
    }

    static MySQLConnectionString getMySQLConnectionString() {
        MySQLConnectionString connString = new MySQLConnectionString();
        connString.setHost("chaos");
        connString.setDatabase("KDI");
        connString.setUser("kds");
        connString.setPassword("kds0809");

        return connString;
    }

    static String serializedQueryRoom(int rooms, int adults, int children) {
        return String.format("%02d%02d%02d", rooms, adults, children);
    }

    public static void scrapHotelsCom() {
        try {
            DateTime dt = new DateTime();
            int year = dt.getYear();
            int revision = 0;
            String type = "호텔, 리조트";
            String beginDate = "2017-11-15";
            String endDate = "2017-11-16";
            Destination[] destinations = getDestinations_HotelsCom();
            Sort[] sortOrders = getSortOrders_HotelsCom();

            MySQLConnectionString connectionString = getMySQLConnectionString();

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
            connection.setAutoCommit(false);

            SnapshotReader snapshotReader = new SnapshotReader(connection);
            HotelsWriter hotelsWriter = new HotelsWriter(connection);


            for (Destination destination : destinations) {
                for (Sort sortOrder : sortOrders) {
                    int rank = 1;
                    int pageNo = 1;
                    int totalCount = 0;

                    String requestURL = getBeginURL_HotelsCom(destination, sortOrder, beginDate, endDate, 1, 2, 0);

                    SnapshotRequest.Builder builder = new SnapshotRequest.Builder();
                    builder.setYear(year);
                    builder.setRevision(revision);
                    builder.setUrl(getBaseURL_HotelsCom());
                    builder.setParam1(destination.getText());
                    builder.setParam2(sortOrder.getQueryParameter());
                    builder.setParam3(beginDate);
                    builder.setParam4(endDate);
                    builder.setParam5(serializedQueryRoom(1, 2, 0));

                    SnapshotRequest request = builder.build();
                    SnapshotResult result = snapshotReader.read(request);

                    if (result instanceof SnapshotResult) {
                        log.debug("skip (reason : already visit, idx : {}) -> revision : {}, url : {}, dest : {}, sort : {}, page : {}", result.getIdx(), revision, getBaseURL_HotelsCom(), destination.getText(), sortOrder.getQueryParameter(), pageNo);

                        return;
                    }

                    SearchHotelsAPI api = new SearchHotelsAPI(requestURL);
                    api.setEncodeFunc(getParameterEncoder());

                    RestfulAPIResponse response = api.request();

                    int statusCode = response.getHttpResponseCode();
                    String responseText = response.getResponse();

                    log.debug("Status Code : {}", statusCode);
                    log.debug("Response Text : {}", responseText);

                    Gson gson = new Gson();
                    SearchResponse searchResponse = gson.fromJson(responseText, SearchResponse.class);
                    Data responseData = searchResponse.getData();
                    Result[] results = responseData.getBody().getSearchResults().getResults();

                    //  Log
                    log.debug("result # : {}", results.length);
                    hotelsWriter.write(results, getDomain_HotelsCom(), destination.getText(), beginDate, endDate, type, sortOrder.getLabel(), rank);
                    connection.commit();

                    rank += Objects.nonNull(results) ? results.length : 0;
                    totalCount += Objects.nonNull(results) ? results.length : 0;

                    String nextUrl = responseData.getBody().getSearchResults().getPagination().getNextPageUrl();

                    while (StringUtils.isNotEmpty(nextUrl)) {
                        //  Sleep
                        Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);

                        log.debug("nextUrl : {}", nextUrl);

                        String fullURL = String.format("%s%s", SearchHotelsAPI.getBaseURL(), nextUrl);
                        api = new SearchHotelsAPI(fullURL);
                        api.setEncodeFunc(getParameterEncoder());

                        response = api.request();

                        statusCode = response.getHttpResponseCode();
                        responseText = response.getResponse();

                        log.debug("Status Code : {}", statusCode);
                        log.debug("Response Text : {}", responseText);

                        searchResponse = gson.fromJson(responseText, SearchResponse.class);
                        responseData = searchResponse.getData();
                        SearchResults searchResults = responseData.getBody().getSearchResults();
                        Pagination pagination = searchResults.getPagination();

                        nextUrl = Objects.nonNull(pagination) ? pagination.getNextPageUrl() : null;
                        results = searchResults.getResults();

                        hotelsWriter.write(results, getDomain_HotelsCom(), destination.getText(), beginDate, endDate, type, sortOrder.getQueryParameter(), rank);

                        rank += Objects.nonNull(results) ? results.length : 0;
                        totalCount += Objects.nonNull(results) ? results.length : 0;

                        //  Log
                        log.debug("result # : {}", results.length);

                        connection.commit();
                    }

                    {
                        SnapshotRequest.Builder writerRequestBuilder = new SnapshotRequest.Builder();
                        writerRequestBuilder.setYear(year);
                        writerRequestBuilder.setRevision(revision);
                        writerRequestBuilder.setUrl(getBaseURL_HotelsCom());
                        writerRequestBuilder.setParam1(destination.getText());
                        writerRequestBuilder.setParam2(sortOrder.getQueryParameter());
                        writerRequestBuilder.setParam3(beginDate);
                        writerRequestBuilder.setParam4(endDate);
                        writerRequestBuilder.setParam5(serializedQueryRoom(1, 2, 0));
                        writerRequestBuilder.setResult(Integer.toString(totalCount));

                        SnapshotRequest writerRequest = writerRequestBuilder.build();

                        //  write snapshot
                        SnapshotWriter snapshotWriter = new SnapshotWriter(connection);
                        snapshotWriter.write(writerRequest);

                        connection.commit();
                    }
                }
            }

            connection.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static String getBookingSeoulURL(int rows, int offset) {
        StringBuffer urlBuilder = new StringBuffer("https://www.booking.com/searchresults.ko.html?aid=304142&label=gen173nr-1DCAEoggJCAlhYSDNiBW5vcmVmaH2IAQGYARe4AQfIAQzYAQPoAQGSAgF5qAID&sid=7be03716f83bc273a5ae464108ee9221&checkin_month=11&checkin_monthday=15&checkin_year=2017&checkout_month=11&checkout_monthday=16&checkout_year=2017&class_interval=1&dest_id=-716583&dest_type=city&group_adults=2&group_children=0&label_click=undef&lsf=ht_id%7C204%7C434&nflt=ht_id%3D204%3B&no_rooms=1&raw_dest_type=city&room1=A%2CA&sb_price_type=total&src=index&ss=%EC%84%9C%EC%9A%B8&ssb=empty&ssne=%EC%84%9C%EC%9A%B8&ssne_untouched=%EC%84%9C%EC%9A%B8&unchecked_filter=hoteltype&rows=15");

        if (rows > 0) {
            urlBuilder.append("&rows=").append(rows);
        }

        if (offset > 0) {
            urlBuilder.append("&offset=").append(offset);
        }

        return urlBuilder.toString();
    }

    public static void scrapBookingCom() {
        try {
            Destination[] destinations = getDestinations_HotelsCom();
            Sort[] sortOrders = getSortOrders_HotelsCom();

            int rows = 15;
            int offset = 0;
            String requestURL = getBookingSeoulURL(rows, offset);

            System.setProperty("webdriver.chrome.driver", "/Users/sogiro/IdeaProjects/chromedriver");
            WebDriver driver = new ChromeDriver();
            driver.get(requestURL);

            Thread.sleep(10L * DateUtils.MILLIS_PER_SECOND);

            System.out.println("========== PAGE SOURCE ==========\n" + driver.getPageSource());

            WebElement element = driver.findElement(By.id("hotellist_inner"));
            String pageHTML = element.getAttribute("outerHTML");

            Document pageDocument = Jsoup.parse(pageHTML);
            Elements elements = pageDocument.select("div[data-hotelid]");

            Iterator<Element> iterElement = elements.iterator();

            while (iterElement.hasNext()) {
                Element currentElement = iterElement.next();

                Elements elementDODTitle = currentElement.select("div.dod-banner__title");
                String dodTitle = elementDODTitle.size() > 0 ? elementDODTitle.text() : null;

                Elements elementRibbon = currentElement.select("div.ribbon");
                String ribbonText = "";
                String ribbonNumber = "";
                String hotelName = "";
                String detailLinkUrl = "";
                String hotelId = currentElement.attr("data-hotelid");
                String hotelClass = currentElement.attr("data-class");
                String hotelScore = currentElement.attr("data-score");
                Elements infoElements = currentElement.select("i.preferred-program-icon.bicon-thumb-up");

                boolean thumbUp = infoElements.size() > 0 ? true : false;

                if (elementRibbon.size() > 0) {
                    ribbonText = elementRibbon.select("div.ribbon--word").text();
                    ribbonNumber = elementRibbon.select("div.ribbon--number").text();

                    Elements elementBannerContent = currentElement.select("div.dod-banner__content");
                    Elements elementBannerLink = elementBannerContent.select("a.dod-banner__hotel-link");

                    hotelName = elementBannerLink.size() > 0 ? elementBannerLink.text() : null;
                    detailLinkUrl = elementBannerLink.attr("href");
                }
                else {
                    Elements elementName = currentElement.select("span.sr-hotel__name");
                    hotelName = elementName.size() > 0 ? elementName.text() : null;
                    Elements linkElements = currentElement.select("a.hotel_name_link.url");
                    detailLinkUrl = linkElements.size() > 0 ? linkElements.attr("href") : null;
                }

                System.out.println("hotelId : " + hotelId);
                System.out.println("hotelClass : " + hotelClass);
                System.out.println("hotelScore : " + hotelScore);
                System.out.println("hotelName : " + hotelName);
                System.out.println("thumb-up element : " + thumbUp);
                System.out.println("detailLinkURL : " + detailLinkUrl);
            }
            System.out.println("elements.size : " + elements.size());
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void scrapExpediaCom(WebDriver driver) {
        try {
            DefaultHTMLFileWriter writer = new DefaultHTMLFileWriter("/Users/sogiro/Developer/Expedia.com");

            List<com.github.seijuro.site.com.expedia.query.Destination> destinations = Arrays.asList(
                    com.github.seijuro.site.com.expedia.query.Destination.SEOUL,
                    com.github.seijuro.site.com.expedia.query.Destination.JEJU,
                    com.github.seijuro.site.com.expedia.query.Destination.BALLY,
                    com.github.seijuro.site.com.expedia.query.Destination.HAWAI);
            List<Sort> sorts = Arrays.asList(
                    com.github.seijuro.site.com.expedia.query.Sort.RECOMMANED,
                    com.github.seijuro.site.com.expedia.query.Sort.STAR_RATING,
                    com.github.seijuro.site.com.expedia.query.Sort.GUEST_RATING
            );
            List<com.github.seijuro.site.com.expedia.query.Lodging> lodgings = Arrays.asList(
                    Lodging.HOTEL,
                    Lodging.HOTEL_RESORT
            );

            ExpediaComScraper scraper = new ExpediaComScraper(driver);
            scraper.setAdults(2);
            scraper.setDestinations(destinations);
            scraper.setSorts(sorts);
            scraper.setStartDate(new Date(2017, 11,15));
            scraper.setEndDate(new Date(2017, 11,16));
            scraper.setLodgings(lodgings);
            scraper.setHtmlWriter(writer);

            scraper.scrap();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    static final String ROOT_DIR_BOOKING = "/Users/sogiro/Developer/Booking.com";
    static final String ROOT_DIR_EXPEDIA = "/Users/sogiro/Developer/Expedia.com";

    public static interface HotelParser {
        public abstract void parse(String filepath);
    }

    public static final HotelParser BookingHotelParser = (filepath -> {parseBookingHTMLPage(filepath);});
    public static final HotelParser ExpeidaHotelParser = (filepath -> {parseExpediaHTMLPage(filepath);});

    public static void parseHotels(HotelParser parser, String rootDir, com.github.seijuro.search.query.Destination destination, com.github.seijuro.search.query.Sort sort) {
        try {
            StringBuffer pathBuilder = new StringBuffer(rootDir);
            pathBuilder.append(File.separator).append(sort.getLabel())
                    .append(File.separator).append(destination.getLabel());

            File dirpath = new File(pathBuilder.toString());
            assert dirpath.isDirectory();

            parseHotels(parser, dirpath.listFiles());
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void parseHotels(HotelParser parser, File... files) {
        for (File file : files) {
            if (file.isDirectory()) {
                parseHotels(parser, file.listFiles());
            }
            else {
                //  Log
                log.debug("filename : {}", file.getName());

                parser.parse(file.getAbsolutePath());
            }
        }
    }

    public static void parseBookingHTMLPage(String filepath) {
        try {
            String[] tokens = filepath.split(File.separator);
            String destinaion = tokens[tokens.length - 2];
            String sort = tokens[tokens.length - 3];
            String domain = tokens[tokens.length - 4];

            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            BookingHTMLPageParser parser = new BookingHTMLPageParser();
            TSVFileWriter writer = new TSVFileWriter(ROOT_DIR_BOOKING, String.format("%s_%s.tsv", destinaion, sort));

            parser.setDomain(domain);
            parser.setSort(sort);
            parser.setDestination(destinaion);
            parser.setStartDate("2017-11-15");
            parser.setEndDate("2017-11-16");

            parser.setWriter(writer);

            String line;
            StringBuffer sb = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            parser.parse(sb.toString());

            reader.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void parseExpediaHTMLPage(String filepath) {
        try {
            String[] tokens = filepath.split(File.separator);
            String destinaion = tokens[tokens.length - 2];
            String sort = tokens[tokens.length - 3];
            String domain = tokens[tokens.length - 4];

            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            ExpediaHTMLPageParser parser = new ExpediaHTMLPageParser();
            CSVFileWriter writer = new CSVFileWriter(ROOT_DIR_EXPEDIA, String.format("%s_%s.csv", destinaion, sort));

//            parser.setDomain(domain);
//            parser.setSort(sort);
//            parser.setDestination(destinaion);
//            parser.setStartDate("2017-11-15");
//            parser.setEndDate("2017-11-16");

            parser.setWriter(writer);

            String line;
            StringBuffer sb = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            parser.parse(sb.toString());

            reader.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }



    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Users/sogiro/IdeaProjects/chromedriver");

//        scrapHotelsCom();
//        scrapBookingCom();
//
        try {
//            WebDriver driver = new ChromeDriver();
//            driver.get(BookingComScraper.getBaseURL());
//
//            Thread.sleep(5 * DateUtils.MILLIS_PER_SECOND);
//
//            BookingComScraper scraper = new BookingComScraper(driver, new HTMLFileWriter("/Users/sogiro/Developer/Booking.com/Score"));
//            scraper.setSleepMillis(10L * DateUtils.MILLIS_PER_SECOND);
//            scraper.setSortOrder(BookingComScraper.SortOrder.SCORE);
//            scraper.scrapUntilLast();
//
//            scrapExpediaCom(driver);
//            driver.close();

            {
                com.github.seijuro.search.query.Destination[] destinations = getDestinations_BookingCom();
                com.github.seijuro.search.query.Sort[] sorts = getSortOrders_BookingCom();

                for (com.github.seijuro.search.query.Sort sort : sorts) {
                    for (com.github.seijuro.search.query.Destination destination : destinations) {
                        parseHotels(BookingHotelParser, ROOT_DIR_BOOKING, destination, sort);
                    }
                }
            }
//            parseHotels(ExpeidaHotelParser, ROOT_DIR_EXPEDIA, com.github.seijuro.site.com.expedia.query.Destination.BALLY, com.github.seijuro.site.com.expedia.query.Sort.RECOMMANED);
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }



    }
}
