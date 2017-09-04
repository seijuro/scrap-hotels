package com.github.seijuro.site.com.agoda;

import com.github.seijuro.search.query.Date;
import com.github.seijuro.search.query.Destination;
import com.github.seijuro.search.query.Lodging;
import com.github.seijuro.search.query.Sort;
import com.github.seijuro.site.com.agoda.query.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchURL implements com.github.seijuro.search.SearchURL {
    @Getter
    public static String BaseURL = "https://www.agoda.com";
    @Getter
    public static String SearchResultSubURL = "/ko-kr/pages/agoda/default/DestinationSearchResult.aspx";

    /**
     * Instance Properties
     */
    @Getter
    private final Destination destination;
    @Getter
    private final Sort sort;
    @Getter
    private int page;
    private final CheckIn checkIn;
    private final CheckOut checkOut;
    private final Adults adults;
    private final Children children;
    @Getter
    private final List<Lodging> lodgings;
    private final Currency currency;

    private int los;
    private int rooms;
    private int cid;
    private int aid;
    private int languageId;
    private int storefrontId;
    private int hotelReviewScore;
    private int pageTypeId;
    private String ckuid;
    private String trafficType;
    private String htmlLanguage;
    private String cultureInfoName;
    private String userId;
    private String origin;
    private String tag;
    private String gclid;
    private String sessionId;

    public enum RequestParameter {
        CITY("city"),
        CHECK_IN("checkIn"),
        CHECK_OUT("checkOut"),
        LOS("los"),
        ROOMS("rooms"),
        ADULTS("adults"),
        CHILDREN("children"),
        CID("cid"),
        PAGE_TYPE_ID("pagetypeid"),
        ORIGIN("origin"),
        TAG("tag"),
        GCLID("gclid"),
        AID("aid"),
        USER_ID("userId"),
        LANGUAGE_ID("languageId"),
        SESSION_ID("sessionId"),
        STORE_FRONT_ID("storefrontId"),
        CURRENCY_CODE("currencyCode"),
        HTML_LANGUAGE("htmlLanguage"),
        TRAFFIC_TYPE("trafficType"),
        CURTURE_INFO_NAME("cultureInfoName"),
        CHILD_AGES("childages"),
        PRICE_CURRENCY("priceCur"),
        HOTEL_REVIEW_SCORE("hotelReviewScore"),
        CKUID("ckuid"),
        LODGING("hotelAccom"),
        SORT("sort");

        @Getter
        private final String parameter;

        RequestParameter(String param) {
            parameter = param;
        }
    }

    /**
     * C'tor
     *
     * @param builder
     */
    protected SearchURL(Builder builder) {
        destination = builder.destination;
        sort = builder.sort;
        checkIn = builder.checkIn;
        checkOut = builder.checkOut;
        adults = builder.adults;
        children = builder.children;
        lodgings = builder.lodgings;
        currency = builder.currency;

        los = builder.los;
        rooms = builder.rooms;
        cid = builder.cid;
        aid = builder.aid;
        tag = builder.tag;
        pageTypeId = builder.pageTypeId;
        languageId = builder.languageId;
        storefrontId = builder.storefrontId;
        hotelReviewScore = builder.hotelReviewScore;
        ckuid = builder.ckuid;
        trafficType = builder.trafficType;
        htmlLanguage = builder.htmlLanguage;
        cultureInfoName = builder.cultureInfoName;
        userId = builder.userId;
        origin = builder.origin;
        gclid = builder.gclid;
        sessionId = builder.sessionId;
        page = builder.page;
    }

    @Override
    public String toURL() {
        StringBuffer urlBuilder = new StringBuffer(getBaseURL());
        urlBuilder.append(getSearchResultSubURL());

        urlBuilder
                .append("?").append(RequestParameter.CITY.getParameter()).append("=").append(destination.getQueryParameter())
                .append("&").append(RequestParameter.CHECK_IN.getParameter()).append("=").append(checkIn.getQueryParameter())
                .append("&").append(RequestParameter.LOS.getParameter()).append("=").append(los)
                .append("&").append(RequestParameter.ROOMS.getParameter()).append("=").append(rooms)
                .append("&").append(RequestParameter.ADULTS.getParameter()).append("=").append(adults.getQueryParameter())
                .append("&").append(RequestParameter.CHILDREN.getParameter()).append("=").append(children.count())
                .append("&").append(RequestParameter.CID.getParameter()).append("=").append(cid)
                .append("&").append(RequestParameter.PAGE_TYPE_ID.getParameter()).append("=").append(pageTypeId)
                .append("&").append(RequestParameter.ORIGIN.getParameter()).append("=").append(origin)
                .append("&").append(RequestParameter.TAG.getParameter()).append("=").append(tag)
                .append("&").append(RequestParameter.GCLID.getParameter()).append("=").append(gclid)
                .append("&").append(RequestParameter.AID.getParameter()).append("=").append(aid)
                .append("&").append(RequestParameter.USER_ID.getParameter()).append("=").append(userId)
                .append("&").append(RequestParameter.LANGUAGE_ID.getParameter()).append("=").append(languageId)
                .append("&").append(RequestParameter.SESSION_ID.getParameter()).append("=").append(sessionId)
                .append("&").append(RequestParameter.STORE_FRONT_ID.getParameter()).append("=").append(storefrontId)
                .append("&").append(RequestParameter.CURRENCY_CODE.getParameter()).append("=").append(currency.getQueryParameter())
                .append("&").append(RequestParameter.HTML_LANGUAGE.getParameter()).append("=").append(htmlLanguage)
                .append("&").append(RequestParameter.TRAFFIC_TYPE.getParameter()).append("=").append(trafficType)
                .append("&").append(RequestParameter.CURTURE_INFO_NAME.getParameter()).append("=").append(cultureInfoName)
                .append("&").append(RequestParameter.CHECK_OUT.getParameter()).append("=").append(checkOut.getQueryParameter())
                .append("&").append(RequestParameter.CHILD_AGES.getParameter()).append("=").append(children.getQueryParameter())
                .append("&").append(RequestParameter.PRICE_CURRENCY.getParameter()).append("=").append(currency.getQueryParameter())
                .append("&").append(RequestParameter.HOTEL_REVIEW_SCORE.getParameter()).append("=").append(hotelReviewScore)
                .append("&").append(RequestParameter.CKUID.getParameter()).append("=").append(ckuid);

        if (Objects.nonNull(lodgings) &&
                lodgings.size() > 0) {
            urlBuilder.append("&").append(RequestParameter.LODGING.getParameter()).append("=").append(lodgings.get(0).getQueryParameter());

            for (int index = 1; index < lodgings.size(); ++index) {
                urlBuilder.append(",").append(lodgings.get(index).getQueryParameter());
            }
        }

        if (Objects.nonNull(sort) &&
                sort != com.github.seijuro.site.com.agoda.query.Sort.RECOMMEND) {
            urlBuilder.append("&").append(RequestParameter.SORT.getParameter()).append("=").append(sort.getQueryParameter());
        }

        return urlBuilder.toString();
    }

    @Override
    public Date getStartDate() {
        return checkIn;
    }

    @Override
    public Date getEndDate() {
        return checkOut;
    }

    public static class Builder {
        @Setter
        private Destination destination = null;
        @Setter
        private Sort sort = null;
        @Setter
        private int los = 1;
        @Setter
        private int rooms = /* default */1;
        private int cid = /* defulat */171976;
        private int aid = /* default */81837;
        private int languageId = /* default */9;
        private int storefrontId = /* default */3;
        private int hotelReviewScore = /* default */5;
        private int pageTypeId = 1;
        private String tag = StringUtils.EMPTY;
        private String ckuid = "e53cf1ff-bf82-4911-b0e4-84ec8c7ab28c";
        private String trafficType = "User";
        private String htmlLanguage = "ko-kr";
        private String cultureInfoName = "ko-KR";
        private String userId = "e53cf1ff-bf82-4911-b0e4-84ec8c7ab28c";
        private String origin = "KR";
        private String gclid = StringUtils.EMPTY;
        private String sessionId = "korh021hup5ppfxthujlh0eu";
        @Setter
        private CheckIn checkIn = null;
        @Setter
        private CheckOut checkOut = null;
        private Adults adults = new Adults(2);
        @Setter
        private Currency currency = Currency.KRW;
        private Children children = new Children(Collections.emptyList());
        @Setter
        private List<Lodging> lodgings = new ArrayList<>();
        @Setter
        private int page = 1;

        public void setCheckIn(int year, int month, int day) {
            checkIn = new CheckIn(year, month, day);
        }

        public void setCheckOut(int year, int month, int day) {
            checkOut = new CheckOut(year, month, day);
        }

        public void setChildren(List<Integer> ages) {
            children = new Children(ages);
        }

        /**
         * Builder Pattern Method
         *
         * @return
         */
        public SearchURL build() {
            return new SearchURL(this);
        }
    }
}
