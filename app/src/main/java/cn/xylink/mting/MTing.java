package cn.xylink.mting;

import android.app.Application;

import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;

public class MTing extends Application {

    private static MTing instance;
    private int mActivityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        WXapi.init(this);
        QQApi.init(this);
        initOkHttp();
    }

    private void initOkHttp() {

    }

    public static MTing getInstance() {
        return instance;
    }



}
