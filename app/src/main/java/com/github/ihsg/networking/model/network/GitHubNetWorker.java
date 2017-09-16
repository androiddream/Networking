package com.github.ihsg.networking.model.network;

import android.util.Log;

import com.github.ihsg.library.net.BaseNetWorker;
import com.github.ihsg.library.net.NetLogLevel;
import com.github.ihsg.library.net.ServerTimestampListener;
import com.github.ihsg.networking.model.api.GitHubApi;

import java.util.HashMap;

/**
 * Created by hsg on 13/09/2017.
 */

public class GitHubNetWorker extends BaseNetWorker {

    private static final String TAG = "GitHubNetWorker";

    private static GitHubBaseUrl gitHubBaseUrl;

    public static GitHubApi createGitHubApiService() {
        BaseNetWorker.Builder builder = new GitHubNetWorker().getBuilder()
                .setBaseUrl(getGitHubBaseUrl())
                .setNetLogLevel(NetLogLevel.BODY)
                .setServerTimestampListener(new ServerTimestampListener() {
                    @Override
                    public void onResponse(String serverTimestamp) {
                        Log.i(TAG, serverTimestamp);
                    }
                })
                .setTimestampKey("Date")
                .setHeaders(new HashMap<String, String>() {{
                    put("UserAgent", "Android 10.0");
                }});
        return builder.build(GitHubApi.class);
    }

    public static GitHubBaseUrl getGitHubBaseUrl() {
        if (gitHubBaseUrl == null) {
            gitHubBaseUrl = new GitHubBaseUrl();
        }
        return gitHubBaseUrl;
    }
}
