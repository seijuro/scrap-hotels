package com.github.seijuro.snapshot;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

public class SnapshotResult extends SnapshotRequest {
    /**
     * Instance Properties
     */
    @Getter
    private final Long idx;
    @Getter
    private final Timestamp lastUpdate;

    /**
     * Construct snapshot key.
     *
     * @param builder
     */
    protected SnapshotResult(Builder builder) {
        super(builder);

        idx = builder.idx;
        lastUpdate = builder.lastUpdate;
    }

    public static class Builder extends SnapshotRequest.Builder {
        @Setter
        private Long idx = Long.MIN_VALUE;
        @Setter
        private Timestamp lastUpdate;

        /**
         * Build <code>SnapshotKey</code> instance.
         *
         * @return
         */
        public SnapshotResult build() {
            return new SnapshotResult(this);
        }
    }
}
