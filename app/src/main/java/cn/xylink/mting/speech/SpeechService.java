package cn.xylink.mting.speech;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import cn.xylink.mting.bean.Article;
import cn.xylink.mting.speech.data.ArticleDataProvider;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;


public class SpeechService extends Service {

    /*SpeechService的状态描述类型*/
    public enum SpeechServiceState {
        /*准备就绪*/
        Ready,
        /*播放中*/
        Playing,
        /*暂停中*/
        Paused,
        /*播放完成*/
        Stoped,
        /*加载中*/
        Loadding,
        /*发生错误*/
        Error
    }

    /*
    定时器类型，用于service.setCountDown
     */
    public enum CountDownMode {
        /*定时器关闭*/
        None,
        /*播放数量定时器*/
        NumberCount,
        /*分钟定时器*/
        MinuteCount
    }

    /*TTS 播报接口对象*/
    Speechor speechor;

    /*binder 对象，用来返回service对象*/
    IBinder binder = new SpeechBinder();

    /*Speech 算法的常用函数*/
    SpeechHelper helper = new SpeechHelper();

    /*播放列表，speechList是对全局SpeechList对象的引用*/
    SpeechList speechList = SpeechList.getInstance();

    /*SpeechService的状态*/
    SpeechServiceState serviceState;

    /*常用对Article的网络操作类*/
    ArticleDataProvider articleDataProvider;

    /*倒计时数值，可以表示倒计时的分钟数，也可以表示倒计时的播放数*/
    int countdownValue;

    /*指示countdownValue的类型*/
    CountDownMode countDownMode;

    /*分钟倒计时要使用的Timer，没分钟递减一次*/
    Timer countdownTimer;



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
                synchronized (SpeechService.this) {
                    Article currentArticle = SpeechService.this.getSelected();
                    //在每个文章播放完成后，做以下逻辑判定
                    if (speakerState == SpeechorState.SpeechorStateReady) {
                        currentArticle.setProgress(1);
                        SpeechService.this.onSaveArticleProgress(currentArticle, 1);
                        //先预先设置一个播放停止信号默认值
                        SpeechStopEvent.StopReason reason = SpeechStopEvent.StopReason.ListIsNull;
                        //在每个播放完成的时机，判断下当前是否有Number定时器， 如果有，就减一，如果减一等于0，说明定时器到期
                        if (SpeechService.this.countDownMode == CountDownMode.NumberCount && --countdownValue == 0) {
                            SpeechService.this.cancelCountDown();
                            reason = SpeechStopEvent.StopReason.CountDownToZero;
                        }
                        //如果定时器没有走， 那继续判定是否还有下一个文章可播放， 如果有，去播放；
                        else if (SpeechService.this.hasNext()) {
                            SpeechService.this.playNextInvokeByInternal();
                            return;
                        }
                        //不是要播放下一个，因为当前没有下一个了， 而是要通过playNext、内部为moveNext，删除当前的
                        SpeechService.this.moveNext();
                        //没有要读的文章了
                        serviceState = SpeechServiceState.Stoped;
                        EventBus.getDefault().post(new SpeechStopEvent(reason));
                    }
                }
            }

            @Override
            public void onProgress(List<String> textFragments, int index) {
                synchronized (SpeechService.this) {
                    speechList.getCurrent().setProgress((float) index / (float) textFragments.size());
                }
                EventBus.getDefault().post(new SpeechProgressEvent(index, textFragments, speechList.getCurrent()));
            }

            @Override
            public void onError(int errorCode, String message) {
                SpeechService.this.serviceState = SpeechServiceState.Error;
                EventBus.getDefault().post(new SpeechErrorEvent(errorCode, message, speechList.getCurrent()));
            }
        };
    }


    protected void onSaveArticleProgress(Article article, float progress) {
        //articleDataProvider.readArticle(article.getArticleId(), progress);
        Log.d("xylink", "onSaveProgress:" + article.getTitle() + "=>" + progress);
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


    public synchronized Speechor.SpeechorState getState() {
        return speechor.getState();
    }


    /*
    设定计时器
     */
    public synchronized void setCountDown(CountDownMode mode, int tickcountValue) {
        //如果一个倒计时正在进行，先取消
        this.cancelCountDown();
        //参数检查
        if (tickcountValue <= 0 || mode == CountDownMode.None) {
            return;
        }

        this.countDownMode = mode;
        this.countdownValue = tickcountValue;

        if (mode == CountDownMode.MinuteCount) {
            countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (SpeechService.this) {
                        if (--SpeechService.this.countdownValue == 0 && SpeechService.this.getState() == Speechor.SpeechorState.SpeechorStatePlaying) {
                            SpeechService.this.speechor.stop();
                            SpeechService.this.serviceState = SpeechServiceState.Stoped;
                            SpeechService.this.cancelCountDown();
                            EventBus.getDefault().post(new SpeechStopEvent(SpeechStopEvent.StopReason.CountDownToZero));
                        }
                    }
                }
            }, 1000 * 60, 1000 * 60);
        }
    }

    public synchronized void cancelCountDown() {
        if (this.countDownMode == CountDownMode.MinuteCount) {
            if (this.countdownTimer != null) {
                this.countdownTimer.cancel();
                this.countdownTimer = null;
            }
        }
        this.countDownMode = CountDownMode.None;
        this.countdownValue = 0;
    }


    public synchronized CountDownMode getCountDownMode() {
        return this.countDownMode;
    }


    public synchronized int getCountDownValue() {
        return this.countdownValue;
    }


    public synchronized int seek(float percentage) {
        //如果当前播放不存在，那返回错误
        if (speechList.getCurrent() == null) {
            return -4;
        }

        //当前状态必须是在播放，或者是暂停，否则不支持
        if (serviceState == SpeechServiceState.Playing || serviceState == SpeechServiceState.Paused) {
            int index;
            if ((index = helper.seekFragmentIndex(percentage, speechor.getTextFragments())) >= 0) {
                int result = speechor.seek(index);
                if (result >= 0) {
                    this.serviceState = SpeechServiceState.Playing;
                }
                return result;
            }
            return index;
        }
        return -5;
    }


    public synchronized boolean pause() {
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

    public synchronized boolean resume() {
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

    private synchronized boolean playSelected() {
        if (speechList.getCurrent() == null)
            return false;

        prepareArticle(speechList.getCurrent(), false);
        return true;
    }


    public synchronized Article play(String articleId) {
        Article previousArt = this.speechList.getCurrent();
        if (previousArt != null && articleId.equals(previousArt.getArticleId()) == false) {
            if (previousArt.getProgress() != 1) {
                this.onSaveArticleProgress(previousArt, previousArt.getProgress());
            }
        }

        Article article = this.speechList.select(articleId);
        if (article != null) {
            prepareArticle(article, false);
        }

        return article;
    }


    public synchronized Article addFirstAndPlay(Article article) {
        Article previousArt = this.speechList.getCurrent();
        if (previousArt != null && article.getArticleId().equals(previousArt.getArticleId()) == false) {
            if (previousArt.getProgress() != 1) {
                this.onSaveArticleProgress(previousArt, previousArt.getProgress());
            }
        }

        Article artcleSelected = this.speechList.pushFrontAndSelect(article);
        if (artcleSelected != null) {
            prepareArticle(article, false);
        }

        return artcleSelected;
    }


    public synchronized void addFirst(List<Article> list) {
        this.speechList.pushFront(list);
    }

    public synchronized void setSpeed(Speechor.SpeechorSpeed speed) {
        this.speechor.setSpeed(speed);
    }

    public synchronized Speechor.SpeechorSpeed getSpeed() {
        return this.speechor.getSpeed();
    }


    private void prepareArticle(final Article article, boolean needSourceEffect) {
        if (speechList.getCurrent() != null && serviceState == SpeechServiceState.Playing) {
            this.speechor.stop();
        }

        this.serviceState = SpeechServiceState.Loadding;
        EventBus.getDefault().post(new SpeechStartEvent(speechList.getCurrent()));
        this.articleDataProvider.loadArticleContent(article, needSourceEffect, (int errorcode, Article articleUpdated) -> {

            synchronized (this) {
                //如果回来之后，状态已经不是Loadding，说明在加载期间，有了其他操作
                if (serviceState != SpeechServiceState.Loadding || articleUpdated != this.speechList.getCurrent()) {
                    return;
                }
                //网络加载动作结束后，走到这里， 要判定下errorCode
                if (errorcode != 0) {
                    //文章正文加载错误
                    this.serviceState = SpeechServiceState.Ready;
                    EventBus.getDefault().post(new SpeechErrorEvent(errorcode, null, speechList.getCurrent()));
                    return;
                }
                //首先判定下回调回来后，是否物是人非，在加载期间，用户可能做了其他操作
                //比如暂停、切换文章，点选等等
                //如果用户在加载期间，没有做其他操作，比如pause、切换文章
                speechor.reset();
                speechor.prepare(article.getTitle());
                speechor.prepare(article.getTextBody());

                int fragmentSize = speechor.getTextFragments().size();
                int destFragIndex = helper.seekFragmentIndex(article.getProgress(), speechor.getTextFragments());

                if (destFragIndex >= fragmentSize) {
                    destFragIndex = fragmentSize - 1;
                }

                if (speechor.seek(destFragIndex) >= 0) {
                    this.serviceState = SpeechServiceState.Playing;
                }
                else {
                    this.serviceState = SpeechServiceState.Ready;
                }
            } // end synchonized
        });
    }

    private boolean moveNext() {
        return speechList.moveNext();
    }


    public synchronized boolean playNext() {
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

    public synchronized void setRole(Speechor.SpeechorRole role) {
        speechor.setRole(role);
    }


    public synchronized void getRole() {
        speechor.getRole();
    }


    public boolean hasNext() {
        return speechList.hasNext();
    }


    public synchronized List<Article> getSpeechList() {
        return this.speechList.getArticleList();
    }

    public void loadArticles(List<Article> listToLoad) {
        this.speechList.appendArticles(listToLoad);
    }

    public synchronized void clearSpeechList() {
        boolean isSelectedDeleted = this.speechList.removeAll();
        if (isSelectedDeleted) {
            this.speechor.stop();
            EventBus.getDefault().post(new SpeechStopEvent());
        }
    }

    public synchronized void removeFromSpeechList(List<String> articleIds) {
        boolean isSelectedDeleted = this.speechList.removeSome(articleIds);
        /*
        如果当前正在播放的被删除掉
         */
        if (isSelectedDeleted) {
            this.speechor.stop();
            if (this.speechList.size() > 0) {
                //播放列表中的第一个
                Article article = this.speechList.selectFirst();
                prepareArticle(article, false);
            }
            else {
                //没有要播放的内容了
                EventBus.getDefault().post(new SpeechStopEvent());
            }
        }
    }


    public synchronized Article getSelected() {
        return this.speechList.getCurrent();
    }


    public synchronized float getProgress() {
        return speechor.getProgress();
    }
}
