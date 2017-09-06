package com.github.seijuro.site.com.agoda;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.agoda.data.AgodaHotel;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@ToString
public class AgodaHTMLParser implements HTMLPageParser<AgodaHotel> {
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



    private String extractStarRating(Elements startElements) {
        if (startElements.size() > 0) {
            String className = startElements.first().attr("class");

            if (className.contains("5")) {
                return "5";
            } else if (className.contains("4")) {
                return "4";
            } else if (className.contains("3")) {
                return "3";
            } else if (className.contains("2")) {
                return "2";
            } else if (className.contains("1")) {
                return "1";
            }
        }

        return "0";
    }

    public AgodaHotel parseHotelElement(String hotelId, Element hotelElement) {
        AgodaHotel.Builder hotelBuilder = new AgodaHotel.Builder();

        Element hotelContainerElement = hotelElement.getElementById(String.format("hotel-%s-container", hotelId));
        Elements hotelInfoElements = hotelElement.getElementsByAttributeValue("data-selenium", "hotel-info");
        Elements priceAndPromoteElements = hotelElement.getElementsByAttributeValue("data-selenium", "hotel-price-promo-container");
        Element hotelInfoElement = hotelInfoElements.first();
        Element priceAndPromoteElement = priceAndPromoteElements.first();
        Elements optionContainerElements = hotelInfoElement.getElementsByAttributeValue("data-selenium", "pill-container");
        Elements couponBadgeElements = hotelInfoElement.getElementsByAttributeValue("data-selenium", "coupon-badge");
        Elements GCAElements = hotelInfoElement.getElementsByAttributeValue("data-selenium", "hotel-gca-badge");
        Elements thumbUpElements = hotelInfoElement.getElementsByAttributeValue("data-selenium", "green-thumb");
        Element reviewContainerElement = priceAndPromoteElements.select("div.hotel-review-container").first();
        Elements priceContainerElements = priceAndPromoteElement.getElementsByAttributeValue("data-selenium", "price-container");
        Elements discountRibbonElements = priceAndPromoteElement.getElementsByAttributeValue("data-selenium", "discount-ribbon");

        //  (default)
        hotelBuilder.setDomain(domain);
        hotelBuilder.setDestination(destination);
        hotelBuilder.setSort(sort);
        hotelBuilder.setStartDate(startDate);
        hotelBuilder.setEndDate(endDate);

        //  hotel id
        hotelBuilder.setId(hotelId);
        //  linkURL
        hotelBuilder.setLinkURL(hotelContainerElement.attr("href"));
        //  hotel name
        hotelBuilder.setName(hotelInfoElement.select("h3.hotel-name").first().text());
        //  star rating
        hotelBuilder.setStarRating(extractStarRating(hotelInfoElement.select("i.ficon")));
        //  options
        if (optionContainerElements.size() > 0) {
            for (int index = 0; index < optionContainerElements.size(); ++index) {
                Element optionContainerElement = optionContainerElements.get(index);

                if (optionContainerElement.attr("class").contains("orange")) {
                    Elements optionItemElements = optionContainerElement.getElementsByAttributeValue("data-selenium", "pill-item");
                    for (Element optionItemElement : optionItemElements) {
                        String optionTextx = optionItemElement.select("span").text();
                        hotelBuilder.setOption(optionItemElement.select("span").text());

                        if (optionTextx.contains("무료") &&
                                optionTextx.contains("취소")) {
                            hotelBuilder.setFreeCancellation(true);
                        }
                    }
                }
            }
        }
        //  review score
        if (reviewContainerElement.attr("class").contains("has-reviews")) {
            //  exists reviews
            Elements scoreElements = reviewContainerElement.getElementsByAttributeValue("data-selenium", "review-socre");
            Elements reviewCountElements = reviewContainerElement.getElementsByAttributeValue("data-selenium", "review-count");

            if (scoreElements.size() > 0) {
                hotelBuilder.setReviewsScore(scoreElements.text());
            }

            if (reviewCountElements.size() > 0) {
                String reviewsCountText = reviewCountElements.text();
                if (StringUtils.isNotEmpty(reviewsCountText)) {
                    String formalzedCountText = reviewsCountText;

                    if (reviewsCountText.endsWith("+")) {
                        formalzedCountText = reviewsCountText.substring(0, reviewsCountText.length() - 1);
                    }

                    String[] tokens = reviewsCountText.split("\\s+");
                    if (tokens.length > 1) {
                        formalzedCountText = tokens[tokens.length - 1];
                    }


                    hotelBuilder.setReviewCount(Integer.parseInt(formalzedCountText));
                }
            }
        }
        //  price
        if (priceContainerElements.size() > 0) {
            //  currency
            Element priceContainerElement = priceContainerElements.first();
            Elements currencyElements = priceContainerElement.getElementsByAttributeValue("data-selenium", "hotel-currency");
            Elements priceElements = priceContainerElement.getElementsByAttributeValue("data-selenium", "display-price");

            if (currencyElements.size() > 0) {
                hotelBuilder.setCurrency(currencyElements.text());
            }

            if (priceElements.size() > 0) {
                hotelBuilder.setPrice(priceElements.text());
            }
        }
        //  discount
        if (discountRibbonElements.size() > 0) {
            Elements discountPriceElements = discountRibbonElements.first().getElementsByAttributeValue("class", "price-text");

            if (discountPriceElements.size() > 0) {
                hotelBuilder.setDiscountRibbon(discountPriceElements.text());
            }
        }
        //  coupon
        if (couponBadgeElements.size() > 0) {
            Elements couponBadgeDetailElements = couponBadgeElements.first().getElementsByAttributeValue("data-selenium", "coupon-detail");

            if (couponBadgeDetailElements.size() > 0) {
                hotelBuilder.setCouponDiscount(couponBadgeDetailElements.text());
            }
        }
        //  award
        if (GCAElements.size() > 0) {
            hotelBuilder.setAward(GCAElements.text());
        }
        //  thumbup icon
        hotelBuilder.setThumbUp(thumbUpElements.size() > 0);

        return hotelBuilder.build();
    }

    @Override
    public List<AgodaHotel> parse(String html) {
        Document pageDocument = Jsoup.parse(html);
        Elements hotels = pageDocument.getElementsByAttributeValue("data-selenium", "hotel-item");

        List<AgodaHotel> results = new ArrayList<>();
        for (Element hotel : hotels) {
            String hotelId = hotel.attr("data-hotelid");

            try {
                results.add(parseHotelElement(hotelId, hotel));
            }
            catch (Exception excp) {
                excp.printStackTrace();
            }
        }

        return results;
    }
}
