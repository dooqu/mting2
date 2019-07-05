package cn.xylink.mting;

import android.app.Application;
import android.content.Intent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.xylink.mting.model.Article;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.data.SpeechList;

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

    }

    public static MTing getInstance() {
        return instance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
