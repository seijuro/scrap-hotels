package com.github.seijuro.site.common.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Provider<T> {
    int next = 0;
    private final List<T> elements = new ArrayList<>();

    public Provider(List<T> elements) {
        this.elements.addAll(elements);
    }

    public Provider(T... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    synchronized public T getNext() {
        if (next < elements.size()) {
            return elements.get(next++);
        }

        return null;
    }

    synchronized public void append(T element) {
        elements.add(element);
    }
}
