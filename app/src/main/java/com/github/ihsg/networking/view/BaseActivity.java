package com.github.ihsg.networking.view;

import android.support.v7.app.AppCompatActivity;

import com.github.ihsg.library.view.ILoadingCancelListener;
import com.github.ihsg.library.view.ILoadingView;
import com.github.ihsg.library.view.LoadingSubscriber;

/**
 * Created by hsg on 13/09/2017.
 */

public class BaseActivity extends AppCompatActivity implements ILoadingView {

    private LoadingSubscriber<String> loadingSubscriber;


    @Override
    protected void onDestroy() {
        if (this.loadingSubscriber != null) {
            this.loadingSubscriber.release();
            this.loadingSubscriber = null;
        }
        super.onDestroy();
    }

    @Override
    public LoadingSubscriber<String> showLoading(ILoadingCancelListener cancelListener, String message) {
        if (this.loadingSubscriber == null) {
            this.loadingSubscriber = new LoadingSubscriber<>(this, cancelListener);
        }
        this.loadingSubscriber.onNext(message);
        return this.loadingSubscriber;
    }
}
