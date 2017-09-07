package com.github.seijuro.site.com.agoda;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.agoda.data.AgodaHotelDetail;
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
public class AgodaDetailHTMLParser implements HTMLPageParser<AgodaHotelDetail> {
    private final String hotelId;

    public AgodaDetailHTMLParser(String hotelId) {
        this.hotelId = hotelId;
    }

    @Override
    public List<AgodaHotelDetail> parse(String html) {
        AgodaHotelDetail.Builder hotelBuilder = new AgodaHotelDetail.Builder();
        //  ID
        hotelBuilder.setId(hotelId);

        Document document = Jsoup.parse(html);

        Element bodyElement = document.body();
        Elements hotelHeaderElements = bodyElement.getElementsByAttributeValue("class", "hotel-header");
        Elements hotelReviewSectionElements = bodyElement.select("div.ReviewSection");              //  summary
        Elements customerReviewSectionElements = bodyElement.select("div.customer-review-section"); //  detail
        Element hotelReviewDetailElement = bodyElement.getElementById("hotelreview-detail-item");
        Elements reviewCommentsCountElements = bodyElement.select("div.review-comments-count"); //  detail
        Elements favoriteFeaturesElements = bodyElement.select("div.fav-features__body");
        Elements aboutHotelElements = bodyElement.getElementsByAttributeValue("data-selenium", "abouthotel-panel");
        Element roomsElement = bodyElement.getElementById("roomGridContent");

        //  hotel header
        if (hotelHeaderElements.size() > 0) {
            Element hotelHeaderElement = hotelHeaderElements.size() > 0 ? hotelHeaderElements.first() : null;
            Elements addressElements = Objects.nonNull(hotelHeaderElement) ? hotelHeaderElement.getElementsByAttributeValue("class", "hotel-header-info-address") : null;
            Element addressElement = Objects.nonNull(addressElements) ? addressElements.first() : null;

            if (Objects.nonNull(addressElement)) {
                //  주소
                hotelBuilder.setAddress(StringUtils.normalizeSpace(addressElement.text()));
            }
        }

        //  review section #1
//        if (customerReviewSectionElements.size() > 0) {
//            Element customerReviewSectionElement = customerReviewSectionElements.first();
//
//            Elements reviewTabElements = customerReviewSectionElement.select("div.review-tab");
//            for (Element reviewTab : reviewTabElements) {
//                Elements agodaReviews = reviewTab.getElementsByAttributeValue("data-redirect", "false");
//
//                if (agodaReviews.size() > 0) {
//                    //  아고다 리뷰 카운트
//                    hotelBuilder.setAgodaReviewCount(Integer.parseInt(agodaReviews.attr("data-count")));
//                }
//            }
//        }

        //  review section #2
//        if (reviewCommentsCountElements.size() > 0) {
//            String reviewCommentsCountText = reviewCommentsCountElements.first().text();
//            reviewCommentsCountText = reviewCommentsCountText.replace("100%", "");
//            hotelBuilder.setAgodaReviewCount(Integer.parseInt(reviewCommentsCountText.replaceAll("[^0-9]", "")));
//        }

        //  review section #3 <--
        if (Objects.nonNull(hotelReviewDetailElement)) {
            hotelBuilder.setAgodaReviewCount(Integer.parseInt(hotelReviewDetailElement.attr("data-totalindex")));
        }

        if (Objects.nonNull(roomsElement)) {
            Elements roomInfoElement = roomsElement.getElementsByAttributeValue("data-selenium", "MasterRoom");

            if (roomInfoElement.size() > 0) {
                //  room(s) - top item
                Element roomElement = roomInfoElement.first();

                Elements childRoomElements = roomElement.getElementsByAttributeValue("data-selenium", "ChildRoomsList-room");

                if (childRoomElements.size() > 0) {
                    Element childRoomElement = childRoomElements.first();
                    Elements offerAndConditionElements = childRoomElement.select("div[data-ppapi='offer-and-condition']");
                    Elements bookButtonElements = childRoomElement.select("div.ChildRoomsList-bookButton");

                    if (offerAndConditionElements.size() > 0) {
                        String offerAndCondition = offerAndConditionElements.text();

                        if (offerAndCondition.contains("조식")) {
                            //  조식 포함
                            hotelBuilder.setBreakfastInclude(true);
                        }

                        if (offerAndCondition.contains("무료 취소")) {
                            //  무료 취소 #1

                        }
                    }

                    if (bookButtonElements.size() > 0) {
                        if (bookButtonElements.first().text().contains("무료 취소")) {
                            //  무료 취소 #2

                        }
                    }

                    Elements noteElements = childRoomElements.select("div.qa-TwoLineTaxAndSurcharge");
                    for (Element noteElement : noteElements) {
                        String text = noteElement.text();

                        if (text.contains("불포함")) {
                            if (text.contains("세금") ||
                                    text.contains("Tax")) {
                                //  세금 불포함
                                hotelBuilder.setTaxIncluded(false);
                            }
                        }
                    }
                }
            }
        }

        //  favorite features
        if (favoriteFeaturesElements.size() > 0) {
            Elements favoriteElements = favoriteFeaturesElements.select("li.fav-features__listitem");

            for (Element favoriteFeatureElement : favoriteElements) {
                if (favoriteFeatureElement.child(0).attr("class").contains("ficon-beach")) {
                    hotelBuilder.setBeachText(favoriteFeatureElement.text());
                }
            }
        }

        //  about hotel
        if (aboutHotelElements.size() > 0) {
            Element aboutHotelElement = aboutHotelElements.first();

            //  시설 및 서비스
            Element featuresInfo = aboutHotelElement.getElementById("abouthotel-features");
            if (Objects.nonNull(featuresInfo)) {
                Elements groupElements = featuresInfo.select("div.feature-group");

                for (Element groupElement : groupElements) {
                    //  액티비티 및 레저 활동
                    if (groupElement.select("i.ficon").attr("class").contains("ficon-private-pool")) {
                        Elements subSectionElements = groupElement.select("li.list-item");

                        for (Element subSectionItem : subSectionElements) {
                            Elements icons = subSectionItem.select("i.ficon");

                            for (Element icon : icons) {
                                String className = icon.attr("class");
                                if (className.contains("ficon-fitness-center")) {
                                    //  피트니스센터
                                    hotelBuilder.setHasFitness(true);
                                }
                                else if (className.contains("ficon-casino")) {
                                    //  카지노
                                    hotelBuilder.setHasCasino(true);
                                }
                                else if (className.contains("ficon-outdoor-pool")) {
                                    //  실외 수영장
                                    hotelBuilder.setHasPool(true);
                                }
                                else if (className.contains("ficon-restaurant")) {
                                    //  레스토랑
                                    hotelBuilder.setHasRestaurant(true);
                                }
                            }
                        }
                    }

                    //  식음료 시설/서비스
                    else if (groupElement.select("i.ficon").attr("class").contains("ficon-restaurant")) {
                        Elements subSectionElements = groupElement.select("li.list-item");

                        for (Element subSectionItem : subSectionElements) {
                            Elements icons = subSectionItem.select("i.ficon");

                            for (Element icon : icons) {
                                String className = icon.attr("class");
                                if (className.contains("ficon-restaurant")) {
                                    //  레스토랑
                                    hotelBuilder.setHasRestaurant(true);
                                }
                            }
                        }
                    }
                }
            }

            //  이용 정보
            Element usefulInfo = aboutHotelElement.getElementById("abouthotel-usefulinfo");
            if (Objects.nonNull(usefulInfo)) {
                Elements subSectionElements = usefulInfo.select("div.sub-section.no-margin");

                for (Element subSection : subSectionElements) {
                    Elements infos = subSection.select("div");

                    for (Element info : infos) {
                        Elements childrenElements = info.children();

                        if (childrenElements.size() > 0 &&
                                childrenElements.first().className().equals("useful-info-icon")) {
                            Element childElement =  childrenElements.first();

                            if (childElement.child(0).attr("class").contains("ficon-number-of-rooms")) {
                                //  총 객실 수
                                String[] tokens = info.text().split(":");

                                hotelBuilder.setRooms(StringUtils.normalizeSpace(tokens[tokens.length - 1]));
                            }

                            if (childElement.child(0).attr("class").contains("ficon-year-hotel-built")) {
                                // 건축 년도
                                String[] tokens = info.text().split(":");

                                hotelBuilder.setBuiltDate(StringUtils.normalizeSpace(tokens[tokens.length - 1]));
                            }

                            if (childElement.child(0).attr("class").contains("ficon-number-of-floors")) {
                                // 총 층수
                                String[] tokens = info.text().split(":");

                                hotelBuilder.setFloors(StringUtils.normalizeSpace(tokens[tokens.length - 1]));
                            }
                        }
                    }
                }
            }
        }


        return Arrays.asList(hotelBuilder.build());
    }
}
