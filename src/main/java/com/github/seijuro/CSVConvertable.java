package com.github.seijuro;

public interface CSVConvertable {
    public abstract String recordSeperator();
    public abstract String columnSeperator();
    public abstract String toCSV();
}
