package cn.xylink.mting;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.tendcloud.tenddata.TCAgent;

import org.apaches.commons.codec.binary.Base64;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UpgradeInfo;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.UpgradeRequest;
import cn.xylink.mting.model.UpgradeResponse;
import cn.xylink.mting.common.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.speech.TTSAudioLoader;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.data.XiaoIceTTSAudioLoader;
import cn.xylink.mting.upgrade.UpgradeManager;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.GsonUtil;
import cn.xylink.mting.utils.ImageUtils;
import cn.xylink.mting.utils.PackageUtils;
import okhttp3.OkHttpClient;

public class MTing extends Application {

    private static MTing instance;
    private int mActivityCount = 0;

    public static ActivityManager activityManager = null;

    public static ActivityManager getActivityManager() {
        return activityManager;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        activityManager = ActivityManager.getScreenManager();
        ContentManager.init(this);
        WXapi.init(this);
        try {
            QQApi.init(this);
        }
        catch (Exception e) {
            Log.e("Application", "qq未安装");
        }
        initOkHttp();
        ImageUtils.init(this);

        startService(new Intent(this, SpeechService.class));

        try {
            checkOnlineUpgrade();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        TCAgent.LOG_ON = true;
        TCAgent.init(this, Const.TCAGENT_APPID, "mting");
        TCAgent.setReportUncaughtExceptions(true);
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(2000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build());

    }


    private void checkOnlineUpgrade() throws Exception {
        UpgradeRequest request = new UpgradeRequest();
        request.setAppPackage(PackageUtils.getAppPackage(this));
        request.setAppVersion(PackageUtils.getAppVersionName(this));
        request.setVersionId(PackageUtils.getAppVersionCode(this));
        request.setChannel(new Base64().encodeToString(EncryptionUtil.encrypt("mting", EncryptionUtil.getPublicKey(Const.publicKey))));
        request.setDeviceId(PackageUtils.getWifiMac(this));
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                },
                "http://service.xylink.cn/api/v2/version/check",
                GsonUtil.GsonString(request), UpgradeResponse.class,
                new OkGoUtils.ICallback<UpgradeResponse>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        Log.d("SPEECH", "onFailure:" + errorMsg);
                        UpgradeManager.CurrentUpgradeInfo = null;
                    }

                    @Override
                    public void onSuccess(UpgradeResponse response) {
                        Log.d("SPEECH", "onSuccess:" + response.getCode() + "," + response.getMessage());
                        Log.d("SPEECH", "upgrade.onSuccess");
                        if ((response.getCode() == 200 || response.getCode() == 201) && response.getData() != null) {
                            UpgradeManager.CurrentUpgradeInfo = response.getData();
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("xylink", "onComplete");
                    }
                });
    }

    public static MTing getInstance() {
        return instance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
