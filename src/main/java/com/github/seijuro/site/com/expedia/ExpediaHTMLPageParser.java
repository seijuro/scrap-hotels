package com.github.seijuro.site.com.expedia;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.writer.CSVFileWriter;
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
public class ExpediaHTMLPageParser implements HTMLPageParser {
    @Setter
    private CSVFileWriter writer = null;

    @Override
    public void parse(String html) {
        Document pageDocument = Jsoup.parse(html);
        Elements articles = pageDocument.select("article[data-hotelid]");

        for (Element article : articles) {
            Elements hotelInfoElements = article.select("ul.hotel-info");
            Elements hotelNameElements = hotelInfoElements.select("li.hotelTitle");
            Elements secondaryHotelNameElements = hotelInfoElements.select("li.secondaryHotelName");
            Elements nppElements = hotelInfoElements.select("li.npp");
            Elements badgeElements = hotelInfoElements.select("li.hotelBadge");
            Elements startRatingElements = hotelInfoElements.select("li.starRating.secondary");

            Elements freeCancelElements = article.select("li.freeCancel.secondary");
            Elements discountRibbonElements = article.select("li.discount-ribbon");
            Elements linkURLElements = article.select("a.flex-link");
            Elements priceElements1 = article.select("div.price-col-1");
            Elements priceElements2 = article.select("div.price-col-2");
            Elements actualPriceElements = priceElements1.select("span.actualPrice");
            Elements averagePerNightElements = priceElements1.select("li.avgPerNight.priceType");
            Elements salfePercentOffElements = priceElements1.select("span.salePercentOff");
            Elements saleElements = priceElements2.select("li.drrMsg");
            Elements saleTagElements = priceElements2.select("li.etp.secondary");

            String hotelId = article.attr("data-hotelid");
            String hotelName = hotelNameElements.size() > 0 ? hotelNameElements.first().text() : StringUtils.EMPTY;
            String secondaryHotelName = secondaryHotelNameElements.size() > 0 ? secondaryHotelNameElements.first().text() : StringUtils.EMPTY;
            String startRating = startRatingElements.size() > 0 ? startRatingElements.first().text() : StringUtils.EMPTY;
            String linkURL = linkURLElements.size() > 0 ? linkURLElements.first().attr("href") : StringUtils.EMPTY;
            String averagePerNight = averagePerNightElements.size() > 0 ? StringUtils.normalizeSpace(averagePerNightElements.first().text()) : StringUtils.EMPTY;
            String price = actualPriceElements.size() > 0 ? StringUtils.normalizeSpace(actualPriceElements.first().text()) : StringUtils.EMPTY;
            String salePercentOff = salfePercentOffElements.size() > 0 ? StringUtils.normalizeSpace(salfePercentOffElements.first().text()) : StringUtils.EMPTY;
            String sale = saleElements.size() > 0 ? StringUtils.normalizeSpace(saleElements.first().text()) : StringUtils.EMPTY;
            String saleTag = saleTagElements.size() > 0 ? StringUtils.normalizeSpace(saleTagElements.first().text()) : StringUtils.EMPTY;
            String npp = nppElements.size() > 0 ? StringUtils.normalizeSpace(nppElements.first().text()) : StringUtils.EMPTY;
            String badge = badgeElements.size() > 0 ? StringUtils.normalizeSpace(badgeElements.first().text()) : StringUtils.EMPTY;
            String freeCancel = freeCancelElements.size() > 0 ? StringUtils.normalizeSpace(freeCancelElements.first().text()) : StringUtils.EMPTY;

            List<String> discountRibbons = new ArrayList<>();
            for (Element discountRibbon : discountRibbonElements) {
                discountRibbons.add(StringUtils.normalizeSpace(discountRibbon.text()));
            }

            //  Log
            log.debug("id : {}, name : {} ({}), star : {}, price : {}, sale : {}, sale(%) : {}, sale tag : {}, avgPerNight : {}, npp : {}, badge : {}, free-cancel : {}, link : {}",
                    hotelId, hotelName, secondaryHotelName, startRating, price, sale, salePercentOff, saleTag, averagePerNight, npp, badge, freeCancel, linkURL);
        }
    }
}
