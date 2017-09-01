package com.github.seijuro.snapshot;

public interface AbstractSnapshotWriter {
    public abstract int write(SnapshotRequest request);
}
