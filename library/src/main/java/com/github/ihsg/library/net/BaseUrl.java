package com.github.ihsg.library.net;

/**
 * Created by hsg on 10/09/2017.
 */

public abstract class BaseUrl implements IBaseUrl {

    @Override
    public String getBaseUrl() {
        return isOnline() ? getOnlineBaseUrl() : getOfflineBaseUrl();
    }

    @Override
    public String getRestBaseUrl() {
        return isOnline() ? getOnlineRestBaseUrl() : getOfflineRestBaseUrl();
    }

    @Override
    public String getH5BaseUrl() {
        return isOnline() ? getOnlineH5BaseUrl() : getOnlineH5BaseUrl();
    }

    @Override
    public String getSiteBaseUrl() {
        return isOnline() ? getOnlineSiteBaseUrl() : getOfflineSiteBaseUrl();
    }
}
