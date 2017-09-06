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
        Elements hotelReviewSectionElements = bodyElement.select("div.ReviewSection");
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

        //  review section
        if (hotelReviewSectionElements.size() > 0) {
            Element hotelReviewSectionElement = hotelReviewSectionElements.first();
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
            Element favoriteFeaturesElement = favoriteFeaturesElements.first();

            Elements beachElements =favoriteFeaturesElement.select("i[class~='ifcon-beach']");

            if (beachElements.size() > 0) {
                hotelBuilder.setBeach(beachElements.first().text());
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
                    Elements wifiElements = groupElement.select("i[class~='ficon-wifi']");
                    Elements poolElements = groupElement.select("i[class~='ficon-private-pool']");

                    //  인터넷
                    if (wifiElements.size() > 0) {
                        //  do nothings
                    }

                    //  액티비티 및 레저 활동
                    if (poolElements.size() > 0) {
                        Elements fitnessElements = poolElements.select("i[class~='ficon-fitness-center'");
                        Elements casinoElements = poolElements.select("i[class~='ficon-casino'");
                        Elements outdoorPoolElements = poolElements.select("i[class~='ficon-outdoor-pool'");
                        Elements restaurantPoolElements = poolElements.select("i[class~='ficon-restaurant'");

                        if (fitnessElements.size() > 0) {
                            //  피트니스센터
                            hotelBuilder.setHasFitness(true);
                        }

                        if (casinoElements.size() > 0) {
                            //  카지노
                            hotelBuilder.setHasCasino(true);
                        }

                        if (outdoorPoolElements.size() > 0) {
                            //  실외 수영장
                            hotelBuilder.setHasPool(true);
                        }

                        if (restaurantPoolElements.size() > 0) {
                            //  레스토랑
                            hotelBuilder.setHasRestaurant(true);
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

                                hotelBuilder.setBuiltYear(StringUtils.normalizeSpace(tokens[tokens.length - 1]));
                            }

                            if (childElement.child(0).attr("class").contains("ficon-number-of-floors")) {
                                // 총 층수
                                String[] tokens = info.text().split(":");

                                hotelBuilder.setFloor(StringUtils.normalizeSpace(tokens[tokens.length - 1]));
                            }
                        }
                    }
                }
            }
        }


        return Arrays.asList(hotelBuilder.build());
    }
}
