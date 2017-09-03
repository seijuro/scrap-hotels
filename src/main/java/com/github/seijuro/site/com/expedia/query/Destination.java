package com.github.seijuro.site.com.expedia.query;

import lombok.Getter;

import java.net.URLEncoder;

public enum Destination implements com.github.seijuro.search.query.Destination {
    SEOUL("178308", "서울", "서울(및+인근+지역),+한국"),
    JEJU("6049718", "제주", "제주도,+한국"),
    BALLY("602651", "발리", "발리,+인도네시아"),
    HAWAI("213", "하와이", "하와이,+미국"),
    LAHAINA("8499", "라하이나,하와이,미국", "라하이나"),
    KIHEI("9304", "키헤이,하와이,미국", "키헤이"),
    HONOLULU("1488", "호놀룰루,하와이,미국", "호놀룰루"),
    PRINCEVILLE("1509", "프린스빌,하와이,미국", "프린스빌"),
    KAILUA("1879", "카일루아 코나,하와이,미국", "카일루아"),
    KOLOA("7675", "콜로아,하와이,미국", "콜로아"),
    KAPAA("8213", "카파,하와이,미국", "카파"),
    WAIKOLOA("183158", "와이콜로아,하와이,미국", "와이콜로아"),
    WAILEA("7739", "와일레아,하와이,미국", "와일레아"),
    KAPOLEI("8341", "카폴레이,하와이,미국", "카폴레이"),
    HILO("1643", "힐로,하와이,미국", "힐로"),
    LIHUE("2074", "리후에,하와이,미국", "리후에");

    @Getter
    private final String id;
    @Getter
    private final String label;
    @Getter
    private final String destination;

    @Override
    public String getQueryParameter() {
        try {
            return URLEncoder.encode(destination, "UTF-8");
        }
        catch (Exception excp) {
            excp.printStackTrace();
        }

        return destination;
    }

    /**
     * Construct
     *
     * @param id
     * @param label
     * @param param
     */
    Destination(String id, String label, String param) {
        this.id = id;
        this.label = label;
        this.destination = param;
    }
}
