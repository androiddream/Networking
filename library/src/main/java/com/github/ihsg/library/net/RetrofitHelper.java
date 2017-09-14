package com.github.ihsg.library.net;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hsg on 10/09/2017.
 */

class RetrofitHelper {

    private Retrofit retrofit;

    public RetrofitHelper(IBaseUrl baseUrl, NetClientHelper netClientHelper) {
        if ((baseUrl != null) && (netClientHelper != null)) {
            this.retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl.getRestBaseUrl())
                    .client(netClientHelper.getNetClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public Retrofit getRetrofit() {
        return this.retrofit;
    }

    /**
     * handle the exception: end of input at line 1 column 1 path $
     * when api response the empty body with 0-bytes, just like An empty pojo is {} in JSON.
     * reference from: https://github.com/square/retrofit/issues/1554
     */
    private class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) {
                        return null;
                    }
                    return delegate.convert(body);
                }
            };
        }
    }
}
