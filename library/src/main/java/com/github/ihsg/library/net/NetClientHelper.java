package com.github.ihsg.library.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by hsg on 10/09/2017.
 */
class NetClientHelper {
    //minimum timtout is 10 seconds
    private final int MIN_TIMEOUT = 10;

    private long timeout;
    private CookieJar cookieJar;
    private NetLogInterceptor netLogInterceptor;
    private NetParamsInterceptor netParamsInterceptor;

    public NetClientHelper() {
        initCookieJar();
    }

    public NetClientHelper setTimeout(final long timeout) {
        this.timeout = timeout;
        return this;
    }

    public NetClientHelper setNetParamsInterceptor(NetParamsInterceptor netParamsInterceptor) {
        this.netParamsInterceptor = netParamsInterceptor;
        return this;
    }

    public NetClientHelper setNetLogInterceptor(NetLogInterceptor netLogInterceptor) {
        this.netLogInterceptor = netLogInterceptor;
        return this;
    }

    public OkHttpClient getNetClient() {
        //1. get builder & add common setting
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //2. set cookie
        if (this.cookieJar != null) {
            builder.cookieJar(this.cookieJar);
        }

        //3. add params interceptor
        if (this.netParamsInterceptor != null) {
            builder.addInterceptor(netParamsInterceptor);
        }

        //4. add log interceptor
        if (this.netLogInterceptor != null) {
//            builder.addNetworkInterceptor(this.netLogInterceptor);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(logging);

        }

        return builder.retryOnConnectionFailure(false)
                .connectTimeout(getTimeout(), TimeUnit.SECONDS)
                .readTimeout(getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(getTimeout(), TimeUnit.SECONDS)
                .build();
    }

    private void initCookieJar() {
        this.cookieJar = new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new LinkedHashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return (cookies != null) ? cookies : new ArrayList<Cookie>();
            }
        };
    }

    private long getTimeout() {
        return (this.timeout < MIN_TIMEOUT) ? MIN_TIMEOUT : this.timeout;
    }
}
