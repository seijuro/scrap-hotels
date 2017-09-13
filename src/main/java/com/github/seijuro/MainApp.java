package com.github.seijuro;

import com.github.seijuro.db.reader.ExpediaHotelBaseReader;
import com.github.seijuro.db.reader.ExpediaSnaphostReader;
import com.github.seijuro.http.rest.IURLEncoder;
import com.github.seijuro.http.rest.RestfulAPIResponse;
import com.github.seijuro.scrap.Scraper;
import com.github.seijuro.search.query.Sort;
import com.github.seijuro.site.com.agoda.*;
import com.github.seijuro.site.com.agoda.data.AgodaHotel;
import com.github.seijuro.site.com.agoda.data.AgodaHotelDetail;
import com.github.seijuro.site.com.agoda.data.AgodaHotelReview;
import com.github.seijuro.site.com.agoda.query.CheckIn;
import com.github.seijuro.site.com.agoda.query.CheckOut;
import com.github.seijuro.site.com.booking.BookingHTMLPageParser;
import com.github.seijuro.site.com.booking.data.BookingHotel;
import com.github.seijuro.site.com.expedia.ExpediaHotelReviewHTMLParser;
import com.github.seijuro.site.com.expedia.ExpediaScraper;
import com.github.seijuro.site.com.expedia.ExpediaHTMLPageParser;
import com.github.seijuro.site.com.expedia.ExpediaHotelDetailHTMLParser;
import com.github.seijuro.site.com.expedia.data.ExpediaHotelDetail;
import com.github.seijuro.site.com.expedia.data.ExpediaHotelReview;
import com.github.seijuro.site.com.expedia.query.Date;
import com.github.seijuro.site.com.expedia.query.Lodging;
import com.github.seijuro.site.com.hotels.Destination;
import com.github.seijuro.site.com.hotels.property.query.QueryProperty;
import com.github.seijuro.site.com.hotels.result.*;
import com.github.seijuro.site.com.tripadvisor.BasicHTMLFileWriter;
import com.github.seijuro.site.com.tripadvisor.TripAdvisorReviewScraper;
import com.github.seijuro.snapshot.*;
import com.github.seijuro.writer.CSVFileWriter;
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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

@Log4j2
public class MainApp {
    @Getter
    public static final String UserHomePath = "/Users/myungjoonlee";
    @Getter
    public static final String ChromeDriverPath = UserHomePath + "/Desktop/Selenium-grid/chromedriver";

    static final String ROOT_DIR_BOOKING = UserHomePath + "/Developer/Booking.com";
    static final String ROOT_DIR_EXPEDIA = UserHomePath + "/Developer/Expedia.com";
    static final String ROOT_DIR_AGODA = UserHomePath + "/Desktop/Agoda.com";

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

    public static interface HotelParser {
        public abstract void parse(String filepath);
    }

    public static final HotelParser BookingHotelParser = (filepath -> { parseBookingHTMLPage(filepath);} );
    public static final HotelParser ExpeidaHotelParser = (filepath -> { parseExpediaHTMLPage(filepath);} );
    public static final HotelParser AgodaHotelParser = (filepath -> { parseAgodaHTMLPage(filepath); } );


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

            System.setProperty("webdriver.chrome.driver", getChromeDriverPath());
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
            com.github.seijuro.DefaultHTMLFileWriter writer = new com.github.seijuro.DefaultHTMLFileWriter(ROOT_DIR_EXPEDIA);

            List<com.github.seijuro.site.com.expedia.query.Destination> destinations = Arrays.asList(
                    com.github.seijuro.site.com.expedia.query.Destination.SEOUL,
                    com.github.seijuro.site.com.expedia.query.Destination.JEJU,
                    com.github.seijuro.site.com.expedia.query.Destination.BALLY,
                    com.github.seijuro.site.com.expedia.query.Destination.HAWAI);
            List<Sort> sorts = Arrays.asList(
                    com.github.seijuro.site.com.expedia.query.Sort.RECOMMANED,
                    com.github.seijuro.site.com.expedia.query.Sort.STAR_RATING,
                    com.github.seijuro.site.com.expedia.query.Sort.GUEST_RATING,
                    com.github.seijuro.site.com.expedia.query.Sort.BEST_DEALS
            );
            List<com.github.seijuro.site.com.expedia.query.Lodging> lodgings = Arrays.asList(
                    Lodging.HOTEL,
                    Lodging.HOTEL_RESORT
            );

            ExpediaScraper scraper = new ExpediaScraper(driver);
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

    public static void scrapAgodaCom(WebDriver driver) {
        try {
            com.github.seijuro.DefaultHTMLFileWriter writer = new com.github.seijuro.DefaultHTMLFileWriter(ROOT_DIR_AGODA);

            List<com.github.seijuro.search.query.Destination> destinations = Arrays.asList(com.github.seijuro.site.com.agoda.query.Destination.values());
            List<com.github.seijuro.search.query.Sort> sorts = Arrays.asList(com.github.seijuro.site.com.agoda.query.Sort.values());
            List<com.github.seijuro.search.query.Lodging> lodgings = Arrays.asList(com.github.seijuro.site.com.agoda.query.Lodging.values());

            AgodaScraper scraper = new AgodaScraper(driver);
            scraper.setAdults(2);
            scraper.setDestinations(destinations);
            scraper.setSorts(sorts);
            scraper.setStartDate(new CheckIn(2017, 11,15));
            scraper.setEndDate(new CheckOut(2017, 11,16));
            scraper.setLodgings(lodgings);
            scraper.setHtmlWriter(writer);

            scraper.scrap();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    private static BufferedReader agodaHotelIdReader = null;
    synchronized static String getAgodaNextHotel() {
        try {
            if (agodaHotelIdReader == null) {
                agodaHotelIdReader = new BufferedReader(new FileReader(getUserHomePath() + "/Desktop/Agoda/agoda-hotels.txt"));
            }

            try {
                return agodaHotelIdReader.readLine();
            }
            catch (Exception excp) {
                excp.printStackTrace();
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return null;
    }

    public static void scrapAgodaDescAndReview(AgodaHotelReviewHTMLWriter writer, WebDriver driver) {
        String line;

        try {
            while ((line = getAgodaNextHotel()) != null) {
                String[] tokens = line.split(":", 2);

                if (tokens.length != 2) {
                    //  Log
                    log.warn("tokens.lenth(required : 2) : {}, data : {}", tokens.length, line);

                    continue;
                }

                String hotelId = tokens[0];
                String linkURL = tokens[1];

                if (writer.alreadyVisited(hotelId)) {
                    //  Log
                    log.debug("already exists ... hotelId : {}", hotelId);

                    continue;
                }

                //  Log
                log.debug("hotelId : {}, linkURL : {}", hotelId, linkURL);

                List<com.github.seijuro.search.query.Destination> destinations = Arrays.asList(com.github.seijuro.site.com.agoda.query.Destination.values());
                List<com.github.seijuro.search.query.Sort> sorts = Arrays.asList(com.github.seijuro.site.com.agoda.query.Sort.values());
                List<com.github.seijuro.search.query.Lodging> lodgings = Arrays.asList(com.github.seijuro.site.com.agoda.query.Lodging.values());

                AgodaScraper scraper = new AgodaScraper(driver);
                scraper.setAdults(2);
                scraper.setDestinations(destinations);
                scraper.setSorts(sorts);
                scraper.setStartDate(new CheckIn(2017, 11,15));
                scraper.setEndDate(new CheckOut(2017, 11,16));
                scraper.setLodgings(lodgings);

                scraper.scrapDetailsNReviews(writer, hotelId, linkURL);
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

    }


    public static void extractHotelIdAndLinkURL(FileWriter fwriter, File ... files) {
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    extractHotelIdAndLinkURL(fwriter, file.listFiles());
                }
                else {
                    //  Log
                    log.debug("filename : {}", file.getName());

                    String line;
                    StringBuffer contentBuilder = new StringBuffer();
                    BufferedReader freader = new BufferedReader(new FileReader(file));

                    while ((line = freader.readLine()) != null) {
                        contentBuilder.append(line);
                    }

                    freader.close();

                    Document pageDocument = Jsoup.parse(contentBuilder.toString());
                    Elements hotelItems = pageDocument.select("li[data-hotelid]");

                    for (Element hotelItem : hotelItems) {
                        String hotelId = hotelItem.attr("data-hotelid");
                        String linkURL = String.format("%s%s", "https://www.agoda.com", hotelItem.select("a[href]").first().attr("href"));

                        fwriter.write(String.format("%s:%s\n", hotelId, linkURL));
                    }
                }
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

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

    public static Map<String, Integer> rank = new HashMap<>();

    public static void parseBookingHTMLPage(String filepath) {
        try {
            String[] tokens = filepath.split(File.separator);
            String destinaion = tokens[tokens.length - 2];
            String sort = tokens[tokens.length - 3];
            String domain = tokens[tokens.length - 4];

            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            BookingHTMLPageParser parser = new BookingHTMLPageParser();

            parser.setDomain(domain);
            parser.setSort(sort);
            parser.setDestination(destinaion);
            parser.setStartDate("2017-11-15");
            parser.setEndDate("2017-11-16");

            String line;
            StringBuffer sb = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            String rankKey = String.format("%s%s%s", domain, sort, destinaion);
            Integer latestRank = rank.getOrDefault(rankKey, 0);
            List<BookingHotel> results = parser.parse(sb.toString());

            MySQLConnectionString connectionString = getMySQLConnectionString();

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
            connection.setAutoCommit(false);
            BookingWriter writer = new BookingWriter(connection);
            writer.write(results.toArray(new BookingHotel[results.size()]), domain, destinaion, "2017-11-15", "2017-11-16", "호텔, 리조트", sort, latestRank + 1);
            rank.put(rankKey, latestRank + results.size());

            connection.commit();
            connection.close();;
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void parseAgodaHTMLPage(String filepath) {
        try {
            String[] tokens = filepath.split(File.separator);
            String destinaion = tokens[tokens.length - 2];
            String sort = tokens[tokens.length - 3];
            String domain = tokens[tokens.length - 4];

            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            AgodaHTMLParser parser = new AgodaHTMLParser();

            parser.setDomain(domain);
            parser.setSort(sort);
            parser.setDestination(destinaion);
            parser.setStartDate("2017-11-15");
            parser.setEndDate("2017-11-16");

            String line;
            StringBuffer sb = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            String rankKey = String.format("%s%s%s", domain, sort, destinaion);
            Integer latestRank = rank.getOrDefault(rankKey, 0);
            List<AgodaHotel> results = parser.parse(sb.toString());

            MySQLConnectionString connectionString = getMySQLConnectionString();

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
            connection.setAutoCommit(false);
            AgodaWriter writer = new AgodaWriter(connection);
            writer.write(results.toArray(new AgodaHotel[results.size()]), domain, destinaion, "2017-11-15", "2017-11-16", "호텔, 리조트", sort, latestRank + 1);
            rank.put(rankKey, latestRank + results.size());

            connection.commit();
            connection.close();;
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

//            parser.setWriter(writer);

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

    public static void extractReviewCounts(FileWriter fwriter, File... files) {
        for (File file : files) {
            if (file.isDirectory()) {
                extractReviewCounts(fwriter, file.listFiles());
            }
            else {
                //  Log
                log.debug("filename : {}", file.getName());

                extractReviewCountsFromAgodaHotelDetail(fwriter, file);
            }
        }
    }

    public static void extractReviewCountsFromAgodaHotelDetail(FileWriter fwriter, File file) {
        try {
            String[] tokens = file.getAbsolutePath().split(File.separator);
            String filename = tokens[tokens.length - 1];
            String hotelId = tokens[tokens.length - 2];

            if (filename.equals("001.html")) {
                String line;
                StringBuffer content = new StringBuffer();
                BufferedReader freader = new BufferedReader(new FileReader(file));

                while ((line = freader.readLine()) != null) {
                    content.append(line);
                }

                AgodaDetailHTMLParser parser = new AgodaDetailHTMLParser(hotelId);

                //  Log
                log.debug("parse HTML file ({}) ...", filename);
                List<AgodaHotelDetail> results = parser.parse(content.toString());
                if (results.size() > 0) {
                    fwriter.write(String.format("%s : %d%s", results.get(0).getId(), results.get(0).getReviewPageCount() , System.lineSeparator()));
                }
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void parseHotelDetails(AgodaHotelDetailWriter writer, File... files) {
        for (File file : files) {
            if (file.isDirectory()) {
                parseHotelDetails(writer, file.listFiles());
            }
            else {
                //  Log
                log.debug("filename : {}", file.getName());

                parseAgodaHotelDetail(writer, file);
            }
        }
    }

    public static void parseAgodaHotelDetail(AgodaHotelDetailWriter writer, File file) {
        try {
            String[] tokens = file.getAbsolutePath().split(File.separator);
            String filename = tokens[tokens.length - 1];
            String hotelId = tokens[tokens.length - 2];

            if (filename.equals("001.html")) {
                String line;
                StringBuffer content = new StringBuffer();
                BufferedReader freader = new BufferedReader(new FileReader(file));

                while ((line = freader.readLine()) != null) {
                    content.append(line);
                }

                AgodaDetailHTMLParser parser = new AgodaDetailHTMLParser(hotelId);

                //  Log
                log.debug("parse HTML file ({}) ...", filename);
                List<AgodaHotelDetail> results = parser.parse(content.toString());
                writer.write(results.toArray(new AgodaHotelDetail[results.size()]), null, null, null, null, null, null, -1);
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }




    public static void parseHotelReviews(AgodaHotelReviewWriter writer, File... files) {
        for (File file : files) {
            if (file.isDirectory()) {
                parseHotelReviews(writer, file.listFiles());
            }
            else {
                //  Log
                log.debug("filename : {}", file.getName());

                parseAgodaHotelReview(writer, file);
            }
        }
    }

    public static void parseAgodaHotelReview(AgodaHotelReviewWriter writer, File file) {
        try {
            String[] tokens = file.getAbsolutePath().split(File.separator);
            String filename = tokens[tokens.length - 1];
            String hotelId = tokens[tokens.length - 2];
            String line;

            StringBuffer content = new StringBuffer();
            BufferedReader freader = new BufferedReader(new FileReader(file));

            while ((line = freader.readLine()) != null) {
                content.append(line);
            }

            AgodaHotelReviewHTMLParser parser = new AgodaHotelReviewHTMLParser(hotelId);

            //  Log
            log.debug("parse HTML file ({}) ...", filename);
            List<AgodaHotelReview> results = parser.parse(content.toString());
            writer.write(results.toArray(new AgodaHotelReview[results.size()]), null, null, null, null, null, null, -1);
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }


    public static void parseExpediaHotelDetailHTMLPage(ExpediaHotelDetailWriter writer, String domain, String hotelId, String html) {
        ExpediaHotelDetailHTMLParser parser = new ExpediaHotelDetailHTMLParser(hotelId);
        List<ExpediaHotelDetail> restuls = parser.parse(html);

        if (restuls.size() > 0) {
            if (Objects.nonNull(writer)) { writer.write(restuls.toArray(new ExpediaHotelDetail[restuls.size()]), domain, null, null, null, null, null, -1); }
        }
    }

    public static void parseExpediaHotelDetails() {
        try {
            MySQLConnectionString connectionString = getMySQLConnectionString();

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
            connection.setAutoCommit(false);

            ExpediaHotelBaseReader expediaHotelBaseReader = new ExpediaHotelBaseReader(connection);
            ExpediaSnaphostReader snaphostReader = new ExpediaSnaphostReader(connection);
            ExpediaHotelDetailWriter writer = new ExpediaHotelDetailWriter(connection);

            List<String> hotelIds = expediaHotelBaseReader.readHotelIds();
            final String domain = "Expedia.com";
            int count = 0;
            int totalCount = hotelIds.size();

            //  release
            expediaHotelBaseReader = null;

            //  Log
            log.info("PROCESS ... ({} / {})", count, totalCount);

            for (String hotelId : hotelIds) {
                //  Log
                log.info("PROCESS : PARSING (hotel_id : {})", hotelId);


                String pageSource = snaphostReader.readHTMLPageSource(domain, hotelId, 0, false);

                if (Objects.nonNull(pageSource)) {
                    parseExpediaHotelDetailHTMLPage(writer, domain, hotelId, pageSource);

                    //  Log
                    log.info("PROCESS ... {} / {}", ++count, totalCount);

                    connection.commit();
                }
                else {
                    //
                    log.warn("No snapshot! (hotel_id : {})", hotelId);
                }
            }

            //  Log
            log.info("PROCESS : DONE ({} / {})", ++count, totalCount);

            connection.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }



    public static void parseExpediaHotelReviewHTMLPage(ExpediaHotelReviewWriter writer, String domain, String hotelId, String html) {
        ExpediaHotelReviewHTMLParser parser = new ExpediaHotelReviewHTMLParser(hotelId);
        List<ExpediaHotelReview> restuls = parser.parse(html);

        if (restuls.size() > 0) {
            if (Objects.nonNull(writer)) { writer.write(restuls.toArray(new ExpediaHotelReview[restuls.size()]), domain, null, null, null, null, null, -1); }
        }
    }


    public static void parseExpediaHotelReviews() {
        try {
            MySQLConnectionString connectionString = getMySQLConnectionString();

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
            connection.setAutoCommit(false);

            ExpediaHotelBaseReader expediaHotelBaseReader = new ExpediaHotelBaseReader(connection);
            ExpediaSnaphostReader snaphostReader = new ExpediaSnaphostReader(connection);
            ExpediaHotelReviewWriter writer = new ExpediaHotelReviewWriter(connection);

            List<String> hotelIds = expediaHotelBaseReader.readHotelIds();
            final String domain = "Expedia.com";
            int count = 0;
            int totalCount = hotelIds.size();

            //  release
            expediaHotelBaseReader = null;

            //  Log
            log.info("PROCESS ... ({} / {})", count, totalCount);

            for (String hotelId : hotelIds) {
                //  Log
                log.info("PROCESS : PARSING (hotel_id : {})", hotelId);

                List<Integer> pageNumbers = snaphostReader.readPageNumbers(domain, hotelId, true);
                for (int pageNumber : pageNumbers) {
                    String pageSource = snaphostReader.readHTMLPageSource(domain, hotelId, pageNumber, true);

                    if (Objects.nonNull(pageSource)) {

                        parseExpediaHotelReviewHTMLPage(writer, domain, hotelId, pageSource);

                        //  Log
                        log.info("PROCESS ... {} / {}", ++count, totalCount);

                        connection.commit();
                    }
                    else {
                        //
                        log.warn("No snapshot! (hotel_id : {})", hotelId);
                    }
                }


//                if (Objects.nonNull(pageSource)) {
//                    parseExpediaHotelDetailHTMLPage(writer, domain, hotelId, pageSource);
//                }

            }

            //  Log
            log.info("PROCESS : DONE ({} / {})", ++count, totalCount);

            connection.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void extractTripAdvisorHotelReviewURL(FileWriter fwriter, File file) {
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder contentBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                contentBuilder.append(line);
            }

            Document document = Jsoup.parse(contentBuilder.toString());
            Elements hotelElements = document.body().select("div.listing.easyClear.p13n_imperfect");

            for (Element hotelElement : hotelElements) {
                String hotelId = hotelElement.attr("data-locationid");

                //  Log
                log.debug("hotelId : {}", hotelId);

                Elements hotelLinkElements = hotelElement.select("div.illisting.ilCR div.hotel_content div.listing_title a");

                if (hotelLinkElements.size() > 0) {
                    String linkURL = String.format("http://www.tripadvisor.co.kr%s", hotelLinkElements.first().attr("href"));

                    //  Log
                    log.debug("linkURL : {}", linkURL);

                    fwriter.write(String.format("%s : %s%s", hotelId, linkURL, System.lineSeparator()));
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractTripAdvisorHotelReviewURL(FileWriter fwriter, File... files) {
        for (File file : files) {
            if (file.isDirectory()) {
                extractTripAdvisorHotelReviewURL(fwriter, file.listFiles());
            }
            else {
                extractTripAdvisorHotelReviewURL(fwriter, file);
            }
        }
    }

    public static void extractTripAdvisorHotelReviewURL() {
        try {
            String srcRootPath = getUserHomePath() + "/Desktop/TripAdvisor.com";
            String outFilepath = getUserHomePath() + "/Desktop/TripAdvisorLinkURL.txt";
            FileWriter fileWriter = new FileWriter(outFilepath, true);

            extractTripAdvisorHotelReviewURL(fileWriter, new File(srcRootPath).listFiles());

            fileWriter.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }


    public Map<String, Set<Integer>> readErrors(String filepath) {
        Map<String, Set<Integer>> results = new HashMap<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(filepath));

            while (Objects.nonNull(line = br.readLine())) {
                String[] tokens = line.split(":", 2);

                try {
                    String hotelId = tokens[0];
                    Integer pageNuber = Integer.parseInt(tokens[1]);

                    if (!results.containsKey(hotelId)) {
                        results.put(hotelId, new HashSet<>());
                    }

                    results.get(hotelId).add(pageNuber);
                }
                catch (NumberFormatException excp) {
                    log.error("error msg : {}, (original text : \"{}\")", excp.getMessage(), line);
                }
            }

            br.close();
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return results;
    }

    private static List<Thread> createTripAdvisorReviewScraperThread(int threadCount, final LinkedHashMap<String, String> hotelInfos, final Iterator<String> hotelInfoIdIterator) {
        List<Thread> threads = new ArrayList<>();

        for (int index = 0; index < threadCount; ++index) {
            threads.add(new Thread(() -> {
                WebDriver webDriver = null;
                Capabilities capabilities = DesiredCapabilities.chrome();

                try {
                    webDriver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);

                    TripAdvisorReviewScraper scraper = new TripAdvisorReviewScraper(webDriver);
                    BasicHTMLFileWriter writer = new BasicHTMLFileWriter(getUserHomePath() + "/Desktop/TripAdvisor.com/Reviews");

                    while (true) {
                        String hotelId = null;
                        String searchURL = null;

                        synchronized (hotelInfoIdIterator) {
                            if (hotelInfoIdIterator.hasNext()) {
                                hotelId = hotelInfoIdIterator.next();
                                searchURL = hotelInfos.get(hotelId);
                            }
                            else {
                                break;
                            }
                        }

                        if (writer.exists(new String[] {hotelId})) { continue; }

                        scraper.setHotelId(hotelId);
                        scraper.setWriter(writer);
                        scraper.scrap(searchURL, 2L * DateUtils.MILLIS_PER_SECOND);
                    }
                }
                catch (Exception excp) {
                    excp.printStackTrace();
                }

                //  quit!
                if (Objects.nonNull(webDriver)) {
                    webDriver.quit();
                }
            }));
        }

        return threads;
    }

    private static List<Thread> createRecoverTripAdvisorReviewThread(int threadCount, final Map<String, String> hotelInfos, final Iterator<String> iterErrorLogs) {
        List<Thread> threads = new ArrayList<>();

        for (int index = 0; index < threadCount; ++index) {
            {
                threads.add(new Thread(() -> {
                    WebDriver webDriver = null;

                    try {
                        Capabilities capabilities = DesiredCapabilities.chrome();
                        webDriver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);

                        TripAdvisorReviewScraper scraper = new TripAdvisorReviewScraper(webDriver);
                        BasicHTMLFileWriter writer = new BasicHTMLFileWriter(getUserHomePath() + "/Desktop/TripAdvisor.com/Reviews");

                        LinkedHashMap<String, Integer> hotelIdsToRecover = new LinkedHashMap<>();

                        String errorLog = null;

                        do {
                            synchronized (iterErrorLogs) {
                                if (iterErrorLogs.hasNext()) {
                                    errorLog = iterErrorLogs.next();
                                }
                                else {
                                    break;
                                }
                            }

                            if (StringUtils.isNotEmpty(errorLog)) {
                                String[] tokens = errorLog.split(":");

                                String hotelId = StringUtils.stripToEmpty(tokens[0]);
                                String searchURL = hotelInfos.get(hotelId);
                                int pageNumber = Integer.parseInt(StringUtils.stripToEmpty(tokens[1]));

                                log.debug("hotel-id : {}, page# : {}, url : {}", hotelId, pageNumber, searchURL);

                                if (tokens.length > 2) {
                                    log.debug("error type : {}", tokens[2]);

                                    if (tokens[2].equals("r")) {
                                        log.debug("scrap from page# : {}", pageNumber);
                                    }

                                    //  일단, 패스
                                    //  테스트 이후,
                                    continue;
                                }

                                scraper.setHotelId(hotelId);
                                scraper.setWriter(writer);
                                scraper.scrapOnly(searchURL, pageNumber, 3L * DateUtils.MILLIS_PER_SECOND);
                            }
                        } while (true);

                        webDriver.quit();
                    }
                    catch (Exception excp) {
                        excp.printStackTrace();
                    }

                    if (Objects.nonNull(webDriver)) {
                        webDriver.quit();
                    }
                }));
            }
        }

        return threads;
    }


    private static void recoverErrorTripAdvisorReviews(int maxThread) {
        try {
            final LinkedHashMap<String, String> hotelInfos = new LinkedHashMap<>();
            final List<String> errorLogs = new ArrayList<>();


            String line;

            //  load hotel-ids
            {
                BufferedReader reader = new BufferedReader(new FileReader(getUserHomePath() + "/Desktop/TripAdvisor.com/TripAdvisorLinkURL_U.txt"));
                while (Objects.nonNull(line = reader.readLine())) {
                    String[] tokens = line.split(":", 2);
                    hotelInfos.put(tokens[0].trim(), tokens[1].trim());
                }

                reader.close();
            }

            {
                BufferedReader reader = new BufferedReader(new FileReader(getUserHomePath() + "/Desktop/TripAdvisor.com/Reviews/error.txt"));
                while (Objects.nonNull(line = reader.readLine())) {
                    // check comment line
                    if (line.trim().startsWith("#")) { continue; }
                    errorLogs.add(line);
                }

                reader.close();
            }

            Iterator<String> iterErrorLogs = errorLogs.iterator();

            List<Thread> threads = createRecoverTripAdvisorReviewThread(maxThread, hotelInfos, iterErrorLogs);

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }


    private static void scrapTripAdvisorReviews(int maxThread) {
        try {
            final LinkedHashMap<String, String> hotelInfos = new LinkedHashMap<>();

            String line;

            //  load hotel-ids
            {
                BufferedReader reader = new BufferedReader(new FileReader(getUserHomePath() + "/Desktop/TripAdvisor.com/TripAdvisorLinkURL_U.txt"));
                while (Objects.nonNull(line = reader.readLine())) {
                    String[] tokens = line.split(":", 2);
                    hotelInfos.put(tokens[0].trim(), tokens[1].trim());
                }

                reader.close();
            }

            Iterator<String> hotelInfoIterator = hotelInfos.keySet().iterator();
            List<Thread> threads = createTripAdvisorReviewScraperThread(4, hotelInfos, hotelInfoIterator);

            //  threads start
            for (Thread thread : threads) {
                thread.start();

                Thread.sleep(1000L);
            }

            for (Thread thread : threads) {
                thread.join();
            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        System.setProperty("webdriver.chrome.driver", getChromeDriverPath());

//        scrapHotelsCom();
//        scrapBookingCom();
//
//        try {
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
//            driver.close();

        /**
         * site : Expedia.com
         * source : site
         * [target]
         * server : chaos
         * database : KDI
         */
//            {
//                WebDriver driver = new ChromeDriver();
//                scrapExpediaCom(driver);
//                driver.close();
//            }


        /**
         * site : Expeida.com
         * type : parsing
         * [target]
         * server : chaos
         * database : KDI
         */
//        {
//            parseExpediaHotelDetails();
//            parseExpediaHotelReviews();
//        }


        /**
         CREATE TABLE `ExpediaUserId` (
         `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
         `reviewer` varchar(255) NOT NULL,
         `country` varchar(50) NOT NULL,
         PRIMARY KEY (`id`),
         UNIQUE KEY `UIDX_REVIEWER` (`reviewer`,`country`)
         ) ENGINE=MyISAM DEFAULT CHARSET=utf8
         */



//        try {
//            Capabilities capabilities = DesiredCapabilities.chrome();
//
//            try {
//                WebDriver webDriver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);
//                JavascriptExecutor js = ((JavascriptExecutor)webDriver);
//
//                webDriver.get("https://www.expedia.co.kr/Jeju-Island-Hotels-Playce-Camp-Jeju.h18519688.Hotel-Information?chkin=2017.10.15&chkout=2017.10.16&rm1=a2&regionId=6049718&hwrqCacheKey=528f911b-0de3-4105-ab20-11faabf78219HWRQ1504799632185&vip=false&c=fca1f5f7-dd52-4569-bb81-b4cc09f8f5c3&&exp_dp=48182&exp_ts=1504799632934&exp_curr=KRW&swpToggleOn=false&exp_pg=HSR");
//                Thread.sleep(3 * DateUtils.MILLIS_PER_SECOND);
//
//                WebElement footerElement = webDriver.findElement(By.xpath("//div[@id='site-footer-wrap']"));
//                int scrollEndPosY = footerElement.getLocation().getY();
//                int scrollPx = 10;
//
//                for (int pos = 0; pos < scrollEndPosY; pos += scrollPx) {
//                    ((JavascriptExecutor)webDriver).executeScript("window.scrollBy(0,10)", "");
//                }
//
//                parseExpediaHotelDetailHTMLPage(null, "Expedia.com", "hotel-id-sample", webDriver.getPageSource());
//
//                WebElement hotelOverview = webDriver.findElement(By.xpath("//div[@class='hotel-overview']"));
//                js.executeScript("arguments[0].scrollIntoView(true);", hotelOverview);
//
//                WebElement buttonTapReviews = webDriver.findElement(By.xpath("//button[@id='tab-reviews']"));
//                buttonTapReviews.click();
//
//                Thread.sleep(1L * DateUtils.MILLIS_PER_SECOND);
//
//                scrollEndPosY = footerElement.getLocation().getY();
//                for (int pos = hotelOverview.getLocation().getY(); pos < scrollEndPosY; pos += scrollPx) {
//                    ((JavascriptExecutor)webDriver).executeScript("window.scrollBy(0,10)", "");
//                }
//
//                ExpediaHotelReviewHTMLParser parser = new ExpediaHotelReviewHTMLParser("1");
//
//                parser.parse(webDriver.getPageSource());
//
//                webDriver.quit();
//            }
//            catch (MalformedURLException excp) {
//                excp.printStackTrace();
//            }
//            catch (IOException excp) {
//                excp.printStackTrace();
//            }
//        }
//        catch (InterruptedException excp) {
//            excp.printStackTrace();
//        }


        /**
         * site : Agoda.com
         * source : site
         * [target]
         * server : chaos
         * database : KDI
         */
//            {
//                WebDriver driver = new ChromeDriver();
//                scrapAgodaCom(driver);
//                driver.close();
//
        try {
            String outFilepath = getUserHomePath() + "Desktop/agoda-hotels.txt";

            //  extract 'hotelId(s) and linkURL(s) ...
//                    {
//                        FileWriter fwriter = new FileWriter(outFilepath);
//                        extractHotelIdAndLinkURL(fwriter, new File(getUserHomePath() + "/Desktop/Agoda.com").listFiles());
//                        fwriter.close();
//                    }

//            for (int index = 0; index < 5; ++index) {
//                new Thread(() -> {
//                    Capabilities capabilities = DesiredCapabilities.chrome();
//
//                    try {
//                        WebDriver webDriver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);
//
//                        AgodaHotelReviewHTMLWriter writer = new AgodaHotelReviewHTMLWriter(getUserHomePath() + "/Desktop/Agoda-reviews");
//                        scrapAgodaDescAndReview(writer, webDriver);
//
//                        webDriver.quit();
//                    } catch (MalformedURLException excp) {
//                        excp.printStackTrace();
//                    } catch (IOException excp) {
//                        excp.printStackTrace();
//                    }
//                }).start();
//            }
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }



        //  TripAdvisor.com
//        try {
//            List<com.github.seijuro.search.query.Destination> destinations = Arrays.asList(new com.github.seijuro.search.query.Destination[] {
//                    com.github.seijuro.site.com.tripadvisor.query.Destination.BALLY,
//                    com.github.seijuro.site.com.tripadvisor.query.Destination.SEOUL,
//                    com.github.seijuro.site.com.tripadvisor.query.Destination.JEJU,
//                    com.github.seijuro.site.com.tripadvisor.query.Destination.HAWAII
//            });
//
//            List<com.github.seijuro.search.query.Sort> sorts = Arrays.asList(new com.github.seijuro.search.query.Sort[] {
//                    com.github.seijuro.site.com.tripadvisor.query.Sort.RECOMMENDED,
//                    com.github.seijuro.site.com.tripadvisor.query.Sort.POPULARITY
//            });
//
//            for (com.github.seijuro.search.query.Destination destination : destinations) {
//                new Thread(() -> {
//                    try {
//                        Capabilities capabilities = DesiredCapabilities.chrome();
//                        WebDriver webDriver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);
//                        TripAdvisorSearchResultScraper scraper = new TripAdvisorSearchResultScraper(webDriver);
//                        TripAdvisorSearchResultHTMLFileWriter writer = new TripAdvisorSearchResultHTMLFileWriter(getUserHomePath() + "/Desktop/TripAdvisor.com");
//
//                        scraper.setDestination(destination);
//                        scraper.setSorts(sorts);
//                        scraper.setHtmlWriter(writer);
//
//                        scraper.scrap();
//
//                        webDriver.quit();
//                    }
//                    catch (Exception excp) {
//                        excp.printStackTrace();;
//                    }
//                }).start();
//            }
//        }
//        catch (Exception excp) {
//            excp.printStackTrace();
//        }





        /**
         * extract hotelId & linkURL
         *
         * site : TripAdvisor.com
         */
//        extractTripAdvisorHotelReviewURL();



        scrapTripAdvisorReviews(4);
        recoverErrorTripAdvisorReviews(4);


        /**
         * site : Agoda.com
         * source : html files
         * [target]
         * server : chaos
         * database : KDI
         */
//            {
//                com.github.seijuro.search.query.Destination[] destinations = new com.github.seijuro.search.query.Destination[] {
//                        com.github.seijuro.site.com.agoda.query.Destination.SEOUL,
//                        com.github.seijuro.site.com.agoda.query.Destination.JEJU,
//                        com.github.seijuro.site.com.agoda.query.Destination.BALLY,
//                        com.github.seijuro.site.com.agoda.query.Destination.OAHU_HAWAI
//                };
//
//                com.github.seijuro.search.query.Sort[] sorts = new com.github.seijuro.search.query.Sort[] {
//                        com.github.seijuro.site.com.agoda.query.Sort.RECOMMEND,
//                        com.github.seijuro.site.com.agoda.query.Sort.REVIEW,
//                        com.github.seijuro.site.com.agoda.query.Sort.SCRETE_DEALS,
//                        com.github.seijuro.site.com.agoda.query.Sort.STAR_DESC
//                };
//
//                for (com.github.seijuro.search.query.Sort sort : sorts) {
//                    for (com.github.seijuro.search.query.Destination destination : destinations) {
//                        parseHotels(AgodaHotelParser, ROOT_DIR_AGODA, destination, sort);
//                    }
//                }
//            }



        /*
        DROP VIEW ExpediaHotelReview;
        CREATE VIEW `ExpediaHotelReview`
        AS
        SELECT
            `vt_review`.`id` AS `id`,
            `vt_review`.`hotel_id` AS `hotel_id`,
            `vt_hotel`.`name` AS `hotel_name`,
            `vt_review`.`post_date` AS `post_date`,
            `vt_review`.`score` AS `score`,
            `vt_review`.`title` AS `title`,
            `vt_review`.`locale` AS `locale`,
            `vt_review`.`has_response` AS `has_response`,
            `vt_userid`.`id` AS `reviewer_id`,
            `vt_review`.`reviewer` AS `reviewer`,
            `vt_review`.`country` AS `country`,
            `vt_review`.`lastupdate` AS `lastupdate`
        FROM
        `ExpediaHotelReviewBase` AS `vt_review`
        LEFT JOIN `ExpediaHotelDetail` AS `vt_hotel`
        ON `vt_review`.`hotel_id` = `vt_hotel`.`id`
        LEFT JOIN `UserId` `vt_userid`
        ON `vt_userid`.`domain` = 'Expedia.com' AND `vt_review`.`reviewer` = `vt_userid`.`reviewer` AND `vt_review`.`country` = `vt_userid`.`country`
         */

        /*
        DROP VIEW AgodaHotel;
        CREATE VIEW `AgodaHotel`
        AS
        select
            `vt_base`.`idx` AS `idx`,
            `vt_base`.`domain` AS `domain`,
            `vt_base`.`keyword` AS `keyword`,
            `vt_base`.`check_in` AS `check_in`,
            `vt_base`.`check_out` AS `check_out`,
            `vt_base`.`type` AS `type`,
            `vt_base`.`sort` AS `sort`,
            `vt_base`.`rank` AS `rank`,
            `vt_base`.`id` AS `id`,
            `vt_detail`.`name` AS `name`,
            `vt_detail`.`address` AS `addess`,
            `vt_base`.`price` AS `price`,
            `vt_base`.`linkURL` AS `linkURL`,
            `vt_base`.`score` AS `score`,
            `vt_base`.`thumbUp` AS `thumbUp`,
            `vt_base`.`class` AS `class`,
            `vt_base`.`currency` AS `currency`,
            `vt_base`.`review_count` AS `review_count`,
            `vt_base`.`coupon_discount` AS `coupon_discount`,
            `vt_base`.`discount_ribbon` AS `discount_ribbon`,
            `vt_base`.`award` AS `award`,
            `vt_base`.`freeCancellation` AS `freeCancellation`,
            `vt_base`.`options` AS `options`,
            `vt_detail`.`construct_year` AS `built_year`,
            `vt_detail`.`floor` AS `floors`,
            `vt_detail`.`rooms` AS `rooms`,
            `vt_detail`.`beach` AS `beach`,
            `vt_detail`.`has_restaurant` AS `has_restaurant`,
            `vt_detail`.`has_pool` AS `has_pool`,
            `vt_detail`.`has_fitness` AS `has_fitness`,
            `vt_detail`.`has_casino` AS `has_casino`,
            `vt_detail`.`tax_included` AS `tax_included`,
            `vt_detail`.`breakfast_included` AS `breakfast_included`,
            `vt_detail`.`agoda_reviews` AS `reviews`,
            `vt_base`.`lastupdate` AS `lastupdate`
         from (`AgodaHotelBase` `vt_base` left join `AgodaHotelDetail` `vt_detail` on((`vt_base`.`id` = `vt_detail`.`id`)))
         */

        /**
         * hotel detail
         */
//        try {
//            final String rootPath = getUserHomePath() + "/Desktop/Agoda-reviews";
//            File fileRootpath = new File(rootPath);
//
//            MySQLConnectionString connectionString = getMySQLConnectionString();
//
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
//            connection.setAutoCommit(false);
//            AgodaHotelDetailWriter writer = new AgodaHotelDetailWriter(connection);
//
//            parseHotelDetails(writer, fileRootpath.listFiles());
//
//            connection.close();
//
//            FileWriter fileWriter = new FileWriter(getUserHomePath() + "/Desktop/AgodaReviewCount.txt");
//            extractReviewCounts(fileWriter, fileRootpath.listFiles());
//            fileWriter.close();
//        }
//        catch (Exception excp) {
//            excp.printStackTrace();
//        }

        /**
         * Agoda.com - parse all reviews
         */
//        try {
//            final String rootPath = getUserHomePath() + "/Desktop/Agoda-reviews";
//            File fileRootpath = new File(rootPath);
//
//            MySQLConnectionString connectionString = getMySQLConnectionString();
//
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
//            connection.setAutoCommit(false);
//            AgodaHotelReviewWriter writer = new AgodaHotelReviewWriter(connection);
//
//            parseHotelReviews(writer, fileRootpath.listFiles());
//
//            connection.close();
//        }
//        catch (Exception excp) {
//            excp.printStackTrace();
//        }

            /**
             * extract review counts from hotel-detail files(scrapped)
             */
//            {
//                final String rootPath = getUserHomePath() + "/Desktop/Agoda-reviews";
//                File fileRootpath = new File(rootPath);
//
//                MySQLConnectionString connectionString = getMySQLConnectionString();
//
//                Class.forName("com.mysql.jdbc.Driver");
//                Connection connection = DriverManager.getConnection(connectionString.toConnectionString(), connectionString.getUser(), connectionString.getPassword());
//                connection.setAutoCommit(false);
//                AgodaHotelDetailWriter writer = new AgodaHotelDetailWriter(connection);
//
//                parseHotelDetails(writer, fileRootpath.listFiles());
//
//                connection.close();
//
//                FileWriter fileWriter = new FileWriter(getUserHomePath() + "/Desktop/AgodaReviewCount.txt");
//                extractReviewCounts(fileWriter, fileRootpath.listFiles());
//                fileWriter.close();
//            }

            /**
             * site : Booking.com
             * source : html files
             * [target]
             * server : chaos
             * database : KDI
             */
//            {
//                com.github.seijuro.search.query.Destination[] destinations = getDestinations_BookingCom();
//                com.github.seijuro.search.query.Sort[] sorts = getSortOrders_BookingCom();
//
//                for (com.github.seijuro.search.query.Sort sort : sorts) {
//                    for (com.github.seijuro.search.query.Destination destination : destinations) {
//                        parseHotels(BookingHotelParser, ROOT_DIR_BOOKING, destination, sort);
//                    }
//                }
//            }

//            parseHotels(ExpeidaHotelParser, ROOT_DIR_EXPEDIA, com.github.seijuro.site.com.expedia.query.Destination.BALLY, com.github.seijuro.site.com.expedia.query.Sort.RECOMMANED);
//        }
//        catch (Exception excp) {
//            excp.printStackTrace();
//        }
    }
}

