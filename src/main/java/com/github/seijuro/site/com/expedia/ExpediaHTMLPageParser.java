package com.github.seijuro.site.com.expedia;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.expedia.data.ExpediaHotel;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExpediaHTMLPageParser implements HTMLPageParser<ExpediaHotel> {
    @Override
    public List<ExpediaHotel> parse(String html) {
        List<ExpediaHotel> results = new ArrayList<>();
        Document pageDocument = Jsoup.parse(html);
        Elements articles = pageDocument.select("article[data-hotelid]");

        for (Element article : articles) {
            ExpediaHotel.Builder hotelBuilder = new ExpediaHotel.Builder();

            Elements hotelInfoElements = article.select("ul.hotel-info");
            Elements hotelNameElements = hotelInfoElements.select("li.hotelTitle");
            Elements secondaryHotelNameElements = hotelInfoElements.select("li.secondaryHotelName");
            Elements nppElements = hotelInfoElements.select("li.npp");
            Elements badgeElements = hotelInfoElements.select("li.hotelBadge");
            Elements startRatingElements = hotelInfoElements.select("li.starRating.secondary");
            Elements groupBadgesElements = hotelInfoElements.select("li.groupBadges");

            Elements freeCancelElements = article.select("li.freeCancel.secondary");
            Elements discountRibbonElements = article.select("li.discount-ribbon.secondary.flex-flag").not(".hidden");
            Elements linkURLElements = article.select("a.flex-link");
            Elements priceElements1 = article.select("div.price-col-1");
            Elements priceElements2 = article.select("div.price-col-2");
            Elements actualPriceElements = priceElements1.select("span.actualPrice");
            Elements averagePerNightElements = priceElements1.select("li.avgPerNight.priceType");
            Elements salfePercentOffElements = priceElements1.select("span.salePercentOff");
            Elements saleElements = priceElements2.select("li.drrMsg");
            Elements saleTagElements = priceElements2.select("li.etp.secondary");

            hotelBuilder.setId(article.attr("data-hotelid"));
            hotelBuilder.setName(hotelNameElements.size() > 0 ? hotelNameElements.first().text() : StringUtils.EMPTY);
            hotelBuilder.setSecondaryName(secondaryHotelNameElements.size() > 0 ? secondaryHotelNameElements.first().text() : StringUtils.EMPTY);
            hotelBuilder.setRating(startRatingElements.size() > 0 ? startRatingElements.first().text() : StringUtils.EMPTY);
            hotelBuilder.setLinkURL(linkURLElements.size() > 0 ? linkURLElements.first().attr("href") : StringUtils.EMPTY);
            hotelBuilder.setAveragePerNight(averagePerNightElements.size() > 0 ? StringUtils.normalizeSpace(averagePerNightElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setPrice(actualPriceElements.size() > 0 ? StringUtils.normalizeSpace(actualPriceElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setSalePercentOff(salfePercentOffElements.size() > 0 ? StringUtils.normalizeSpace(salfePercentOffElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setSale(saleElements.size() > 0 ? StringUtils.normalizeSpace(saleElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setSaleTag(saleTagElements.size() > 0 ? StringUtils.normalizeSpace(saleTagElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setNpp(nppElements.size() > 0 ? StringUtils.normalizeSpace(nppElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setBadge(badgeElements.size() > 0 ? StringUtils.normalizeSpace(badgeElements.first().text()) : StringUtils.EMPTY);
            hotelBuilder.setVip(groupBadgesElements.select("img.vipBadgeRebranded").size() > 0 ? true : false);
            hotelBuilder.setFreeCancel(freeCancelElements.size() > 0 ? StringUtils.normalizeSpace(freeCancelElements.first().text()) : StringUtils.EMPTY);

            for (Element discountRibbon : discountRibbonElements) {
                hotelBuilder.addDiscountRibbon(StringUtils.normalizeSpace(discountRibbon.text()));
            }

            results.add(hotelBuilder.build());
        }

        return results;
    }
}
