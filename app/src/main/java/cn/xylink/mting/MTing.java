package cn.xylink.mting;

import android.app.Application;
import android.content.Intent;

import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.speech.SpeechService;

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

        startService(new Intent(this, SpeechService.class));
    }

    private void initOkHttp() {

    }

    public static MTing getInstance() {
        return instance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
