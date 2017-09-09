package com.github.seijuro.site.common.provider;

import java.util.List;

public class StringProvider extends Provider<String> {
    public StringProvider(String... elements) {
        super(elements);
    }

    public StringProvider(List<String> elements) {
        super(elements);
    }
}
