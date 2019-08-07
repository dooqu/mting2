package cn.xylink.mting.speech;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.speech.data.ArticleDataProvider;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.FavoriteEvent;
import cn.xylink.mting.speech.event.SpeechArticleStatusSavedOnServerEvent;
import cn.xylink.mting.speech.event.SpeechEndEvent;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechPauseEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechReadyEvent;
import cn.xylink.mting.speech.event.SpeechResumeEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.activity.MainActivity;

/*
SpeechService 的核心思想也是控制分片播放
Speechor的核心控制思想也是分片
两者的分片颗粒不同
在Speechor中，分片是某一个文章的各个段落
而在SpeechService中，分片是整个播放列表

两者在内部都有Loadding状态
对于Speechor，内部的Loadding时刻是在分片和分片之间
而对于SpeechService，内部的Loadding时刻是在文章和文章之间
 */
public class SpeechService extends Service {
    static String TAG = "SPEECH_";

    /*SpeechService的状态描述类型*/
    public enum SpeechServiceState {
        /*正文准备就绪准备就绪*/
        Ready,
        /*播放中*/
        Playing,
        /*暂停中*/
        Paused,
        /*播放完成*/
        //Stoped,
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


    NotificationManager notificationManager;

    boolean isForegroundService;

    static int executeCode = 0;

    boolean isReleased;

    boolean isSimulatePaused;


    public class SpeechBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initService();
        initReceiver();
        setRole(Speechor.SpeechorRole.XiaoIce);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "SpeechService.onDestroy");
        super.onDestroy();
        unregisterReceiver(notifReceiver);
        unregisterReceiver(a2dpReceiver);
        isReleased = true;
        speechor.reset();
        speechor.release();
        articleDataProvider.release();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        this.stopForeground(true);
    }


    private void initService() {
        isForegroundService = false;
        isReleased = false;
        serviceState = SpeechServiceState.Ready;
        articleDataProvider = new ArticleDataProvider(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        speechor = new SpeechEngineWrapper(this) {
            @Override
            public void onStateChanged(SpeechorState speakerState) {
                synchronized (SpeechService.this) {
                    if (isReleased) {
                        return;
                    }
                    Article currentArticle = SpeechService.this.getSelected();
                    if (currentArticle == null) {
                        return;
                    }
                    //在每个文章播正常放完成后，注意是正常不受外部操作干扰的读玩， 像playNext()除外，因为他不触发结束的onReady
                    if (speakerState == SpeechorState.SpeechorStateReady) {
                        Log.d(TAG, "SpeechService.onStateChanged:Ready");
                        //强制设定progress为1
                        currentArticle.setProgress(1);
                        //调用onSpeechEnd事件
                        SpeechService.this.onSpeechEnd(currentArticle, 1, true);
                        //以下代码，判定播放后续动作，包括定时器或者列表为空的情况；先预先设置一个播放停止信号默认值
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
                        //调用moveNext，不是为了要播放下一个，因为当前没有下一个了， 而是要通过moveNext的调用，把指向的当前元素删除掉;
                        SpeechService.this.moveNext();
                        //没有要读的文章了
                        serviceState = SpeechServiceState.Ready;
                        onSpeechStoped(reason);
                    }
                }
            }

            @Override
            public void onProgress(List<String> textFragments, int index) {
                synchronized (SpeechService.this) {
                    if (speechList.getCurrent() == null) {
                        return;
                    }
                    onSpeechProgress(speechList.getCurrent(), index, textFragments);
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                synchronized (SpeechService.this) {
                    SpeechService.this.serviceState = SpeechServiceState.Error;
                    onSpeechError(errorCode, message, speechList.getCurrent());
                }
            }
        };

    }

    private void initReceiver() {
        IntentFilter notifIntent = new IntentFilter();
        notifIntent.addAction("play");
        notifIntent.addAction("pause");
        notifIntent.addAction("next");
        notifIntent.addAction("resume");
        notifIntent.addAction("favorite");
        notifIntent.addAction("unfavorite");
        registerReceiver(notifReceiver, notifIntent);

        //注册广播接收者监听状态改变
        IntentFilter a2dpIntent = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        //a2dpIntent.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        registerReceiver(a2dpReceiver, a2dpIntent);

    }


    private void onSpeechStart(Article article) {
        initNotification();
        EventBus.getDefault().post(new SpeechStartEvent(article));
    }

    private void onSpeechReady(Article article) {
        EventBus.getDefault().post(new SpeechReadyEvent(article));
    }

    private void onSpeechProgress(Article article, int fragmentIndex, List<String> fragments) {
        article.setProgress((float) fragmentIndex / (float) fragments.size());
        //if(fragmentIndex == 0) {
        initNotification();
        // }
        EventBus.getDefault().post(new SpeechProgressEvent(fragmentIndex, fragments, article));
    }

    private void onSpeechError(int errorCode, String message, Article article) {
        Log.d(TAG, "SpeechService.onSpeechError: errorCode=" + errorCode + ", message=" + message);
        EventBus.getDefault().post(new SpeechErrorEvent(errorCode, message, article));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        //initNotification();
    }

    private void onSpeechEnd(Article article, float progress, boolean deleteFromList) {
        //与云端同步数据状态
        articleDataProvider.readArticle(article, progress, deleteFromList, ((errorCode, articleResult) -> {
            EventBus.getDefault().post(new SpeechArticleStatusSavedOnServerEvent(errorCode, "", articleResult));
        }));

        if (progress == 1) {
            EventBus.getDefault().post(new SpeechEndEvent(article, progress));
        }
    }

    private void onSpeechPause(Article article) {
        EventBus.getDefault().post(new SpeechPauseEvent(article));
    }

    private void onSpeechResume(Article article) {
        EventBus.getDefault().post(new SpeechResumeEvent(article));
    }


    private void onSpeechStoped(SpeechStopEvent.StopReason reason) {
        EventBus.getDefault().post(new SpeechStopEvent(reason));
        //notificationManager.cancelAll();
        if (reason == SpeechStopEvent.StopReason.ListIsNull) {
            this.stopForeground(true);
        }
    }


    public synchronized SpeechServiceState getState() {
        return serviceState;
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
                        if (--SpeechService.this.countdownValue == 0
                                && (getState() == SpeechServiceState.Playing || getState() == SpeechServiceState.Loadding)) {
                            SpeechService.this.pause();
                            SpeechService.this.cancelCountDown();
                            SpeechService.this.onSpeechStoped(SpeechStopEvent.StopReason.CountDownToZero);
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
            return -SpeechError.NOTHING_TO_PLAY;
        }
        //当前状态必须是在播放，或者是暂停，否则不支持
        if (serviceState != SpeechServiceState.Ready && getSelected() != null && getSelected().getTextBody() != null) {
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

        return -SpeechError.SEEK_NOT_ALLOW;
    }


    /*
    pause的结果，可能导致当前状态为Paused或者Ready
     */
    public synchronized boolean pause() {
        if (speechList.getCurrent() == null) {
            return false;
        }

        boolean result = false;
        switch (serviceState) {
            case Loadding:
                isSimulatePaused = true;
                //do not break;
            case Playing:
                this.serviceState = SpeechServiceState.Paused;
                result = this.speechor.pause();
                initNotification();
                onSpeechPause(speechList.getCurrent());
                return true;
        }
        return false;
    }

    /*
    resume的时候要分情况，如果当前是Paused，那使用resume
    如果是ready，那么用playSelected();
     */
    public synchronized boolean resume() {
        if (speechList.getCurrent() == null) {
            return false;
        }

        boolean result = false;
        if (serviceState == SpeechServiceState.Paused) {
            if (this.isSimulatePaused == true) {
                playSelected();
                initNotification();
                onSpeechResume(speechList.getCurrent());
                return true;
            }
            else {
                result = this.speechor.resume();
                if (result) {
                    serviceState = SpeechServiceState.Playing;
                    initNotification();
                    onSpeechResume(speechList.getCurrent());
                }
                else {
                    result = seek(getProgress()) > 0;
                    initNotification();
                    onSpeechResume(speechList.getCurrent());
                }
                return result;
            }
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
        if (articleId == null) {
            return null;
        }
        Article previousArt = this.speechList.getCurrent();
        if (previousArt != null && articleId.equals(previousArt.getArticleId()) == false) {
            if (previousArt.getProgress() != 1) {
                this.onSpeechEnd(previousArt, previousArt.getProgress(), false);
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
                this.onSpeechEnd(previousArt, previousArt.getProgress(), false);
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
        this.onSpeechStart(article);
        this.articleDataProvider.loadArticleContent(article, needSourceEffect, (int errorcode, Article articleUpdated) -> {

            synchronized (this) {
                //如果回来之后，状态已经不是Loadding，说明在加载期间，有了其他操作
                if (isReleased || serviceState != SpeechServiceState.Loadding || articleUpdated != this.speechList.getCurrent()) {
                    return;
                }
                //网络加载动作结束后，走到这里， 要判定下errorCode
                if (errorcode != 0) {
                    //文章正文加载错误
                    this.serviceState = SpeechServiceState.Error;
                    this.onSpeechError(SpeechError.ARTICLE_LOAD_ERROR, "文章正文加载失败", article);
                    return;
                }

                this.onSpeechReady(article);
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
            this.onSpeechEnd(speechList.getCurrent(), speechList.getCurrent().getProgress(), true);
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


    public synchronized Speechor.SpeechorRole getRole() {
        return speechor.getRole();
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
            this.serviceState = SpeechServiceState.Ready;
            this.onSpeechStoped(SpeechStopEvent.StopReason.ListIsNull);
        }
    }

    public synchronized void removeFromSpeechList(List<String> articleIds) {
        Article currentArt = this.speechList.getCurrent();
        boolean isSelectedDeleted = this.speechList.removeSome(articleIds);
        //如果当前正在播放的被删除掉
        if (isSelectedDeleted) {
            this.speechor.stop();
            //this.onSpeechEnd(currentArt, currentArt.getProgress());
            //??是否还记录播放进度
            if (this.speechList.size() > 0) {
                //播放列表中的第一个
                Article article = this.speechList.selectFirst();
                prepareArticle(article, false);
            }
            else {
                this.serviceState = SpeechServiceState.Ready;
                //没有要播放的内容了
                this.onSpeechStoped(SpeechStopEvent.StopReason.ListIsNull);
            }
        }
    }


    public synchronized Article getSelected() {
        return this.speechList.getCurrent();
    }


    public synchronized float getProgress() {
        return speechor.getProgress();
    }


    public synchronized List<String> getSpeechorTextFragments() {
        return speechor.getTextFragments();
    }

    public synchronized int getSpeechorFrameIndex() {
        return speechor.getFragmentIndex();
    }

    public void updateNotification() {
        this.initNotification();
    }


    private void initNotification() {

        synchronized (this) {
            Article currentArticle = this.speechList.getCurrent();
            if (currentArticle == null) {
                return;
            }

            Intent intentNotifOpen = new Intent(this, MainActivity.class);
            intentNotifOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentNotifOpen, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    //.setDeleteIntent(pendingIntentCancel)
                    .setSmallIcon(R.mipmap.icon_notif)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification_album))
                    .setTicker(currentArticle.getTitle())
                    .setContentTitle("轩辕听")
                    .setContentText(currentArticle.getTitle())
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setShowWhen(false);

            //>= android 8.0 设定foregroundService的前提是notification要创建channel，并关掉channel的sound
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelId = "cn.xylink.mting";
                String channelName = "SPEECH_SERVICE_NAME";
                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setSound(null, null);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
                //设定builder的channelid
                builder.setChannelId(channelId);
            }

            Intent playIntent = new Intent("play");
            Intent resumeIntent = new Intent("resume");
            Intent pauseIntent = new Intent("pause");
            Intent favIntent = new Intent("favorite");
            Intent unFavIntent = new Intent("unfavorite");
            Intent nextIntent = new Intent("next");
            Intent noneIntent = new Intent("null");

            Notification.Action actionPlay = null, actionNext = null, actionFav = null;

            boolean favorited = currentArticle.getStore() == 1;

            switch (serviceState) {
                case Loadding:
                case Playing:
                    actionFav = new Notification.Action(favorited ? R.mipmap.favorited : R.mipmap.unfavorited, "", PendingIntent.getBroadcast(this, ++executeCode, favorited ? unFavIntent : favIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    actionPlay = new Notification.Action(R.mipmap.ico_pause, "", PendingIntent.getBroadcast(this, ++executeCode, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    actionNext = new Notification.Action(R.mipmap.next, "", PendingIntent.getBroadcast(this, ++executeCode, (hasNext() ? nextIntent : noneIntent), PendingIntent.FLAG_UPDATE_CURRENT));
                    break;

                case Paused:
                    actionFav = new Notification.Action(favorited ? R.mipmap.favorited : R.mipmap.unfavorited, "", PendingIntent.getBroadcast(this, ++executeCode, favorited ? unFavIntent : favIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    actionPlay = new Notification.Action(R.mipmap.ico_playing, "", PendingIntent.getBroadcast(this, ++executeCode, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    actionNext = new Notification.Action(R.mipmap.next, "", PendingIntent.getBroadcast(this, ++executeCode, (hasNext() ? nextIntent : noneIntent), PendingIntent.FLAG_UPDATE_CURRENT));
                    break;

                case Error:
                    actionFav = new Notification.Action(favorited ? R.mipmap.favorited : R.mipmap.unfavorited, "", PendingIntent.getBroadcast(this, ++executeCode, noneIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    actionPlay = new Notification.Action(R.mipmap.ico_playing, "", PendingIntent.getBroadcast(this, ++executeCode, playIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                    actionNext = new Notification.Action(R.mipmap.next, "", PendingIntent.getBroadcast(this, ++executeCode, (hasNext() ? nextIntent : noneIntent), PendingIntent.FLAG_UPDATE_CURRENT));
                    break;

                default:
                    return;
            }

            if (actionFav != null) {
                builder.addAction(actionFav);
            }

            if (actionPlay != null) {
                builder.addAction(actionPlay);
            }

            if (actionNext != null) {
                builder.addAction(actionNext);
            }

            Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
            mediaStyle.setShowActionsInCompactView(0, 1, 2);
            builder.setStyle(mediaStyle);

            Notification notification = builder.build();
            this.startForeground(android.os.Process.myPid(), notification);
        }
    }

    private BroadcastReceiver notifReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                final String action = intent.getAction();
                Article currentArticle = getSelected();
                if (currentArticle == null) {
                    return;
                }

                switch (action) {
                    case "play":
                        if (getSelected() != null) {
                            playSelected();
                        }
                        break;

                    case "pause":
                        pause();
                        break;

                    case "resume":
                        resume();
                        break;

                    case "next":
                        switch (serviceState) {
                            case Ready:
                                if (getSelected() != null) {
                                    playSelected();
                                }
                                break;
                            default:
                                if (hasNext()) {
                                    playNext();
                                }
                                break;
                        }
                        break;

                    case "favorite":
                        if (currentArticle.getStore() == 1) {
                            break;
                        }
                        articleDataProvider.favorite(currentArticle, true, ((errorCode, article) -> {
                            if (errorCode == 0) {
                                initNotification();
                                EventBus.getDefault().post(new FavoriteEvent(currentArticle));
                            }
                        }));
                    case "unfavorite":
                        if (currentArticle.getStore() == 0) {
                            break;
                        }
                        articleDataProvider.favorite(currentArticle, false, ((errorCode, article) -> {
                            if (errorCode == 0) {
                                initNotification();
                                EventBus.getDefault().post(new FavoriteEvent(currentArticle));
                            }
                        }));
                        break;
                } // end switch
            } // end sychornized
        } // end onReceive
    }; // end class

    private BroadcastReceiver a2dpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive action=" + action);
            switch (action) {
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                    Log.d("SPEECH_", "A2DP_Connection_State_Changed:state=" + state);
                    if (state == BluetoothA2dp.STATE_DISCONNECTED) {
                        if (serviceState == SpeechServiceState.Playing) {
                            pause();
                        }
                    }
                    break;
            }
        }
    };
}


