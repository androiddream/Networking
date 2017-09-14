package com.github.ihsg.library.net;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hsg on 10/09/2017.
 */

class NetParamsInterceptor implements Interceptor {
    private Map<String, String> headers;
    private String serverTimestampKey;
    private ServerTimestampListener serverTimestampListener;

    public NetParamsInterceptor setHeaders(final Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public NetParamsInterceptor setServerTimestampKey(final String serverTimestampKey) {
        this.serverTimestampKey = serverTimestampKey;
        return this;
    }

    public NetParamsInterceptor setServerTimestampListener(ServerTimestampListener serverTimestampListener) {
        this.serverTimestampListener = serverTimestampListener;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder newBuilder = originalRequest.newBuilder();

        //add headers
        if ((this.headers != null) && !this.headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = this.headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                newBuilder.addHeader(item.getKey(), item.getValue());
            }
        }

        //get server timestamp from response
        Response response = chain.proceed(newBuilder.build());
        final String sts = response.headers().get(this.serverTimestampKey);
        if ((sts != null) && !sts.isEmpty() && (this.serverTimestampListener != null)) {
            this.serverTimestampListener.onResponse(sts);
        }

        return response;
    }
}
