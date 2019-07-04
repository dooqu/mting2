package cn.xylink.mting.speech;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.xylink.mting.MTing;
import cn.xylink.mting.model.Article;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStateChangedEvent;

public class SpeechService extends Service {

    IBinder binder = new SpeechBinder();
    SpeechHelper helper = new SpeechHelper();
    List<Article> articles ;
    Article article;
    int index = -1;

    public class SpeechBinder extends Binder
    {
        public SpeechService getService()
        {
            return SpeechService.this;
        }
    }

    Speechor speechor;
    @Override
    public void onCreate() {
        super.onCreate();

        speechor = new SpeechEngineWrapper(this) {
            @Override
            public void onStateChanged(SpeechorState speakerState) {

                EventBus.getDefault().post(new SpeechStateChangedEvent(speakerState, article));

                if(speakerState == SpeechorState.SpeechorStateReady)
                {
                    speechor.reset();
                    next();
                }
            }

            @Override
            public void onProgress(List<String> textFragments, int index) {
                EventBus.getDefault().post(new SpeechProgressEvent(index, textFragments, article));

            }

            @Override
            public void onError(int errorCode, String message) {

                EventBus.getDefault().post(new SpeechErrorEvent(errorCode, message, article));
            }
        };

        articles = ((MTing)getApplication()).articlesToRead;
        article = null;
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Speechor.SpeechorState getState()
    {
        return speechor.getState();
    }


    public int seek(float percentage)
    {
        int index = helper.seekFragmentIndex(percentage, speechor.getTextFragments());
        return speechor.seek(index);
    }

    public boolean pause()
    {
        return speechor.pause();
    }

    public boolean resume()
    {
        return speechor.resume();
    }

    public void prepare(String text)
    {
        speechor.prepare(text);
    }

    public float getProgress()
    {
        return speechor.getProgress();
    }

    public void reset()
    {
        speechor.reset();
    }

    public void setArticle(Article article)
    {
        synchronized (articles)
        {
            for(int i = 0; i < articles.size(); i++)
            {
                if(articles.get(i) == article)
                {
                    index = i;
                    this.prepare(article.getTitle());
                    this.prepare(article.getTextBody());
                    this.seek(0);
                }
            }
        }
    }

    private boolean next()
    {
        synchronized (articles) {
            if ((index + 1) >= articles.size())
                return false;

            ++index;
            article = articles.get(index);
            this.prepare(article.getTitle());
            this.prepare(article.getTextBody());
            this.seek(0);

            return true;
        }
    }
}
