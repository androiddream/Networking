package com.github.ihsg.networking.presenter;

import com.github.ihsg.networking.model.api.GitHubApi;
import com.github.ihsg.networking.model.network.GitHubNetWorker;

/**
 * Created by hsg on 13/09/2017.
 */

public class BasePresenter {
    private GitHubApi gitHubApi;

    public GitHubApi getGitHubApi() {
        if(this.gitHubApi == null){
            this.gitHubApi = GitHubNetWorker.createGitHubApiService();
        }
        return gitHubApi;
    }
}
