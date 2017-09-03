package com.github.seijuro.writer;

import com.github.seijuro.search.SearchURL;

public interface HTMLWriter {
    public abstract boolean write(SearchURL searchURL, String html);
}
