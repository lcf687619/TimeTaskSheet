package com.mission.schedule.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author 超
 * @fileName NetUtils.java
 * @description 网络工具类
 * @email 364290013@qq.com
 * @date 2014-5-9
 * @change JohnWatson
 */
public class NetUtil {

    public enum NetWorkState {
        WIFI, MOBILE, NONE;
    }

    /**
     * 获取当前的网络状态
     *
     * @return
     */
    public static NetWorkState getConnectState(Context context) {
        ConnectivityManager manager = null;
        if (context != null) {
            manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (manager == null)
            return NetWorkState.NONE;

        NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();

        if (netWorkInfo == null || !netWorkInfo.isAvailable()
                || !netWorkInfo.isConnected()) {
            return NetWorkState.NONE;
        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetWorkState.WIFI;
        } else {
            return NetWorkState.MOBILE;
        }
    }

}
