package com.github.ihsg.library.view;

/**
 * Created by hsg on 11/09/2017.
 */

public interface ILoadingView {
    LoadingSubscriber<String> showLoading(ILoadingCancelListener cancelListener, String message);
}
