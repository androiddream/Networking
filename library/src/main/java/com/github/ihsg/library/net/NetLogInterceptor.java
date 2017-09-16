package com.github.ihsg.library.net;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSource;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;

/**
 * Created by hsg on 10/09/2017.
 */
class NetLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile NetLogLevel level = NetLogLevel.NONE;
    private final Logger logger;

    public interface Logger {
        void log(String message);

        void json(String message);

        void xml(String message);

        NetLogInterceptor.Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
//                LogUtil.i(message);
            }

            @Override
            public void json(String message) {
//                LogUtil.json(message);
            }

            @Override
            public void xml(String message) {
//                LogUtil.xml(message);
            }
        };
    }

    public NetLogInterceptor() {
        this.logger = Logger.DEFAULT;
    }

    public NetLogInterceptor setLevel(final NetLogLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        NetLogLevel level = this.level;

        //request
        Request request = chain.request();
        if (level == NetLogLevel.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == NetLogLevel.BODY;
        boolean logHeaders = logBody || level == NetLogLevel.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        logger.log(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    logger.log("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    logger.log(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                logger.log("");
                if (isPlaintext(buffer)) {
                    logger.log(buffer.readString(charset));
                    logger.log("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    logger.log("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logger.log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        logger.log("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                logger.log(headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpEngine.hasBody(response)) {
                logger.log("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        logger.log("");
                        logger.log("Couldn't decode the response body; charset is likely malformed.");
                        logger.log("<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    logger.log("");
                    logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    logger.log("");
                    final String content = buffer.clone().readString(charset);
                    if (contentType != null) {
                        if (("json").equalsIgnoreCase(contentType.subtype())) {
                            logger.json(content);
                        } else if (("xml").equalsIgnoreCase(contentType.subtype())) {
                            logger.xml(content);
                        } else {
                            logger.log(content);
                        }
                    }
                }

                logger.log("<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                if (Character.isISOControl(prefix.readUtf8CodePoint())) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static class HttpEngine {
        public static boolean hasBody(Response response) {
            // HEAD requests never yield a body regardless of the response headers.
            if (response.request().method().equals("HEAD")) {
                return false;
            }

            int responseCode = response.code();
            if ((responseCode < StatusLine.HTTP_CONTINUE || responseCode >= 200)
                    && responseCode != HTTP_NO_CONTENT
                    && responseCode != HTTP_NOT_MODIFIED) {
                return true;
            }

            // If the Content-Length or Transfer-Encoding headers disagree with the
            // response code, the response is malformed. For best compatibility, we
            // honor the headers.
            if (OkHeaders.contentLength(response) != -1
                    || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
                return true;
            }

            return false;
        }

        static class OkHeaders {
            public static long contentLength(Response response) {
                return contentLength(response.headers());
            }

            public static long contentLength(Headers headers) {
                return stringToLong(headers.get("Content-Length"));
            }

            private static long stringToLong(String s) {
                if (s == null) return -1;
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
    }
}
