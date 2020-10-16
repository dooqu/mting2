package cn.xylink.mting;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.tendcloud.tenddata.TCAgent;

import org.apaches.commons.codec.binary.Base64;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.UpgradeRequest;
import cn.xylink.mting.model.UpgradeResponse;
import cn.xylink.mting.common.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.speech.TTSAudioLoader;
import cn.xylink.mting.speech.data.XiaoIceTTSAudioLoader;
import cn.xylink.mting.upgrade.UpgradeManager;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.GsonUtil;
import cn.xylink.mting.utils.ImageUtils;
import cn.xylink.mting.utils.PackageUtils;
import cn.xylink.mting.utils.SharedPreHelper;
import okhttp3.OkHttpClient;

public class MTing extends Application {

    private static MTing instance;
    private int mActivityCount = 0;

    public static ActivityManager activityManager = null;

    public static ActivityManager getActivityManager() {
        return activityManager;
    }

    public String AudioCachePath;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        activityManager = ActivityManager.getScreenManager();
        ContentManager.init(this);
        WXapi.init(this);
        try {
            QQApi.init(this);
        } catch (Exception e) {
            Log.e("Application", "qq未安装");
        }
        initOkHttp();
        ImageUtils.init(this);

        clearAudioCache();
        Intent serviceIntent = new Intent(this, SpeechService.class);
        String defaultRole = String.valueOf(SharedPreHelper.getInstance(this).getSharedPreference("SPEECH_ROLE", "XiaoIce"));
        String defaultSpeed = String.valueOf(SharedPreHelper.getInstance(this).getSharedPreference("SPEECH_SPEED", "SPEECH_SPEED_NORMAL"));
        serviceIntent.putExtra("role", defaultRole);
        serviceIntent.putExtra("speed", defaultSpeed);
        startService(serviceIntent);

        try {
            checkOnlineUpgrade();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        TCAgent.LOG_ON = true;
        TCAgent.init(this, Const.TCAGENT_APPID, "renrendict");
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

    private void clearAudioCache() {
        File audioCacheFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + PackageUtils.getAppPackage(MTing.getInstance()) + "/audio/");
        AudioCachePath = audioCacheFile.getPath();
        new Thread(() -> {
            File[] mp3Files = audioCacheFile.listFiles();
            if (mp3Files == null || mp3Files.length <= 0) {
                return;
            }
            for (File mp3File : mp3Files) {
                mp3File.delete();
            }
        }).start();
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
                RemoteUrl.getUpgradeUrl(),
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


    public boolean isDebugMode() {
        try {
            ApplicationInfo info = this.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }


    public static MTing getInstance() {
        return instance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private String getAppName(int pID) {
        String processName = null;
        android.app.ActivityManager am = (android.app.ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            android.app.ActivityManager.RunningAppProcessInfo info = (android.app.ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
