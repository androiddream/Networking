package com.github.ihsg.networking.view;

import com.github.ihsg.library.view.ILoadingView;
import com.github.ihsg.networking.model.bean.GitHubUserBean;

/**
 * Created by hsg on 13/09/2017.
 */

public interface IMainView extends ILoadingView {
    void updateView(GitHubUserBean gitHubUserBean);
}
