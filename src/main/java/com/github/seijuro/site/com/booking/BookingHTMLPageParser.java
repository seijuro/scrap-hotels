package com.github.seijuro.site.com.booking;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.booking.data.BookingHotel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class BookingHTMLPageParser implements HTMLPageParser<BookingHotel> {
    /**
     * enum - SearchResultType
     */
    public enum SearchResultType {
        NORMAL("sr_item"),
        BANNER("dod-banner");

        @Getter
        private final String clazz;

        SearchResultType(String clazz) {
            this.clazz = clazz;
        }
    }

    @Setter
    private String domain = StringUtils.EMPTY;
    @Setter
    private String destination = StringUtils.EMPTY;
    @Setter
    private String sort = StringUtils.EMPTY;
    @Setter
    private String startDate = StringUtils.EMPTY;
    @Setter
    private String endDate = StringUtils.EMPTY;

    public BookingHotel parseHotel(SearchResultType type, Element hotel) {
        BookingHotel.Builder hotelBuilder = new BookingHotel.Builder();
        Elements thumbUpElements = hotel.select("i.preferred-program-icon.bicon-thumb-up");
        Elements tagsElement = hotel.select("span.add-red-tag__amount");
        Elements ribbonElements = hotel.select("div.ribbon");
        Elements dealElements = hotel.select("span.d-deal--main.d-deal--main__text");

        //  default(s)
        hotelBuilder.setDomain(domain);
        hotelBuilder.setDestination(destination);
        hotelBuilder.setSort(sort);
        hotelBuilder.setStartDate(startDate);
        hotelBuilder.setEndDate(endDate);

        hotelBuilder.setThumbUpIcon(thumbUpElements.size() > 0);
        hotelBuilder.setId(StringUtils.stripToEmpty(hotel.attr("data-hotelid")));
        hotelBuilder.setTag(tagsElement.size() > 0 ? tagsElement.text() : StringUtils.EMPTY);

        for (Element element : ribbonElements) { hotelBuilder.addRibbon(StringUtils.normalizeSpace(element.text())); }
        for (Element element : dealElements) { hotelBuilder.addDeal(StringUtils.normalizeSpace(element.text())); }

        if (type == SearchResultType.NORMAL) {
            Elements linkElements = hotel.select("a.hotel_name_link.url");
            Elements hotelNameElements = hotel.select("span.sr-hotel__name");
            Elements priceElements = hotel.select("strong.price");
            Elements reinforcementElements = hotel.select("sup.sr_room_reinforcement");

            hotelBuilder.setClazz(StringUtils.stripToEmpty(hotel.attr("data-class")));
            hotelBuilder.setLinkURL(linkElements.size() > 0 ? StringUtils.normalizeSpace(linkElements.get(0).attr("href")) : StringUtils.EMPTY);
            hotelBuilder.setScore(StringUtils.stripToEmpty(hotel.attr("data-score")));
            hotelBuilder.setName(hotelNameElements.size() > 0 ? hotelNameElements.get(0).text() : StringUtils.EMPTY);
            hotelBuilder.setPrice(priceElements.size() > 0 ? priceElements.get(0).text() : StringUtils.EMPTY);

            for (Element element : reinforcementElements) { hotelBuilder.addCondition(element.text()); }
        }
        else if (type == SearchResultType.BANNER) {
            Elements titleElements = hotel.select("div.dod-banner__title");
            Elements hotelLinkElements = hotel.select("a.dod-banner__hotel-link");
            Elements scoreElements = hotel.select("a.review-score-badge");
            Elements priceElements = hotel.select("span.dod-banner-price__number");
            Elements freeCanellationElements = hotel.select("div.dod-banner-room__conditions.dod-banner-room__free-cancellation");

            hotelBuilder.setBanner(titleElements.size() > 0 ? titleElements.get(0).text() : StringUtils.EMPTY);
            hotelBuilder.setName(hotelLinkElements.size() > 0 ? hotelLinkElements.get(0).text() : StringUtils.EMPTY);
            hotelBuilder.setLinkURL(hotelLinkElements.size() > 0 ? StringUtils.normalizeSpace(hotelLinkElements.get(0).attr("href")) : StringUtils.EMPTY);
            hotelBuilder.setScore(scoreElements.size() > 0 ? scoreElements.get(0).text() : StringUtils.EMPTY);
            hotelBuilder.setPrice(priceElements.size() > 0 ? priceElements.get(0).text() : StringUtils.EMPTY);
            if (freeCanellationElements.size() > 0) { hotelBuilder.addCondition(freeCanellationElements.first().text()); }
        }

        return hotelBuilder.build();
    }

    @Override
    public List<BookingHotel> parse(String html) {
        Document pageDocument = Jsoup.parse(html);
        Elements hotels = pageDocument.select("div[data-hotelid]");

        List<BookingHotel> results = new ArrayList<BookingHotel>();
        for (Element hotel : hotels) {
            SearchResultType type = (StringUtils.stripToEmpty(hotel.attr("class")).split("\\s+"))[0].equals(SearchResultType.BANNER.getClazz()) ? SearchResultType.BANNER : SearchResultType.NORMAL;

            try {
                results.add(parseHotel(type, hotel));
            }
            catch (Exception excp) {
                excp.printStackTrace();
            }
        }

        return results;
    }
}
