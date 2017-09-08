package com.github.seijuro.site.com.expedia;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.expedia.data.ExpediaHotelReview;
import com.github.seijuro.site.common.data.HotelReview;
import com.github.seijuro.site.common.parser.HotelReviewParser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
public class ExpediaHotelReviewHTMLParser extends HotelReviewParser {
    /**
     * Construct <code>ExpediaHotelReviewHTMLParser</code>
     *
     * @param hotelId
     */
    public ExpediaHotelReviewHTMLParser(String hotelId) {
        super(hotelId);
    }

    /**
     * parse HTML page source. And extract all reviews and return the list of reviews
     *
     * @param html
     * @return
     */
    @Override
    public List<ExpediaHotelReview> parse(String html) {
        List<ExpediaHotelReview> results = new ArrayList<>();

        Document document = Jsoup.parse(html);
        Element body = document.body();

        assert (body != null);
        //  Log
        log.debug("Parsing HTML > body");

        Element reviewsContent = body.getElementById("reviews-content");

        if (Objects.nonNull(reviewsContent)) {
            //  Log
            log.debug("Parsing HTML > body > div#review-content");

            Elements articles = reviewsContent.select("div#review-list section#reviews.segmented-list.reviews-list article.segment.review");

            for (Element article : articles) {
                ExpediaHotelReview.Builder reviewBuilder = new ExpediaHotelReview.Builder();
                //  hotel_id
                reviewBuilder.setHotelId(getHotelId());
                //  review_id
                reviewBuilder.setReviewId(article.attr("data-review-id"));

                String attributes = article.attr("dataattributes");
                Elements reviewerNameElements = article.select("div.summary div.recommendation div.user-information div.user span");
                Elements reviewerLocationElements = article.select("div.summary div.recommendation div.user-information div.location");
                Elements reviewDateElements = article.select("div.summary div.recommendation div.date-posted");
                Elements guestRatingElements = article.select("div.details div.rating-header-container span.badge.rating-score span");
                Elements titleElements = article.select("div.details div.rating-header-container h3.review-title");
                Elements replyElements = article.select("div.details div.management-response div.text span");
                Elements replyDateElements = article.select("div.details div.management-response div.date-posted");

                //  extract locale information ...
                {
                    String[] tokens = attributes.split("\\|");
                    for (String token : tokens) {
                        String[] attr = token.split(":");
                        if (attr.length == 2) {
                            String attrKey = attr[0];
                            String attrValue = attr[1];

                            if (attrKey.equals("content-locale")) {
                                if (attrValue.startsWith("en")) {
                                    reviewBuilder.setLocale("영어");
                                }
                                else if (attrValue.startsWith("ko")) {
                                    reviewBuilder.setLocale("한국어");
                                }
                                else {
                                    reviewBuilder.setLocale("그 외");
                                }
                            }
                        }
                    }
                }

                if (reviewerNameElements.size() > 0) { reviewBuilder.setReviewer(StringUtils.normalizeSpace(reviewerNameElements.first().text())); }
                if (reviewerLocationElements.size() > 0) { reviewBuilder.setLocation(StringUtils.normalizeSpace(reviewerLocationElements.first().text())); }
                if (reviewDateElements.size() > 0) { reviewBuilder.setPostDate(StringUtils.normalizeSpace(reviewDateElements.first().text())); }
                if (guestRatingElements.size() > 0) { reviewBuilder.setScore(StringUtils.normalizeSpace(guestRatingElements.first().text())); }
                if (titleElements.size() > 0) { reviewBuilder.setTitle(StringUtils.normalizeSpace(titleElements.first().text())); }
                if (replyElements.size() > 0) { reviewBuilder.setHasResponse(true); }

                ExpediaHotelReview review = reviewBuilder.build();
                results.add(review);

                //  Log
                log.debug("review  : {}", review.toString());
            }
        }

        return results;
    }
}
