package com.github.seijuro.site.com.expedia;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.expedia.data.ExpediaHotelDetail;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
public class ExpediaHotelDetailHTMLParser implements HTMLPageParser<ExpediaHotelDetail> {
    private final String hotelId;

    public ExpediaHotelDetailHTMLParser(String hotelId) {
        this.hotelId = hotelId;
    }

    @Override
    public List<ExpediaHotelDetail> parse(String html) {
        ExpediaHotelDetail.Builder hotelBuilder = new ExpediaHotelDetail.Builder();

        Document document = Jsoup.parse(html);
        Element body = document.body();

        Elements licensePlateElements = body.select("div.site-content-wrap.hotelInformation div.site-content header.page-header div#license-plate");
        Elements pageHeaderElements = body.select("header.page-header");
        Elements contentWrapElemetns = document.body().select("div.site-content-wrap.hotelInformation");
        Elements priceElements = pageHeaderElements.select("div #lead-price-container div div a.price.link-to-rooms");
        Elements guestRatingElements = body.select("div div section section div article div div div.guest-rating span.rating-number");
        Elements reviewCountElements = body.select("div div section section div article div div a.reviews-link.link-to-reviews span");
        Elements hotelDescriptionElements = body.select("div div.site-content section.page-content div.hotel-overview div.hotel-description");
        Elements refProductElements = body.select("div.site-content-wrap.hotelInformation div.site-content section.page-content div#rooms-and-rates div article.rooms-and-rates-segment table tbody.room.first-room-featured tr.rate-plan.rate-plan-first");

        //  hotel id
        hotelBuilder.setId(hotelId);

        if (licensePlateElements.size() > 0) {
            Elements starRatingElements = licensePlateElements.select("div.star-rating-wrapper strong.star-rating.star.rating span.icon");
            Elements addressElements = licensePlateElements.select("div.address div a.map-link");

            //  hotel name
            hotelBuilder.setName(licensePlateElements.first().getElementById("hotel-name").text());
            //  hotel starRating
            if (starRatingElements.size() > 0) { hotelBuilder.setStarRating(starRatingElements.first().attr("title")); }
            //  hotel address
            if (addressElements.size() > 0) {
                String addressText = addressElements.first().text();
                if (addressText.startsWith("지도")) { addressText = addressText.substring(2, addressText.length()); }
                hotelBuilder.setAddress(StringUtils.normalizeSpace(addressText));
            }
        }

        //  pool & beach
        if (hotelDescriptionElements.size() > 0) {
            Element hotelDescription = hotelDescriptionElements.first();

            //  수영장 #1
            Elements amenities = hotelDescription.select("div div.important-amenities-container div span.amenity-amenity-element");
            for (Element amenity : amenities) {
                String amenityText = amenity.text();
                //  수영장
                if (amenityText.contains("수영장") &&
                        (amenityText.contains("실내") || amenityText.contains("실외") || amenityText.contains("어린이"))) {
                    hotelBuilder.setHasPool(true);
                }
                else if (amenityText.contains("유수풀")) {
                    hotelBuilder.setHasPool(true);
                }
            }

            //
            Elements descriptionText = hotelDescription.select("p");
            for (Element desc : descriptionText) {
                String text = desc.text();
                //  위치 설명
                if (text.contains("해변") ||
                        text.contains("해수욕장") ||
                        text.contains("해안") ||
                        text.contains("해변")) {
                    if (text.contains("근처에")) {
                        hotelBuilder.setBeach(ExpediaHotelDetail.BeachInfo.NEAR);
                    }
                    else {
                        hotelBuilder.setBeach(ExpediaHotelDetail.BeachInfo.EXIST);
                    }
                }
            }
        }

        if (refProductElements.size() > 0) {
            Element refProduct = refProductElements.first();

            Elements freeCancellationElements = refProduct.select("td.rate-features div div.rate-policies a.free-cancellation-tooltip-link span.free-cancellation-short.free-text");
            Elements freeBreakfastElements = refProduct.select("td.rate-features div div.rate-includes div.room-amenity.free-breakfast span.free-text");

            if (freeCancellationElements.size() > 0) {
                if (freeCancellationElements.first().text().contains("예약 무료 취소")) { hotelBuilder.setFreeCancellation(true); }
                if (freeCancellationElements.first().text().contains("환불 불가")) { hotelBuilder.setFreeCancellation(false); }
            }

            if (freeBreakfastElements.size() > 0) {
                String breakfast = freeBreakfastElements.first().text();
                if (breakfast.contains("아침 식사") && breakfast.contains("포함")) { hotelBuilder.setBreakfastInclude(true); }
            }
        }

        if (guestRatingElements.size() > 0) { hotelBuilder.setGuestRating(guestRatingElements.first().text()); }
        if (reviewCountElements.size() > 0) { hotelBuilder.setReviewCount(Integer.parseInt(reviewCountElements.first().text().replace(",", ""))); }
        if (priceElements.size() > 0) { hotelBuilder.setRefProductPrice(priceElements.first().text()); }

        if (contentWrapElemetns.size() > 0) {
            Element siteContent = contentWrapElemetns.first();
            Element policiesAndAmenties = siteContent.getElementById("policies-and-amenities");

            if (Objects.nonNull(policiesAndAmenties)) {
                //  amenties
                Element amenities = policiesAndAmenties.getElementById("hotel-amenities");
                //  policies
                Element policies = policiesAndAmenties.getElementById("hotel-policies-and-fees");

                //  amenties
                if (Objects.nonNull(amenities)) {
                    Elements amenitiesGeneralElements = amenities.select("div[data-section='amenities-general']");
                    Elements amenitiesFamilyElements = amenities.select("div[data-section='amenities-family']");

                    //  amenities - general
                    if (amenitiesGeneralElements.size() > 0) {
                        Element amenitiesGeneral = amenitiesGeneralElements.first();

                        Elements items = amenitiesGeneral.select("ul");

                        if (items.size() > 0) {
                            //  li(s)
                            for (Element item : items.first().children()) {
                                String text = item.text();

                                if (text.contains("총 객실 수")) {
                                    String[] tokens = text.split("-");
                                    if (tokens.length > 1) {
                                        //  객실 수
                                        hotelBuilder.setRooms(StringUtils.normalizeSpace(tokens[1]));
                                    }
                                }

                                //  준공연도
                                if (text.contains("준공연도")) {
                                    String[] tokens = text.split(":");
                                    if (tokens.length > 1) {
                                        hotelBuilder.setBuiltDate(StringUtils.normalizeSpace(tokens[1]));
                                    }
                                }

                                //  회의실 유무
                                if (text.contains("회의실 수") ||
                                        text.contains("회의 공간")) {
                                    hotelBuilder.setHasMeetingRoom(true);
                                }

                                //  카지노
                                if (text.contains("카지노")) {
                                    hotelBuilder.setHasCasino(true);
                                }

                                //  피트니스
                                if (text.contains("피트니스") &&
                                        text.contains("시설")) {
                                    hotelBuilder.setHasFitness(true);
                                } else if (text.contains("헬스클럽")) {
                                    hotelBuilder.setHasFitness(true);
                                }

                                //  층 수
                                if (text.contains("층 수")) {
                                    String[] tokens = text.split("-");
                                    if (tokens.length > 0) {
                                        hotelBuilder.setFloors(StringUtils.normalizeSpace(tokens[1]));
                                    }
                                }

                                //  레스토랑
                                if (text.contains("레스토랑")) {
                                    hotelBuilder.setHasRestaurant(true);
                                }

                                //  수영장
                                if (text.contains("수영장") &&
                                        (text.contains("실내") || text.contains("실외") || text.contains("어린이"))) {
                                    hotelBuilder.setHasPool(true);
                                } else if (text.contains("유수풀")) {
                                    hotelBuilder.setHasPool(true);
                                }
                            }
                        }
                    }

                    //  가족 사항
                    if (amenitiesFamilyElements.size() > 0) {
                        Element amenitiesFamily = amenitiesFamilyElements.first();
                        Elements items = amenitiesFamily.select("ul");

                        if (items.size() > 0) {
                            // li(s)
                            for (Element item : items.first().children()) {
                                String text = item.text();

                                if (text.contains("수영장") &&
                                        (text.contains("실내") || text.contains("실외") || text.contains("어린이"))) {
                                    hotelBuilder.setHasPool(true);
                                } else if (text.contains("유수풀")) {
                                    hotelBuilder.setHasPool(true);
                                }
                            }
                        }
                    }
                }
            }
        }

        //  Log
        log.debug("hotel-detail : {}", hotelBuilder.toString());

        return Arrays.asList(hotelBuilder.build());
    }
}
