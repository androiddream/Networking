package com.github.ihsg.networking.model.network;

import com.github.ihsg.library.net.BaseUrl;

/**
 * Created by hsg on 13/09/2017.
 */

public class GitHubBaseUrl extends BaseUrl {
    private static final String GIT_HUB_BASE_URL = "https://api.github.com/";

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getOnlineBaseUrl() {
        return null;
    }

    @Override
    public String getOfflineBaseUrl() {
        return null;
    }

    @Override
    public String getOnlineRestBaseUrl() {
        return GIT_HUB_BASE_URL;
    }

    @Override
    public String getOfflineRestBaseUrl() {
        return null;
    }

    @Override
    public String getOnlineH5BaseUrl() {
        return null;
    }

    @Override
    public String getOfflineH5BaseUrl() {
        return null;
    }

    @Override
    public String getOnlineSiteBaseUrl() {
        return null;
    }

    @Override
    public String getOfflineSiteBaseUrl() {
        return null;
    }
}
