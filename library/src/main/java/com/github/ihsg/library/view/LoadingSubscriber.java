package com.github.ihsg.library.view;

import android.content.Context;
import android.os.Handler;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by hsg on 11/09/2017.
 */

public class LoadingSubscriber<String> extends DisposableSubscriber<String> {

    private Handler handler;
    private LoadingView loadingView;

    public LoadingSubscriber(Context context, ILoadingCancelListener cancelListener) {
        this.loadingView = new LoadingView(context, cancelListener);
        this.handler = this.loadingView.getHandler();
    }

    /**
     * release resource, called on onDestroy() function in Activity & Fragment
     */
    public void release() {
        if (this.loadingView != null) {
            this.loadingView.release();
            this.loadingView = null;
        }
        this.handler = null;
    }

    @Override
    public void onError(Throwable e) {
        handler.obtainMessage(LoadingMessageType.DISMISS_LOADING).sendToTarget();
    }

    @Override
    public void onComplete() {
        handler.obtainMessage(LoadingMessageType.DISMISS_LOADING).sendToTarget();
    }

    @Override
    public void onNext(String string) {
        handler.obtainMessage(LoadingMessageType.SHOW_LOADING, string).sendToTarget();
    }
}