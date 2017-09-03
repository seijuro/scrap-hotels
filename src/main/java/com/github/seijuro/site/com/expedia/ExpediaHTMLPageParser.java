package com.github.seijuro.site.com.expedia;

import com.github.seijuro.parser.HTMLPageParser;
import com.github.seijuro.writer.CSVFileWriter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExpediaHTMLPageParser implements HTMLPageParser {
    @Setter
    private CSVFileWriter writer = null;

    @Override
    public void parse(String html) {
        Document pageDocument = Jsoup.parse(html);
        Elements articles = pageDocument.select("article[data-hotelid]");

        for (Element article : articles) {
            Elements hotelNameElements = article.select("h4.hotelName.fakeLink");
            Elements startRatingElements = article.select("span.ratingText");

                    String hotelId = article.attr("data-hotelid");
            String hotelName = hotelNameElements.size() > 0 ? hotelNameElements.get(0).text() : StringUtils.EMPTY;
            String startRating = startRatingElements.size() > 0 ? startRatingElements.get(0).text() : StringUtils.EMPTY;


        }
    }

    //  https://www.expedia.co.kr/Hotel-Search?destination=%EB%B0%9C%EB%A6%AC%2C+%EC%9D%B8%EB%8F%84%EB%84%A4%EC%8B%9C%EC%95%84&latLong=-8.409518%2C115.188916&regionId=602651&startDate=2017.10.15&endDate=2017.10.16&rooms=1&_xpid=11905%7C1&adults=2
}
