package com.github.ihsg.library.net;

/**
 * Created by hsg on 10/09/2017.
 */

public interface IBaseUrl {

    boolean isOnline();

    String getBaseUrl();

    String getOnlineBaseUrl();

    String getOfflineBaseUrl();

    String getRestBaseUrl();

    String getOnlineRestBaseUrl();

    String getOfflineRestBaseUrl();

    String getH5BaseUrl();

    String getOnlineH5BaseUrl();

    String getOfflineH5BaseUrl();

    String getSiteBaseUrl();

    String getOnlineSiteBaseUrl();

    String getOfflineSiteBaseUrl();

}
