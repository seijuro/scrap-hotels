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
        Elements contentWrapElemetns = document.body().select("div.site-content-wrap.hotelInformation");

        if (contentWrapElemetns.size() > 0) {
            Element siteContent = contentWrapElemetns.first();

            Elements pageHeaderElements = siteContent.select("header.page-header");
            Elements hotelOverviewElements = siteContent.select("hotel-overview");
            Element policiesAndAmenties = siteContent.getElementById("policies-and-amenities");

            Element roomsAndRates = siteContent.getElementById("rooms-and-rates");
            Elements reviewsSummaryElements = siteContent.select("article.reviews-summary");

            //  hotel id
            hotelBuilder.setId(hotelId);

            //  header
            if (pageHeaderElements.size() > 0) {
                //  Log
                log.debug("PARSING 'header.page-header'");

                Element pageHeader = pageHeaderElements.first();

                Element leadPriceContainer = pageHeader.getElementById("lead-price-container");

                //  name
                Element hotelName = pageHeader.getElementById("hotel-name");
                if (Objects.nonNull(hotelName)) {
                    //  Log
                    log.debug("PARSING id='hotel-name'");

                    hotelBuilder.setName(StringUtils.normalizeSpace(hotelName.text()));
                }

                Elements addressElements = pageHeader.select("div.address");
                if (addressElements.size() > 0) {
                    //  Log
                    log.debug("PARSING div.address");

                    //  address
                    Element addressLink = addressElements.first().child(0);
                    hotelBuilder.setAddress(StringUtils.normalizeSpace(addressLink.text()));
                }

                Elements starRatingElements = pageHeader.select("div.star-rating-wrapper");
                //  Log
                log.debug("PARSING div.star-rating-wrapper");

                if (starRatingElements.size() > 0) {
                    Element starRating = starRatingElements.first();

                    Elements starIconElements = starRating.child(0).select("span.icon");
                    if (starIconElements.size() > 0) {
                        //  Log
                        log.debug("PARSING span.icon");

                        //  star rating
                        hotelBuilder.setStarRating(starIconElements.first().attr("title"));
                    }
                }
            }

            if (reviewsSummaryElements.size() > 0) {
                Element reviewsSummary = reviewsSummaryElements.first();

                Elements guestRatingElements = reviewsSummary.select("div.guest-rating");
                Elements linkElements = reviewsSummary.select("a.reviews-link.link-to-reviews");

                if (linkElements.size() > 0) {
                    //  Log
                    log.debug("PARSING a.reviews-link.link-to-reviews");

                    hotelBuilder.setReviewCount(Integer.parseInt(linkElements.first().child(0).text().replace(",", "")));
                }

                if (guestRatingElements.size() > 0) {
                    //  Log
                    log.debug("PARSING span.rating-number");

                    Elements ratingNumberElements = guestRatingElements.first().select("span.rating-number");

                    //  리뷰 평점
                    if (ratingNumberElements.size() > 0) {
                        //  Log
                        log.debug("PARSING span.rating-number");

                        hotelBuilder.setGuestRating(ratingNumberElements.first().text());
                    }
                }
            }

            if (Objects.nonNull(roomsAndRates)) {
                Elements roomsElements = roomsAndRates.select("tr.rate-plan");
                //  Log
                log.debug("PARSING tr.rate-plan");

                for (Element room : roomsElements) {
                    //  Log
                    log.debug("PARSING td.rate-features");

                    Elements rateFeatureElements = room.select("td.rate-features");

                    //  Log
                    log.debug("PARSING div.room-amenity.free-breakfast");

                    Elements freeBreakfastElements = rateFeatureElements.select("div.room-amenity.free-breakfast");

                    if (freeBreakfastElements.size() > 0) {
                        Element freeBreakfast = freeBreakfastElements.first();

                        if (freeBreakfast.text().contains("무료") &&
                                freeBreakfast.text().contains("아침 식사")) {
                            hotelBuilder.setBreakfastInclude(true);
                        }
                    }
                }
            }

            if (hotelOverviewElements.size() > 0) {
                //  hotel description
                Elements hotelDescriptionElements = hotelOverviewElements.first().select("div.hotel-description");

                if (hotelDescriptionElements.size() > 0) {
                    Element hotelDescription = hotelDescriptionElements.first();

                    Elements children = hotelDescription.children();

                    for (Element child : children) {
                        if (child.tagName().equals("p")) {
                            int index = children.indexOf(child);
                            if (index > 0 &&
                                    children.get(index - 1).text().contains("위치")) {
                                String locationInfoText = child.text();

                                //  위치 설명
                                if (locationInfoText.contains("해변") ||
                                        locationInfoText.contains("해수욕장") ||
                                        locationInfoText.contains("해안") ||
                                        locationInfoText.contains("해변")) {
                                    if (locationInfoText.contains("근처에")) {
                                        hotelBuilder.setBeach(ExpediaHotelDetail.BeachInfo.NEAR);
                                    } else {
                                        hotelBuilder.setBeach(ExpediaHotelDetail.BeachInfo.EXIST);
                                    }
                                }
                            }
                        }
                    }
                }
            }

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
                                        hotelBuilder.setBuiltYear(StringUtils.normalizeSpace(tokens[1]));
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
                                        hotelBuilder.setFloor(StringUtils.normalizeSpace(tokens[1]));
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

        return Arrays.asList(hotelBuilder.build());
    }
}
