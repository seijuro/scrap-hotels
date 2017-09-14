package com.github.seijuro.site.com.tripadvisor;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.site.com.tripadvisor.data.TripAdvisorReviewerProfile;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@ToString
@EqualsAndHashCode
public class TripAdvisorReviewerProfileParser implements HTMLPageParser<TripAdvisorReviewerProfile> {
    @Setter
    private String hotelId;
    @Setter
    private int page;
    @Setter
    private String reviewId;

    /**
     * Construct
     *
     * @param hotelId
     * @param page
     * @param reviewId
     */
    public TripAdvisorReviewerProfileParser(String hotelId, int page, String reviewId) {
        this.hotelId = hotelId;
        this.page = page;
        this.reviewId = reviewId;
    }

    @Override
    public List<TripAdvisorReviewerProfile> parse(String html) {
        List<TripAdvisorReviewerProfile> result = new ArrayList<>();

        try {
            TripAdvisorReviewerProfile.Builder profileBuilder = new TripAdvisorReviewerProfile.Builder();

            profileBuilder.setReviewId(reviewId);

            Document document = Jsoup.parse(html);
            Element bodyElement = document.body();

            Elements userElements = bodyElement.select("div.memberOverlayRedesign");
            if (userElements.size() > 0) {
                Element userElement = userElements.first();

                //  username
                Elements userNameElements = userElement.getElementsByClass("username");
                if (userNameElements.size() > 0) {
                    profileBuilder.setReviewerName(StringUtils.normalizeSpace(userNameElements.first().text()));
                }

                //  badge
                Elements badgeElements = userElement.getElementsByClass("badgeinfo");
                if (badgeElements.size() > 0) {
                    Element badgeElement = badgeElements.first();

                    //  level
                    Elements levelElements = badgeElement.select("span");
                    if (levelElements.size() > 0) {
                        profileBuilder.setLevel(Integer.parseInt(StringUtils.normalizeSpace(levelElements.first().text())));
                    }

                    //  contributor
                    if (badgeElement.text().contains("컨트리뷰터")) {
                        profileBuilder.setContributor(true);
                    }

                    Elements propertyElements = userElement.select("ul.memberdescriptionReviewEnhancements li");
                    for (Element property : propertyElements) {
                        String text = property.text();
                        if (text.contains("회원 가입 시기")) {
                            String[] tokens = text.split(":");

                            assert (tokens.length > 1);
                            profileBuilder.setRegistration(tokens[1]);
                        }

                        if (text.contains("출신")) {
                            String[] tokens = text.split(",", 2);
                            for (String token : tokens) {
                                String trimmed = token.trim();

                                if (trimmed.endsWith("출신")) {
                                    profileBuilder.setCountry(token.substring(0, token.lastIndexOf("출신")).trim());
                                }
                                else if (trimmed.endsWith("세") ||
                                        trimmed.endsWith("세 남성") ||
                                        trimmed.endsWith("세 여성")) {
                                    profileBuilder.setAge(trimmed.substring(0, trimmed.lastIndexOf("세")));

                                    if (trimmed.endsWith("남성")) {
                                        profileBuilder.setSex(TripAdvisorReviewerProfile.Sex.MALE);;
                                    }
                                    else if (trimmed.endsWith("여성")) {
                                        profileBuilder.setSex(TripAdvisorReviewerProfile.Sex.FEMALE);;
                                    }
                                    else {
                                        profileBuilder.setSex(TripAdvisorReviewerProfile.Sex.UNKNOWN);;
                                    }
                                }
                            }
                        }
                    }

                    Elements reviewElements = userElement.select("ul.countsReviewEnhancements li.countsReviewEnhancementsItem");
                    for (Element reviewElement : reviewElements) {
                        Elements reviewPropertyElements = reviewElement.select("span");
                        for (Element reviewProertyElement : reviewPropertyElements) {
                            String trimmed = reviewElement.text().trim();

                            if (reviewProertyElement.className().contains("pencil-paper")) {
                                //  포스팅
                                String numberText = trimmed.substring(0, trimmed.indexOf("개"));
                                int posts = 0;
                                try {
                                    posts = Integer.parseInt(numberText);
                                }
                                catch (NumberFormatException excp) {
                                    //  not specified.
                                }

                                profileBuilder.setNumberOfPosts(posts);
                            }
                            else if (reviewProertyElement.className().contains("globe-world")) {
                                //  방문 도시
                                String numberText = trimmed.substring(0, trimmed.indexOf("개"));
                                int visits = 0;
                                try {
                                    visits = Integer.parseInt(numberText);
                                }
                                catch (NumberFormatException excp) {
                                    //  not specified.
                                }

                                profileBuilder.setNumberOfCityVisited(visits);
                            }
                            else if (reviewProertyElement.className().contains("thumbs-up-fill")) {
                                // 　도움되는 리뷰
                                String numberText = trimmed.substring(0, trimmed.indexOf("건"));
                                int review = 0;
                                try {
                                    review = Integer.parseInt(numberText);
                                }
                                catch (NumberFormatException excp) {
                                    //  not specified.
                                }

                                profileBuilder.setNumberOfCityVisited(review);
                            }
                        }
                    }

                    Elements histogramElements = userElement.select("div.wrap.container.histogramReviewEnhancements ul div.chartRowReviewEnhancements");
                    for (Element histogramElement : histogramElements) {
                        Elements labelElements = histogramElement.select("span.rowLabelReviewEnhancements.rowCellReviewEnhancements");
                        if (labelElements.size() > 0) {
                            Element labelElement = labelElements.first();
                            Elements valueElements = histogramElement.select("span.rowCountReviewEnhancements.rowCellReviewEnhancements");

                            int count = 0;
                            if (valueElements.size() > 0) {
                                try {
                                    count = Integer.parseInt(valueElements.first().text().trim());
                                }
                                catch (NumberFormatException excp) {
                                    //  not specified
                                }
                            }

                            if (labelElement.text().trim().equals("아주좋음")) {
                                profileBuilder.setGuestRating_Star4(count);
                            }
                            else if (labelElement.text().trim().equals("좋음")) {
                                profileBuilder.setGuestRating_Star4(count);
                            }
                            else if (labelElement.text().trim().equals("보통")) {
                                profileBuilder.setGuestRating_Star3(count);
                            }
                            else if (labelElement.text().trim().equals("별로")) {
                                profileBuilder.setGuestRating_Star2(count);
                            }
                            else if (labelElement.text().trim().equals("최악")) {
                                profileBuilder.setGuestRating_Star1(count);
                            }
                        }
                    }
                }
            }

            result.add(profileBuilder.build());
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return result;
    }
}
