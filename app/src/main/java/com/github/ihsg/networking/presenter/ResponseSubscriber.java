package com.github.ihsg.networking.presenter;

import android.util.Log;

import com.github.ihsg.library.presenter.BaseResponseSubscriber;
import com.github.ihsg.library.view.ILoadingView;

/**
 * Created by hsg on 13/09/2017.
 */

public abstract class ResponseSubscriber<T> extends BaseResponseSubscriber<T> {
    public ResponseSubscriber(ILoadingView loadingView) {
        super(loadingView);
    }

    public ResponseSubscriber(ILoadingView loadingView, String message) {
        super(loadingView, message);
    }

    public ResponseSubscriber(ILoadingView loadingView, String message, boolean enableShowLoading) {
        super(loadingView, message, enableShowLoading);
    }

    @Override
    protected void handleError(Throwable e) {
        Log.e("api", "onError", e);
    }
}
