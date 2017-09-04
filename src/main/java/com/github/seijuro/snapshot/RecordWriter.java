package com.github.seijuro.snapshot;

import com.github.seijuro.site.com.hotels.result.Result;

import java.io.Closeable;

public interface RecordWriter<T> extends Closeable {
    public abstract void write(T[] result, String domain, String query, String checkIn, String checkOut, String type, String sort, int fromRank);
}
