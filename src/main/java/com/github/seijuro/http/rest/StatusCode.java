package com.github.seijuro.http.rest;

import lombok.AccessLevel;
import lombok.Getter;

public interface StatusCode {
    public abstract int getCode();
    public abstract String getName();
    public abstract String getRef();

    /**
     * StatusCode : 100 - 199
     */
    enum StatusCode100 implements StatusCode {
        CONTINUE(100, "RFC7231, Section 6.2.1"),
        SWITCHING_PROTOCOLS(101, "RFC7231, Section 6.2.2"),
        PROCESSING(102, "RFC2518");

        /**
         * Instance Properties
         */
        @Getter(AccessLevel.PUBLIC)
        private final int code;
        @Getter(AccessLevel.PUBLIC)
        private final String ref;

        public String getName() { return name(); }

        /**
         * C'tor
         *
         * @param $code
         * @param $ref
         */
        StatusCode100(int $code, String $ref) {
            this.code = $code;
            this.ref = $ref;
        }
    }

    /**
     * StatusCode : 200 - 299
     */
    enum StatusCode200 implements StatusCode {
        OK(200, "RFC7231, Section 6.3.1"),
        CREATED(201, "RFC7231, Section 6.3.2"),
        ACCEPTED(202, "RFC7231, Section 6.3.3"),
        NON_AUTHORITATIVE_INFORMATION(203, "RFC7231, Section 6.3.4"),
        NO_CONTENT(204, "RFC7231, Section 6.3.5"),
        RESET_CONTENT(205, "RFC7231, Section 6.3.6"),
        PARTIAL_CONTENT(206, "RFC7233, Section 4.1"),
        MULTI_STATUS(207, "RFC4918"),
        ALREADY_REPORTED(208, "RFC5842"),
        IM_USED(226, "RFC3229");

        /**
         * Instance Properties
         */
        @Getter(AccessLevel.PUBLIC)
        private final int code;
        @Getter(AccessLevel.PUBLIC)
        private final String ref;

        public String getName() { return name(); }

        /**
         * C'tor
         *
         * @param $code
         * @param $ref
         */
        StatusCode200(int $code, String $ref) {
            this.code = $code;
            this.ref = $ref;
        }
    }

    /**
     * StatusCode 300 - 399
     */
    enum StatusCode300 implements StatusCode {
        MULTIPLE_CHOICE(300, "RFC7231, Section 6.4.1"),
        MOVED_PERMANETLY(301, "RFC7231, Section 6.4.2"),
        FOUND(302, "RFC7231, Section 6.4.3"),
        SEE_OTHER(303, "RFC7231, Section 6.4.4"),
        NOT_MOFIED(304, "RFC7232, Section 4.1"),
        USE_PROXY(305, "RFC7231, Section 6.4.5"),
        TEMPORARY_REDIRECT(307, "[RFC7231, Section 6.4.7"),
        PERMANENT_REDIRECT(308, "RFC7538");

        /**
         * Instance Properties
         */
        @Getter(AccessLevel.PUBLIC)
        private final int code;
        @Getter(AccessLevel.PUBLIC)
        private final String ref;

        public String getName() { return name(); }

        /**
         * C'tor
         *
         * @param $code
         * @param $ref
         */
        StatusCode300(int $code, String $ref) {
            this.code = $code;
            this.ref = $ref;
        }
    }

    /**
     * StatusCode 400 - 499
     */
    enum StatusCode400 implements StatusCode {
        BAD_REQUEST(400, "RFC7231, Section 6.5.1"),
        UNAUTHORIZED(401, "RFC7235, Section 3.1"),
        PAYMENT_REQUIRED(402, "RFC7231, Section 6.5.2"),
        FORBIDDEN(403, "RFC7231, Section 6.5.3"),
        NOT_FOUND(404, "RFC7231, Section 6.5.4"),
        METHOD_NOT_ALLOWED(405, "RFC7231, Section 6.5.5"),
        NOT_ACCEPTABLE(406, "RFC7231, Section 6.5.6"),
        PROXY_AUTHENTICATION_REQUIRED(407, "FC7235, Section 3.2"),
        REQUEST_TIMEOUT(408, "RFC7231, Section 6.5.7"),
        CONFLICT(409, "RFC7231, Section 6.5.8"),
        GONE(410, "RFC7231, Section 6.5.9"),
        LENGTH_REQUIRED(411, "RFC7231, Section 6.5.10"),
        PRECONDITION_FAILED(412, "RFC7232, Section 4.2, RFC8144, Section 3.2"),
        PAYLOAD_TOO_LARGE(413, "RFC7231, Section 6.5.11"),
        URI_TOO_LONG(414, "RFC7231, Section 6.5.12"),
        UNSUPPORTED_MEDIA_TYPE(415, "RFC7231, Section 6.5.13, RFC7694, Section 3"),
        RANGE_NOT_SATISFIABLE(416, "RFC7233, Section 4.4"),
        EXPECTATION_FAILED(417, "RFC7231, Section 6.5.14"),

        MISDIRECTED_REQUEST(421, "RFC7540, Section 9.1.2"),
        UNPROCESSABLE_ENTITY(422, "RFC4918"),
        LOCKED(423, "RFC4918"),
        FAILED_DEPENDENCY(424, "RFC4918"),
        UPGRADE_REQUIRED(426, "RFC7231, Section 6.5.15"),
        PRECONDITION_REQUIRED(428, "RFC6585"),
        TOO_MANY_REQUEST(429, "RFC6585"),
        REQUEST_HANDLER_FIELDS_TOO_LARGE(431, "RFC6585"),

        UNAVAILABLE_FOR_LEGAL_REASONS(451, "RFC7725");

        /**
         * Instance Properties
         */
        @Getter(AccessLevel.PUBLIC)
        private final int code;
        @Getter(AccessLevel.PUBLIC)
        private final String ref;

        public String getName() { return name(); }

        /**
         * C'tor
         *
         * @param $code
         * @param $ref
         */
        StatusCode400(int $code, String $ref) {
            this.code = $code;
            this.ref = $ref;
        }
    }

    /**
     * StatusCode 500 - 599
     */
    enum StatusCode500 implements StatusCode {
        INTERNAL_SERVER_ERROR(500, "RFC7231, Section 6.6.1"),
        NOT_IMPLEMENTED(501, "RFC7231, Section 6.6.2"),
        BAD_GATEWAY(502, "RFC7231, Section 6.6.3"),
        SERVICE_UNAVAILABLE(503, "RFC7231, Section 6.6.4"),
        GATEWAY_TIMEOUT(504, "RFC7231, Section 6.6.5"),
        HTTP_VERSION_NOT_SUPPORTED(505, "RFC7231, Section 6.6.6"),
        VARIANT_ALSO_NEGOTIATES(506, "RFC2295"),
        INSUFFICIENT_STORAGE(507, "RFC4918"),
        LOOP_DETECTED(508, "RFC5842"),
        NOT_EXTENDED(510, "RFC2774"),
        NETWORK_AUTHENTICATION_REQUIRED(511, "RFC6585");

        /**
         * Instance Properties
         */
        @Getter(AccessLevel.PUBLIC)
        private final int code;
        @Getter(AccessLevel.PUBLIC)
        private final String ref;

        public String getName() { return name(); }

        /**
         * C'tor
         *
         * @param $code
         * @param $ref
         */
        StatusCode500(int $code, String $ref) {
            this.code = $code;
            this.ref = $ref;
        }
    }
}
