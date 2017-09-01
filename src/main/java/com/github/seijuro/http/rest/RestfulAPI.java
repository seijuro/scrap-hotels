package com.github.seijuro.http.rest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

public abstract class RestfulAPI {
    //  Logger
    final Logger log = LoggerFactory.getLogger(RestfulAPI.class);

    @Getter
    public static final int DefaultReadTimeout = (int)(30 * DateUtils.MILLIS_PER_SECOND);

    /**
     * RequestMethod
     */
    public enum RequestMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private final String method;

        RequestMethod(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return this.method;
        }
    }

    /**
     * Instance Properties
     */
    private final Properties requestProperties = new Properties();
    @Getter(AccessLevel.PUBLIC)
    private final RequestMethod requestMethod;
    @Getter(AccessLevel.PUBLIC)
    private final String requestURL;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PUBLIC)
    private Properties properties;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private IURLEncoder encodeFunc;
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private int readTimeout = getDefaultReadTimeout();

    /**
     * C'tor
     *
     * @param method
     * @param url
     */
    public RestfulAPI(RequestMethod method, String url) {
        this.requestMethod = method;
        this.requestURL = url;
    }

    public RestfulAPIResponse request() throws Exception {
        if (requestMethod == RequestMethod.GET) {
            return requestGET();
        }

        return new RestfulAPIErrorResponse("Not implemented yet.");
    }

    public void setRequestProperty(String key, String value) {
        requestProperties.setProperty(key, value);
    }

    /**
     * request using 'GET' method
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    protected RestfulAPIResponse requestGET() throws UnsupportedEncodingException {
        String response = "";
        int responseCode = -1;

        try {
            String urlText = createRequestGETURL();

            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeout);

            for (Object key : requestProperties.keySet()) {
                String propertyKey = (String)key;
                String propertyValue = requestProperties.getProperty(propertyKey);
                conn.setRequestProperty((String)key, propertyValue);
            }

            conn.setRequestMethod(this.requestMethod.toString());

            //  Log
            log.debug("request -> method : {}, read-timeout : {}, property : {}", requestMethod.toString(), readTimeout, requestProperties.toString());

            String line;
            StringBuffer responseBuffer = new StringBuffer();
            responseCode = conn.getResponseCode();

            String contentEncoding = conn.getHeaderField("Content-Encoding");
            BufferedReader br = null;

            if (Objects.nonNull(contentEncoding) &&
                    contentEncoding.equals("gzip")) {
                br = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
            }
            else {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            //  Log
            log.debug("http response -> url : {}, code (status code) : {}", urlText, responseCode);

            while ((line = br.readLine()) != null) {
                responseBuffer.append(line);
            }

            response = responseBuffer.toString();

            br.close();
            conn.disconnect();
        }
        catch (Exception excp) {
            return new RestfulAPIErrorResponse(responseCode, response.toString(), excp.getMessage());
        }

        return createResponse(responseCode, response);
    }

    protected String createRequestGETURL() throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer(this.requestURL);

        if (Objects.nonNull(properties)) {
            Enumeration e = properties.keys();
            boolean isFirst = true;

            while (e.hasMoreElements()) {
                String key = (String) (e.nextElement());
                String value = this.properties.getProperty(key);

                if (isFirst) {
                    isFirst = false;
                    sb.append("?");
                } else {
                    sb.append("&");
                }

                sb.append(this.encodeFunc.encode(key)).append("=").append(this.encodeFunc.encode(value));
            }
        }

        return sb.toString();
    }


    protected RestfulAPIResponse createResponse(int code, String reponse) {
        StatusCode statusCode = StatusCodeUtils.get(code);
        if (StatusCodeUtils.isOK(statusCode)) {
            return new RestfulAPIResponse(code, reponse);
        }

        StringBuffer sb = new StringBuffer();
        StatusCodeUtils.format(statusCode, sb::append);
        String message = sb.toString();

        return new RestfulAPIErrorResponse(statusCode.getCode(), reponse, message);
    }
}
