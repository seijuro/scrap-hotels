package com.github.seijuro.http.rest;

import java.io.UnsupportedEncodingException;

public interface IURLEncoder {
    String encode(String url) throws UnsupportedEncodingException;
}
