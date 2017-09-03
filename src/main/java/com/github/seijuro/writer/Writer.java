package com.github.seijuro.writer;

import java.io.IOException;

public interface Writer {
    public abstract void write(String text) throws IOException;
}
