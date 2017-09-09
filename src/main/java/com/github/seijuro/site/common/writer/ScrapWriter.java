package com.github.seijuro.site.common.writer;

public interface ScrapWriter {
    public abstract boolean write(String requestURL, String html, String... options);
}
