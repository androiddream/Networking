package com.github.ihsg.library.presenter;

import com.github.ihsg.library.view.ILoadingCancelListener;
import com.github.ihsg.library.view.ILoadingView;
import com.github.ihsg.library.view.LoadingSubscriber;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by hsg on 11/09/2017.
 */

public abstract class BaseResponseSubscriber<T> extends DisposableSubscriber<T> implements ILoadingCancelListener {

    private LoadingSubscriber<String> loadingSubscriber;
    private ILoadingView loadingView;
    private String message;
    private boolean enableShowLoading;

    public BaseResponseSubscriber(ILoadingView loadingView) {
        this(loadingView, "正在加载...");
    }

    public BaseResponseSubscriber(ILoadingView loadingView, String message) {
        this(loadingView, message, true);
    }

    public BaseResponseSubscriber(ILoadingView loadingView, String message, boolean enableShowLoading) {
        this.loadingView = loadingView;
        this.message = message;
        this.enableShowLoading = enableShowLoading;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.enableShowLoading) {
            this.loadingSubscriber = this.loadingView.showLoading(this, this.message);
        }
    }

    @Override
    public void onComplete() {
        if (this.loadingSubscriber != null) {
            this.loadingSubscriber.onComplete();
        }
        release();
    }

    @Override
    public void onError(Throwable e) {
        if (this.loadingSubscriber != null) {
            this.loadingSubscriber.onError(e);
        }
        release();
        handleError(e);
    }

    @Override
    public void onCancel() {
        release();
    }

    /**
     * handle error from server
     *
     * @param e
     */
    protected abstract void handleError(Throwable e);

    private void release() {
        if (!this.isDisposed()) {
            this.dispose();
        }

        this.loadingSubscriber = null;
        this.loadingView = null;
        this.message = null;
    }
}