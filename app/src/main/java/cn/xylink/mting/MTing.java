package cn.xylink.mting;

import android.app.Application;

import cn.xylink.mting.http.OkHttpUtils;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;

public class MTing extends Application {

    private static MTing instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        WXapi.init(this);
        QQApi.init(this);
        OkHttpUtils.init();
    }


    public static MTing getInstance() {
        return instance;
    }



}
