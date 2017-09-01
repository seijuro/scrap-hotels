package com.github.seijuro.http.rest;

import lombok.AccessLevel;
import lombok.Getter;

public class RequestProperty {
    public static class UserAgent {
        @Getter(AccessLevel.PUBLIC)
        static final String propertyName = "User-Agent";

        public static final String MOZILA_5_0 = "Mozila/5.0";
    }

    public static class AcceptLanguage {
        @Getter(AccessLevel.PUBLIC)
        static final String propertyName = "Accept-Language";

        public static final String EN_US = "en-US,en;q=0.5";
    }

    public static class ContentType {
        @Getter(AccessLevel.PUBLIC)
        static final String propertyName = "Content-Type";

        public static class Text {
            public static final String PLAIN = "text/plain";
            public static final String HTML = "text/html";
            public static final String CSS = "text/css";
            public static final String JS = "text/javascript";
        }

        public static class Image {
            public static final String GIF = "image/gif";
            public static final String PNG = "image/png";
            public static final String JPEG = "image/jpeg";
            public static final String BMP = "image/bmp";
            public static final String WEBP = "image/webp";
        }

        public static class Audio {
            public static final String MIDI = "audio/midi";
            public static final String MPEG = "audio/mpeg";
            public static final String WEBM = "audio/webm";
            public static final String OGG = "audio/ogg";
            public static final String WAVE = "audio/wave";
            public static final String WAV = "audio/wav";
            public static final String X_WAV = "audio/x-wav";
            public static final String X_PN_WAV = "audio/x-pn-wav";
        }

        public static class Video {
            public static final String WEBM = "video/webm";
            public static final String OGG = "video/ogg";
        }

        public static class Application {
            public static final String OCTET_STREAM = "application/octet-stream";
            public static final String PKCS12 = "application/pkcs12";
            public static final String VND_MSPPT = "application/vnd.mspowerpoint";
            public static final String XHTML_XML = "application/xhtml+xml";
            public static final String XML = "application/xml";
            public static final String JSON = "application/json";
            public static final String PDF = "application/pdf";
        }
    }
}
