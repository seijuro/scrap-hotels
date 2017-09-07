package com.github.seijuro.site.com.agoda;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.agoda.data.AgodaHotelReview;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AgodaHotelReviewHTMLParser implements HTMLPageParser<AgodaHotelReview> {
    private final String hotelId;

    public AgodaHotelReviewHTMLParser(String hotelId) {
        this.hotelId = hotelId;
    }

    @Override
    public List<AgodaHotelReview> parse(String html) {

        List<AgodaHotelReview> reviews = new ArrayList<>();
        Document document = Jsoup.parse(html);

        Element bodyElement = document.body();
        Elements hotelReviewDetailElements = bodyElement.getElementsByAttributeValue("data-selenium", "individual-review-section");

        for (Element hotelReviewDetailElement : hotelReviewDetailElements) {
            AgodaHotelReview.Builder reviewBuiler = new AgodaHotelReview.Builder();

            reviewBuiler.setHotelId(hotelId);
            reviewBuiler.setReviewId(hotelReviewDetailElement.attr("data-id"));

            String locale = StringUtils.stripToEmpty(hotelReviewDetailElement.attr("data-locale"));
            if (locale.startsWith("ko")) {
                reviewBuiler.setLocale("한국어");
            }
            else if (locale.startsWith("en")) {
                reviewBuiler.setLocale("영어");
            }
            else {
                reviewBuiler.setLocale("그 외");
            }

            Elements reviewerDetail = hotelReviewDetailElement.getElementsByAttributeValue("data-selenium", "reviewer-detail");

            //  reviewer detail
            if (reviewerDetail.size() > 0) {
                Element reviewerDetailElement = reviewerDetail.first();

                Elements scoreElements = reviewerDetailElement.select("div.comment-score");
                Elements nameElements = reviewerDetailElement.getElementsByAttributeValue("data-selenium", "reviewer-name");
                Elements roomTypeElements = reviewerDetailElement.getElementsByAttributeValue("data-selenium", "review-roomtype");
                Elements stayDetailElements = reviewerDetailElement.getElementsByAttributeValue("data-selenium", "reviewer-stay-detail");

                //  score
                if (scoreElements.size() > 0) {
                    reviewBuiler.setScore(scoreElements.first().text());
                }

                //  name
                if (nameElements.size() > 0) {
                    String[] tokens = StringUtils.stripToEmpty(nameElements.first().text()).split("/");

                    reviewBuiler.setReviewerName(tokens[0]);
                    if (tokens.length > 1) { reviewBuiler.setCountry(tokens[1]); }
                }

                //  room type
                if (roomTypeElements.size() > 0) {
                    Element roomTypeElement = roomTypeElements.first();

                    reviewBuiler.setRoomType(roomTypeElement.text());
                }

                //  stay detail
                if (stayDetailElements.size() > 0) {
                    Element stayDetailElement = stayDetailElements.first();

                    String[] tokens = StringUtils.stripToEmpty(stayDetailElement.text()).split("\\|");
                    String stayDate = tokens[0];

                    //  stay date
                    reviewBuiler.setStayDate(StringUtils.normalizeSpace(stayDate));

                    if (tokens.length > 1) {
                        String stayDays = tokens[1];

                        //  stay days
                        reviewBuiler.setStayDays(StringUtils.normalizeSpace(stayDays));
                    }
                }
            }


            Elements commentDetail = hotelReviewDetailElement.select("div[data-selenium='individual-review-section']");

            //  comment detail
            if (commentDetail.size() > 0) {
                Element commentDetailElement = commentDetail.first();

                Elements commentTitleElements = commentDetailElement.select("div.comment-title-text");
                Elements commentDateElements = commentDetailElement.getElementsByAttributeValue("data-selenium", "review-date");

                //  comment title
                if (commentTitleElements.size() > 0) {
                    String title = StringUtils.normalizeSpace(commentTitleElements.first().text());

                    if (title.endsWith("”")) {
                        title = title.substring(0, title.length() - 1);
                    }
                    reviewBuiler.setTitle(title);
                }

                //  post date
                if (commentDateElements.size() > 0) {
                    String[] tokens = StringUtils.normalizeSpace(commentDateElements.first().text()).split(":");

                    if (tokens.length == 1) {
                        reviewBuiler.setPostDate(tokens[0]);
                    }
                    else if (tokens.length > 1) {
                        reviewBuiler.setPostDate(tokens[1]);
                    }
                }
            }

            Elements responseElements = hotelReviewDetailElement.select("div[data-selenium='individual-response-panel']");

            //  response
            if (responseElements.size() > 0) {
                reviewBuiler.setHasResponse(true);
            }

            reviews.add(reviewBuiler.build());
        }

        return reviews;
    }
}
