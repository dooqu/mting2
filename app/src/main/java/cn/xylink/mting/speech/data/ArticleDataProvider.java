package cn.xylink.mting.speech.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import cn.xylink.mting.model.Article;
import cn.xylink.mting.speech.SoundEffector;


/*
文章正文内容的网络加载类
 */
public class ArticleDataProvider {

    /*
    文章正文加载的回调，采用函数接口形式
    invoke(errorCode, Article article)
    errorCode: 0时无错误
    article:是返回的正文已经加载完成的Article
     */
    @FunctionalInterface
    public static interface ArticleLoaderCallback {
        void invoke(int errorCode, Article article);
    }

    SoundEffector soundEffector;
    Article currentArticle;
    long tickcount = 0;
    Handler handler;

    public ArticleDataProvider(Context context) {
        soundEffector = new SoundEffector(context);
        currentArticle = null;
        handler = new Handler(Looper.getMainLooper());
    }

    /*
    article:需要更新的 文章对象，其中正文字段是需要更新的
    needSoundPlay:在加载前播放新文章的启动音效
    callback;文章加载成功后的回调，回调在主线程
     */
    public void updateArticle(Article article, boolean needSoundPlay, ArticleLoaderCallback callback) {
        final long tickCountAtTime = ++tickcount;
        currentArticle = article;
        if (needSoundPlay) {
            soundEffector.playSwitch(null);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1500);
                }
                catch (InterruptedException ex) {
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tickCountAtTime == tickcount && callback != null) {
                            callback.invoke(0, article);
                        }
                        else {
                            Log.d("xylink", "取消");
                        }
                    }
                });
            }
        }).start();
    }


    public void release() {
        this.soundEffector.release();
        this.soundEffector = null;
    }
}
