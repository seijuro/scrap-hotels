package com.github.seijuro.site.com.hotels.property;

public class RatePlanProperty {
    public static final String Price = "price";
    public static final String Features = "features";
    public static final String Type = "type";

    public static class PriceProperty {
        public static final String Current = "current";
        public static final String ExactCurrent = "exactCurrent";
        public static final String Old = "old";
        public static final String FromPrice = "fromPrice";
    }

    public static class FeaturesProperty {
        public static final String PaymentPreference = "paymentPreference";
        public static final String InstalmentsMessage = "instalmentsMessage";
        public static final String FreeCancellation = "freeCancellation";
    }
}
