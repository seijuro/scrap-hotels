package com.github.seijuro.snapshot;

public interface AbstractSnapshotReader {
    public abstract SnapshotResult read(SnapshotRequest key);
}
