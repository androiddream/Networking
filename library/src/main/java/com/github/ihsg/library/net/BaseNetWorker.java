package com.github.ihsg.library.net;

import java.util.Map;

/**
 * Created by hsg on 10/09/2017.
 */

public class BaseNetWorker {

    protected Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, String> headers;
        private String timestampKey;
        private ServerTimestampListener serverTimestampListener;
        private NetLogLevel netLogLevel;
        private long timeout;
        private IBaseUrl baseUrl;

        public Builder setHeaders(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setHeaders(final String timestampKey) {
            this.timestampKey = timestampKey;
            return this;
        }

        public Builder setServerTimestampListener(final ServerTimestampListener serverTimestampListener) {
            this.serverTimestampListener = serverTimestampListener;
            return this;
        }

        public Builder setTimestampKey(final String timestampKey) {
            this.timestampKey = timestampKey;
            return this;
        }

        public Builder setNetLogLevel(final NetLogLevel netLogLevel) {
            this.netLogLevel = netLogLevel;
            return this;
        }

        public Builder setTimeout(final long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setBaseUrl(final IBaseUrl baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public <T> T build(final Class<T> service) {
            final NetParamsInterceptor netParamsInterceptor = new NetParamsInterceptor()
                    .setHeaders(this.headers)
                    .setServerTimestampKey(this.timestampKey)
                    .setServerTimestampListener(this.serverTimestampListener);

            final NetLogInterceptor netLogInterceptor = new NetLogInterceptor()
                    .setLevel(this.netLogLevel);

            final NetClientHelper netClientHelper = new NetClientHelper()
                    .setNetParamsInterceptor(netParamsInterceptor)
                    .setNetLogInterceptor(netLogInterceptor)
                    .setTimeout(this.timeout);

            return new RetrofitHelper(this.baseUrl, netClientHelper)
                    .getRetrofit()
                    .create(service);
        }
    }
}
