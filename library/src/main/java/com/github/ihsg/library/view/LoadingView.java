package com.github.ihsg.library.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * Created by hsg on 12/09/2017.
 */

class LoadingView {

    private Context context;
    private ILoadingCancelListener progressCancelListener;
    private Handler handler;
    private ProgressDialog progressDialog;

    public LoadingView(Context context, ILoadingCancelListener cancelListener) {
        this.context = context;
        this.progressCancelListener = cancelListener;
        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg != null) {
                    switch (msg.what) {
                        case LoadingMessageType.SHOW_LOADING:
                            showProgress((String) msg.obj);
                            break;
                        case LoadingMessageType.DISMISS_LOADING:
                            dismissProgress();
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void release() {
        dismissProgress();
        this.progressDialog = null;
        this.context = null;
        this.handler = null;
        this.progressCancelListener = null;
    }

    private void showProgress(String msg) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setCancelable(true);
            this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (progressCancelListener != null) {
                        progressCancelListener.onCancel();
                    }
                }
            });
        }
        dismissProgress();
        this.progressDialog.setMessage((CharSequence) msg);
        this.progressDialog.show();
    }

    private void dismissProgress() {
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }
}
