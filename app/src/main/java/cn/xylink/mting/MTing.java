package cn.xylink.mting;

import android.app.Application;
import android.content.Intent;

import java.util.ArrayList;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.xylink.mting.model.Article;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.data.SpeechList;
import okhttp3.OkHttpClient;

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

        List<Article> list = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            Article article = new Article();
            article.setArticleId(String.valueOf(i));
            article.setTitle("习总书记讲话" + i);
            article.setTextBody("习近平强调，当前国际形势正在发生巨大变化");

            list.add(article);
        }

        SpeechList.getInstance().appendArticles(list);

        startService(new Intent(this, SpeechService.class));
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

    public static MTing getInstance() {
        return instance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
