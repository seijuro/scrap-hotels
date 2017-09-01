package com.github.seijuro.http.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StatusCodeUtils {
    static final Map<Integer, StatusCode> statusCodes;

    static {
        statusCodes = new HashMap<>();

        for (StatusCode statusCode : StatusCode.StatusCode100.values()) { statusCodes.put(statusCode.getCode(), statusCode); }
        for (StatusCode statusCode : StatusCode.StatusCode200.values()) { statusCodes.put(statusCode.getCode(), statusCode); }
        for (StatusCode statusCode : StatusCode.StatusCode300.values()) { statusCodes.put(statusCode.getCode(), statusCode); }
        for (StatusCode statusCode : StatusCode.StatusCode400.values()) { statusCodes.put(statusCode.getCode(), statusCode); }
        for (StatusCode statusCode : StatusCode.StatusCode500.values()) { statusCodes.put(statusCode.getCode(), statusCode); }
    }

    public static StatusCode get(int code) {
        return statusCodes.get(code);
    }

    public static boolean contains(int code) {
        return statusCodes.containsKey(code);
    }

    public static String getRFC(int code) {
        if (contains(code)) {
            return get(code).getRef();
        }

        return null;
    }

    public static boolean isOK(int code) {
        return isOK(statusCodes.get(code));
    }

    public static boolean isOK(StatusCode code) {
        if (code instanceof StatusCode.StatusCode200) {
            return true;
        }

        return false;
    }

    public static void format(StatusCode code, Consumer<String> consumer) {
        StringBuffer sb = new StringBuffer("HTTP StatusCode {");
        sb.append("name(or reason) : [").append(code.getName()).append("], code : [").append(code.getCode()).append("], ref. : [").append(code.getRef()).append("]}");

        consumer.accept(sb.toString());
    }
}
