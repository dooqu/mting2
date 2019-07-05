package cn.xylink.mting.speech;

import java.util.List;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import cn.xylink.mting.model.Article;
import cn.xylink.mting.speech.data.ArticleDataProvider;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;


public class SpeechService extends Service {

    public enum SpeechServiceState {
        Ready,
        Playing,
        Paused,
        Stoped,
        Loadding
    }

    Speechor speechor;
    IBinder binder = new SpeechBinder();
    SpeechHelper helper = new SpeechHelper();
    Article article;
    int index = -1;
    SpeechList speechList = SpeechList.getInstance();
    SpeechServiceState serviceState;
    ArticleDataProvider articleDataProvider;
    int countToPlay = -1;

    public class SpeechBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        serviceState = SpeechServiceState.Ready;
        articleDataProvider = new ArticleDataProvider(this);
        speechor = new SpeechEngineWrapper(this) {
            @Override
            public void onStateChanged(SpeechorState speakerState) {
                //播放完毕
                if (speakerState == SpeechorState.SpeechorStateReady) {
                    if (SpeechService.this.hasNext()) {
                        SpeechService.this.playNextInvokeByInternal();
                    }
                    else {
                        //没有要读的文章了
                        EventBus.getDefault().post(new SpeechStopEvent());
                        serviceState = SpeechServiceState.Stoped;
                    }
                }
            }

            @Override
            public void onProgress(List<String> textFragments, int index) {
                EventBus.getDefault().post(new SpeechProgressEvent(index, textFragments, speechList.getCurrent()));

            }

            @Override
            public void onError(int errorCode, String message) {

                EventBus.getDefault().post(new SpeechErrorEvent(errorCode, message, speechList.getCurrent()));
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechor.reset();
        speechor.release();
        speechor = null;

        articleDataProvider.release();
        articleDataProvider = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Speechor.SpeechorState getState() {
        return speechor.getState();
    }


    private int seek(float percentage) {
        //如果当前播放不存在，那返回错误
        if (speechList.getCurrent() == null) {
            return -4;
        }

        //当前状态必须是在播放，或者是暂停，否则不支持
        if(serviceState == SpeechServiceState.Playing || serviceState == SpeechServiceState.Paused) {
            int index;
            if((index = helper.seekFragmentIndex(percentage, speechor.getTextFragments())) >= 0)
            {
                int result = speechor.seek(index);
                if(result >= 0)
                {
                    this.serviceState = SpeechServiceState.Playing;
                }
                return result;
            }
            return index;
        }

        return -5;
    }

    public boolean pause() {
        if (speechList.getCurrent() == null) {
            return false;
        }

        boolean result = false;
        switch (serviceState) {
            case Playing:
                result = this.speechor.pause();
                if (result) {
                    this.serviceState = SpeechServiceState.Paused;
                }
                return result;
            case Loadding:
                this.serviceState = SpeechServiceState.Ready;
                return true;
        }
        return false;
    }

    public boolean resume() {
        if (speechList.getCurrent() == null) {
            return false;
        }

        boolean result = false;
        switch (serviceState) {
            case Paused:
                result = this.speechor.resume();
                if (result) {
                    this.serviceState = SpeechServiceState.Playing;
                }
                return result;
            case Ready:
                this.serviceState = SpeechServiceState.Playing;
                return playSelected();
        }

        return false;
    }

    public boolean playSelected() {
        if (speechList.getCurrent() == null)
            return false;

        prepareArticle(speechList.getCurrent(), false);
        return true;
    }

    public Article selectAndPlay(String articleId) {
        Article article = this.speechList.select(articleId);
        if (article != null) {
            prepareArticle(article, false);
        }

        return article;
    }

    private void prepareArticle(final Article article, boolean needSourceEffect) {

        if(serviceState == SpeechServiceState.Playing && speechList.getCurrent() != null)
        {
            this.speechor.stop();
        }
        this.serviceState = SpeechServiceState.Loadding;
        this.articleDataProvider.updateArticle(article, needSourceEffect, (int errorcode, Article ar) -> {
            //网络加载动作结束后，走到这里， 要判定下errorCode
            if (errorcode != 0) {
                Log.d("xylink", "加载错误");
                this.serviceState = SpeechServiceState.Ready;
                EventBus.getDefault().post(new SpeechErrorEvent(errorcode, null, speechList.getCurrent()));
                //EventBus 通知？
                return;
            }

            //首先判定下回调回来后，是否物是人非，在加载期间，用户可能做了其他操作
            //比如暂停、切换文章，点选等等
            if (article != null
                    && speechList.getCurrent() != null
                    && article == speechList.getCurrent()) {

                //如果用户在加载期间，没有做其他操作，比如pause、切换文章
                if (serviceState == SpeechServiceState.Loadding) {
                    speechor.reset();
                    speechor.prepare(article.getTitle());
                    speechor.prepare(article.getTextBody());
                    speechor.seek(0);

                    this.serviceState = SpeechServiceState.Playing;
                }
            }
        });

        EventBus.getDefault().post(new SpeechStartEvent(speechList.getCurrent()));
    }


    public boolean playNext() {
        if (speechList.getCurrent() != null) {
            speechor.stop();
        }

        boolean nextExists = speechList.moveNext();
        if (nextExists) {
            prepareArticle(speechList.getCurrent(), false);
        }

        return nextExists;
    }


    private boolean playNextInvokeByInternal() {
        boolean nextExists = SpeechList.getInstance().moveNext();
        if (nextExists) {
            prepareArticle(speechList.getCurrent(), true);
        }
        return nextExists;
    }


    public boolean hasNext() {
        return speechList.hasNext();
    }


    public List<Article> getSpeechList() {
        return this.speechList.getArticleList();
    }

    public void loadArticles(List<Article> listToLoad) {
        this.speechList.appendArticles(listToLoad);
    }

    public void clearSpeechList() {
        synchronized (this) {
            boolean isSelectedDeleted = this.speechList.removeAll();
            if (isSelectedDeleted) {
                this.speechor.stop();
            }
        }
    }

    public void removeFromSpeechList(List<String> articleIds) {
        boolean isSelectedDeleted = this.speechList.removeSome(articleIds);
        if (isSelectedDeleted) {
            this.speechor.stop();
            if (this.speechList.size() > 0) {
                Article article = this.speechList.selectFirst();
                prepareArticle(article, false);
            }
        }
    }


    public Article getSelected() {
        return this.speechList.getCurrent();
    }

    public int getSpeechListSize() {
        return this.speechList.size();
    }


    public float getProgress() {
        return speechor.getProgress();
    }
}