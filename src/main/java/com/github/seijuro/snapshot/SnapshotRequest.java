package com.github.seijuro.snapshot;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SnapshotRequest {
    @Getter
    private final int year;
    @Getter
    private final int revision;
    @Getter
    private final String url;
    @Getter
    private final String param1;
    @Getter
    private final String param2;
    @Getter
    private final String param3;
    @Getter
    private final String param4;
    @Getter
    private final String param5;
    @Getter
    private final String response;
    @Getter
    private final String result;

    /**
     * Construct snapshot key.
     *
     * @param builder
     */
    protected SnapshotRequest(Builder builder) {
        year = builder.year;
        url = builder.url;
        revision = builder.revision;
        param1 = builder.param1;
        param2 = builder.param2;
        param3 = builder.param3;
        param4 = builder.param4;
        param5 = builder.param5;
        response = builder.response;
        result = builder.result;
    }

    /**
     * Builder Pattern class
     */
    public static class Builder {
        @Setter
        private int year = Integer.MIN_VALUE;
        @Setter
        private int revision = Integer.MIN_VALUE;
        @Setter
        private String url = null;
        @Setter
        private String param1 = null;
        @Setter
        private String param2 = null;
        @Setter
        private String param3 = null;
        @Setter
        private String param4 = null;
        @Setter
        private String param5 = null;
        @Setter
        private String response = null;
        @Setter
        private String result = null;

        /**
         * Build <code>SnapshotKey</code> instance.
         *
         * @return
         */
        public SnapshotRequest build() {
            return new SnapshotRequest(this);
        }
    }
}
