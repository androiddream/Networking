package com.github.ihsg.networking.model.api;

import com.github.ihsg.networking.model.bean.GitHubUserBean;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * Created by hsg on 13/09/2017.
 */

public interface GitHubApi {

    @GET("users/list")
    Flowable<GitHubUserBean> getUser();
}
