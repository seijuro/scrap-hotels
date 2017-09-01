package com.github.seijuro.site.com.booking.query;

import lombok.Getter;

public enum Parameter {
    AID("aid"),
    Label("label"),
    SID("sid"),
    UncheckedFilter("unchecked_filter"),
    CheckInYear("checkin_year"),
    CheckInMonth("checkin_month"),
    CheckInMonthDay("checkin_monthday"),
    CheckOutYear("checkout_year"),
    CheckOutMonth("checkout_month"),
    CheckOutMonthDay("checkout_monthday"),
    ClassInterval("class_interval"),
    DestId("dest_id"),
    DestType("dest_type"),
    DTDisc("dtdisc"),
    GroupAdult("group_adults"),
    GroupChildren("group_children"),
    Inac("inac"),
    IndexPostcard("index_postcard"),
    LabelClick("label_click"),
    Map("map"),
    NoRooms("no_rooms"),
    Postcard("postcard"),
    RegSel("reg_sel"),
    SearchSelected("search_selected"),
    RawDestType("raw_dest_type"),
    Room1("room1"),
    SBPriceType("sb_price_type"),
    SRC("src"),
    SRCElement("src_elem"),
    SS("ss"),
    SSAll("ss_all"),
    SSB("ssb"),
    SSHIS("sshis"),
    SSNE("ssne"),
    SSNEUntouched("ssne_untouched"),
    Rows("rows"),
    Offset("offset"),
    SRAjax("sr_ajax"),
    B_GTT("b_gtt"),
    _RESORVED("_"),
    FromSF("from_sf");

    @Getter
    private String property;

    Parameter(String property) {
        this.property = property;
    }
}
