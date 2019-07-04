package cn.xylink.mting;

import android.app.Application;
import android.content.Intent;

import java.util.LinkedList;
import java.util.List;

import cn.xylink.mting.model.Article;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.speech.SpeechService;

public class MTing extends Application {

    private static MTing instance;
    private int mActivityCount = 0;
    public  List<Article> articlesToRead;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        articlesToRead = new LinkedList<>();
        WXapi.init(this);
        QQApi.init(this);
        initOkHttp();

        for(int i = 0; i < 5; i++) {
            Article article = new Article();
            article.setArticleId("123");
            article.setTitle("习总书记讲话");
            article.setTextBody("习近平强调，当前国际形势正在发生巨大变化，中土要坚定维护以联合国为核心、以国际法为基础的国际体系，维护多边主义和国际公平正义，维护以世界贸易组织为核心的多边贸易体制，努力深化两国战略合作关系，捍卫两国和广大发展中国家的共同利益，共同构建相互尊重、公平正义、合作共赢的新型国际关系。双方要在地区事务中保持沟通与协调，共同推动政 治解决有关热点问题，为实现地区和平、稳定、发展作出贡献。");
            articlesToRead.add(article);

        }

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
