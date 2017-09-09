package com.github.seijuro.writer;

import com.github.seijuro.search.SearchURL;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public interface HTMLWriter {
    public abstract boolean write(String searchURL, String html, Object... options);

    default boolean write(SearchURL searchURL, String html, Object... options) {
        return write(Objects.nonNull(searchURL) ? searchURL.toURL() : null, html, options);
    }

    default boolean write(String html, Object... options) {
        return write(StringUtils.EMPTY, html, options);
    }
}
