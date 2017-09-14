package com.github.ihsg.networking.presenter;

import com.github.ihsg.networking.model.bean.GitHubUserBean;
import com.github.ihsg.networking.view.IMainView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hsg on 13/09/2017.
 */

public class GitHubUserPresenter extends BasePresenter {
    private IMainView mainView;

    public GitHubUserPresenter(IMainView mainView) {
        super();
        this.mainView = mainView;
    }

    public void loadGitHubUser() {
        getGitHubApi().getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new ResponseSubscriber<GitHubUserBean>(this.mainView) {
                    @Override
                    public void onNext(GitHubUserBean gitHubUserBean) {
                        mainView.updateView(gitHubUserBean);
                    }
                });

    }
}
