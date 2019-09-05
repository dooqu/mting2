package cn.xylink.mting.speech;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cn.xylink.mting.speech.data.XiaoIceTTSAudioLoader;

public abstract class XiaoIceSpeechor implements Speechor {

    static String TAG = XiaoIceSpeechor.class.getSimpleName();

    enum SpeechTextFragmentState {
        TextReady,
        AudioLoadding,
        AudioReady,
        Error
    }


    public class SpeechTextFragment {
        String fragmentText;
        String audioUrl;
        SpeechTextFragmentState fragmentState;
        String errorMessage;
        int retryCount = 0;
        int frameIndex;
        long seekTime;
        boolean firstFragment;

        public SpeechTextFragment() {
            this.fragmentState = SpeechTextFragmentState.TextReady;
        }

        public String getFragmentText() {
            return fragmentText;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public SpeechTextFragmentState getFragmentState() {
            return fragmentState;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public void setFragmentState(SpeechTextFragmentState fragmentState) {
            this.fragmentState = fragmentState;
        }

        public void setFragmentText(String fragmentText) {
            this.fragmentText = fragmentText;
        }

        public int getFrameIndex() {
            return frameIndex;
        }

        public void setFrameIndex(int frameIndex) {
            this.frameIndex = frameIndex;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public long getSeekTime() {
            return seekTime;
        }

        public void setSeekTime(long seekTime) {
            this.seekTime = seekTime;
        }

        public boolean isFirstFragment() {
            return firstFragment;
        }

        public void setFirstFragment(boolean firstFragment) {
            this.firstFragment = firstFragment;
        }
    }


    public abstract class IceLoadResult implements TTSAudioLoader.LoadResult {
        SpeechTextFragment fragment;

        public IceLoadResult(SpeechTextFragment fragment) {
            this.fragment = fragment;
        }
    }


    int fragmentIndex;
    int fragmentIndexNext;
    boolean isReleased;
    List<String> textFragments;
    List<SpeechTextFragment> speechTextFragments;
    SpeechorState state;
    SpeechorSpeed speed;
    String speedInternal;
    SpeechHelper speechHelper;
    MediaPlayer mediaPlayer;
    boolean isSimulatePaused;
    TTSAudioLoader ttsAudioLoader;
    long seekTime;


    public XiaoIceSpeechor() {
        state = SpeechorState.SpeechorStateReady;
        speed = SpeechorSpeed.SPEECH_SPEED_NORMAL;
        speedInternal = "0";
        speechHelper = new SpeechHelper();
        textFragments = new ArrayList<>();
        speechTextFragments = new ArrayList<>();

        //init mediaplay
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this::onMediaFragmentPrepared);
        mediaPlayer.setOnCompletionListener(this::onMediaFragmentComplete);
        mediaPlayer.setOnErrorListener(this::onMediaFragmentError);
        //init xiaoice tts loader
        ttsAudioLoader = new XiaoIceTTSAudioLoader();
        this.reset();
    }


    @Override
    public synchronized void prepare(String text) {
        if (isReleased) {
            return;
        }
        if (this.state != SpeechorState.SpeechorStateReady) {
            this.reset();
        }
        List<String> textFragmentsNew = speechHelper.prepareTextFragments(text, 90, false);
        this.textFragments.addAll(textFragmentsNew);

        for (int i = 0, size = textFragmentsNew.size(); i < size; i++) {
            SpeechTextFragment fragment = new SpeechTextFragment();
            fragment.setFragmentText(textFragmentsNew.get(i));
            fragment.setFragmentState(SpeechTextFragmentState.TextReady);
            fragment.setAudioUrl(null);
            speechTextFragments.add(fragment);
        }
    }


    @Override
    public synchronized int seek(int index) {
        synchronized (this) {
            if (isReleased)
                return -SpeechError.TARGET_IS_RELEASED;

            if (fragmentIndex < 0
                    || fragmentIndex >= this.textFragments.size())
                return -SpeechError.INDEX_OUT_OF_RANGE;

            if (textFragments.size() == 0) {
                return -SpeechError.HAS_NO_FRAGMENTS;
            }

            if(this.state == SpeechorState.SpeechorStatePlaying) {
                mediaPlayer.stop();
            }

            this.fragmentIndex = index;
            this.fragmentIndexNext = fragmentIndex;
            ttsAudioLoader.cancelAll();
            resetFragmentsState(index);
            SpeechTextFragment firstFrgament = this.speechTextFragments.get(index);
            firstFrgament.setFirstFragment(true);
            //动态缓冲区，尽快让播放启动
            seekAndPlay(index, 1);
            new Thread(() -> {
                onStateChanged(SpeechorState.SpeechorStatePlaying);
            }).start();
            return index;
        }
    }


    private synchronized void seekAndPlay(int indexToPlay, int bufferSize) {
        Log.d(TAG, "seekAndPlay:" + indexToPlay);
        seekTime = System.currentTimeMillis();
        int segmentSize = this.textFragments.size();
        for (int startIndex = indexToPlay, endIndex = Math.min(startIndex + bufferSize, segmentSize); startIndex < endIndex; ++startIndex) {
            boolean isSegumentCurrentToPlay = startIndex == this.fragmentIndex;
            SpeechTextFragment fragment = this.speechTextFragments.get(startIndex);
            fragment.setFrameIndex(startIndex);
            fragment.setSeekTime(seekTime);
            switch (fragment.getFragmentState()) {
                case AudioLoadding:
                    //如果准备播放的分片正在加载，设定播放器的状态为Loadding
                    if (isSegumentCurrentToPlay == true) {
                        if(this.state != SpeechorState.SpeechorStateLoadding) {
                            this.state = SpeechorState.SpeechorStateLoadding;
                            new Thread(()->{
                                onStateChanged(SpeechorState.SpeechorStateLoadding);
                            }).start();
                        }
                    }
                    break;
                //如果当前的这片段正在loading，跳过它；

                case Error:
                    if (isSegumentCurrentToPlay == true) {
                        this.state = SpeechorState.SpeechorStateReady;
                        //此时fragmentText装载的是错误信息
                        this.onError(SpeechError.FRAGMENT_TTS_ERROR, fragment.getErrorMessage());
                    }
                    return;

                case TextReady:
                    if (isSegumentCurrentToPlay == true) {
                        if(this.state != SpeechorState.SpeechorStateLoadding) {
                            this.state = SpeechorState.SpeechorStateLoadding;
                            new Thread(()->{
                                onStateChanged(SpeechorState.SpeechorStateLoadding);
                            }).start();
                        }
                    }

                    Log.d(TAG, "loadding fragment audio: loadIndex=" + startIndex + ", frameIndex=" + indexToPlay);
                    TTSAudioLoader.LoadResult loadResult = new IceLoadResult(fragment) {
                        @Override
                        public void invoke(int errorCode, String message, String audioUrl) {
                            synchronized (XiaoIceSpeechor.this) {
                                if (isReleased == true) {
                                    return;
                                }
                                Log.d(TAG, "fragment audio loaded: loadIndex =" + this.fragment.getFrameIndex() + ", indexToPlay=" + indexToPlay + ", errorCode=" + errorCode);
                                if (errorCode == 0) {
                                    boolean isFirstFragment = this.fragment.isFirstFragment();
                                    this.fragment.setFragmentState(SpeechTextFragmentState.AudioReady);
                                    this.fragment.setAudioUrl(audioUrl);
                                    //this.fragment.setFirstFragment(false);
                                    if (XiaoIceSpeechor.this.seekTime != this.fragment.getSeekTime()) {
                                        Log.d(TAG, "load ticken invalided, return;");
                                        return;
                                    }
                                    if (this.fragment.getFrameIndex() == XiaoIceSpeechor.this.fragmentIndex) {
                                        Log.d(TAG, "回来之后状态是:" + state);
                                        if (state == SpeechorState.SpeechorStateLoadding) {
                                            //定性
                                            state = SpeechorState.SpeechorStatePlaying;
                                            //play it;
                                            boolean isPlaying = playSegment(this.fragment.getFrameIndex());
                                            if (isPlaying == true && isFirstFragment == true) {
                                                //此时buffer_size = 1;
                                                //算上index那个，就意味着在当前之后，加载了两个
                                                seekAndPlay(this.fragment.getFrameIndex() + 1, 2);
                                            }
                                        }
                                    }
                                }
                                else {
                                    //加载失败之后的逻辑分之
                                    if (++this.fragment.retryCount > Speechor.ERROR_RETRY_COUNT) {
                                        this.fragment.setFragmentState(SpeechTextFragmentState.Error);
                                        this.fragment.setErrorMessage("网络连接失败，请稍候重试:" + errorCode);
                                        //如果当前播放的主控正在等待当前分片的加载结果，那么反向主动回应
                                        if (this.fragment.getFrameIndex() == XiaoIceSpeechor.this.fragmentIndex) {
                                            //用户在重试等待期间，可能改动了播放主控的操作，如果播放还需要继续，那么就显示错误
                                            if (state == SpeechorState.SpeechorStateLoadding) {
                                                state = SpeechorState.SpeechorStateReady;
                                                onError(SpeechError.FRAGMENT_TTS_ERROR, this.fragment.getErrorMessage());
                                            }
                                        }
                                    }
                                    else {
                                        //state的状态不会等于Error，内部状态没有Error，Error的时候，就是Ready
                                        //也就是说，只要播放器还在Loadding或者Playing，就继续尝试重试
                                        if (state != SpeechorState.SpeechorStateReady) {
                                            ttsAudioLoader.textToSpeech(fragment.getFragmentText(), speed, this);
                                        }
                                    }
                                }
                            } // end synchornized
                        } // end invoke
                    };

                    ttsAudioLoader.textToSpeech(fragment.getFragmentText(), speed, loadResult);
                    fragment.setFragmentState(SpeechTextFragmentState.AudioLoadding);
                    break;

                case AudioReady:
                    if (isSegumentCurrentToPlay == true) {
                        state = SpeechorState.SpeechorStatePlaying;
                        playSegment(fragmentIndex);
                    }
                    break;
            }//end switch
        }
    }


    private boolean playSegment(int segmentIndex) {
        try {
            SpeechTextFragment fragmentToPlay = speechTextFragments.get(segmentIndex);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fragmentToPlay.getAudioUrl());
            mediaPlayer.prepareAsync();
            return true;
        }
        catch (IOException ex) {
            Log.d(TAG, "playSegment error:" + ex.toString());
            onError(SpeechError.FRAGMENT_IO_ERROR, "media player 分片IO错误:" + ex.getMessage());
        }
        catch (NullPointerException ex) {
            SpeechTextFragment fragment = speechTextFragments.get(segmentIndex);
            onError(SpeechError.MEDIA_PLAYER_NULL_ERROR, "NullPointError:" + ex.getMessage() + ",source=" + speechTextFragments.get(segmentIndex).getAudioUrl() + ", text=" + speechTextFragments.get(segmentIndex).getFragmentText());
        }
        return false;
    }

    /*
    media player播放完一个媒体切片后进行回调；
     */
    private synchronized void onMediaFragmentComplete(MediaPlayer player) {
        //Log.d(TAG, "mediaPlay:onComplete:(state=" + state + ", fragmentIndex=" + fragmentIndex + ",size=" + textFragments.size() + ",frgtext=" + textFragments.get(fragmentIndex));
        /*
        在进入fragmentComplete之前枪入下一个分片的播放之前，要判定是否被用户的主控所中断；
        如果当前状态是playing，那继续播放下一个分片
        如果当前状态是paused，说明用户抢入了控制权， 进入假暂停状态
        其他情况，返回退出
         */
        switch (state) {
            case SpeechorStatePlaying:
                break;
            case SpeechorStatePaused:
                //如果被paused掉了，那么设定标志，直接返回
                this.isSimulatePaused = true;
            default:
                return;
        }

        SpeechTextFragment fragment = this.speechTextFragments.get(fragmentIndex);
        //一个切片播放完成之后，下一步的操作：
        //1、判断播放器是否被停止：可能在一个切片播放完成之后，被外部抢入控制权
        //2、当前还存在下一个切片
        ++fragmentIndex;
        int fragmentSize = speechTextFragments.size();

        if (fragmentIndex < fragmentSize) {
            seekAndPlay(fragmentIndex, fragment.isFirstFragment()? 2 : 3);
        }
        else if (fragmentIndex == fragmentSize) {
            state = SpeechorState.SpeechorStateReady;
            fragmentIndex = 0;
            //在这里灵气线程进行事件通知，防止上层回调有锁调用防止死锁；
            new Thread(() -> {
                onStateChanged(SpeechorState.SpeechorStateReady);
            }).start();
        }
    }

    private void resetFragmentsState(int startIndex) {
        for (; startIndex < this.speechTextFragments.size(); startIndex++) {
            SpeechTextFragment fragment = this.speechTextFragments.get(startIndex);
            if (fragment.getFragmentState() == SpeechTextFragmentState.Error) {
                fragment.setFragmentState(SpeechTextFragmentState.TextReady);
                fragment.setFirstFragment(false);
            }
        }
    }


    private void clearCachedFragmentsAudio() {
        for (int index = 0, size = this.speechTextFragments.size(); index < size; ++index) {
            this.speechTextFragments.get(index).setFragmentState(SpeechTextFragmentState.TextReady);
            this.speechTextFragments.get(index).setAudioUrl(null);
            this.speechTextFragments.get(index).setFirstFragment(false);
        }
    }

    /*
    media player完成一个媒体切片加载后进行回调调用
     */
    private synchronized void onMediaFragmentPrepared(MediaPlayer mp) {
        Log.d(TAG, "play fragment at:{" + fragmentIndex + "},duration=" + mp.getDuration());
        if (state == SpeechorState.SpeechorStatePlaying) {
            new Thread(() -> {
                onProgress(textFragments, fragmentIndex);
                mp.start();
            }).start();
        }
    }

    /*
    media player在播放中遇到播放错误后，进行回调调用
     */
    private synchronized boolean onMediaFragmentError(MediaPlayer mp, int what, int extra) {
        new Thread(() -> {
            onError(SpeechError.MEDIA_PLAYER_ERROR, "media player播放错误");
        });
        return false;
    }


    @Override
    public synchronized boolean pause() {
        if (isReleased) {
            return false;
        }
        switch (state) {
            case SpeechorStateLoadding:
                this.isSimulatePaused = true;
            case SpeechorStatePlaying:
                mediaPlayer.pause();
                this.state = SpeechorState.SpeechorStatePaused;
                return true;
        }
        return false;
    }


    @Override
    public synchronized boolean resume() {
        if (isReleased) {
            return false;
        }
        if(this.state != SpeechorState.SpeechorStatePaused) {
            return false;
        }

        if (this.isSimulatePaused == true) {
            this.isSimulatePaused = false;
            seekAndPlay(fragmentIndex, 1);
            //不设定状态，状态由seekAndPlay确定
            return true;
        }
        else {
            int fragSize = speechTextFragments.size();
            if (fragSize > 0 && fragmentIndex < fragSize &&
                    speechTextFragments.get(fragmentIndex).getAudioUrl() != null &&
                    speechTextFragments.get(fragmentIndex).getFragmentState() == SpeechTextFragmentState.AudioReady) {
                mediaPlayer.start();
                this.state = SpeechorState.SpeechorStatePlaying;
                return true;
            }
            else {
                return false;
            }
        }
    }


    @Override
    public synchronized void stop() {
        if (this.isReleased) {
            return;
        }

        if (state != SpeechorState.SpeechorStateReady) {
            state = SpeechorState.SpeechorStateReady;
            mediaPlayer.stop();
        }
    }


    @Override
    public synchronized void reset() {
        if (this.isReleased) {
            return;
        }
        SpeechorState currState = this.state;
        this.state = SpeechorState.SpeechorStateReady;
        this.fragmentIndex = 0;
        this.fragmentIndexNext = 0;
        this.isSimulatePaused = false;

        if (this.textFragments != null) {
            this.textFragments.clear();
        }
        this.textFragments = new ArrayList<>();

        if (this.speechTextFragments != null) {
            this.speechTextFragments.clear();
        }
        this.textFragments = new ArrayList<>();

        if (currState == SpeechorState.SpeechorStatePlaying) {
            mediaPlayer.reset();
            ttsAudioLoader.cancelAll();
            this.onStateChanged(SpeechorState.SpeechorStateReady);
        }
    }

    @Override
    public synchronized void release() {
        if (isReleased) {
            return;
        }

        state = SpeechorState.SpeechorStateReady;
        ttsAudioLoader.cancelAll();

        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnPreparedListener(null);

        if (this.state == SpeechorState.SpeechorStatePlaying) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        isReleased = true;
    }

    @Override
    public void setRole(SpeechorRole role) {
        if (state == SpeechorState.SpeechorStatePlaying || state == SpeechorState.SpeechorStatePaused) {
            mediaPlayer.stop();
            this.state = SpeechorState.SpeechorStateReady;
        }
    }

    @Override
    public synchronized void setSpeed(SpeechorSpeed speed) {
        if (this.speed == speed) {
            return;
        }
        switch (speed) {
            case SPEECH_SPEED_NORMAL:
                speedInternal = "0";
                break;

            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                speedInternal = "50";
                break;

            case SPEECH_SPEED_MULTIPLE_2:
                speedInternal = "100";
                break;

            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                speedInternal = "200";
                break;

            default:
                speedInternal = "0";
                break;
        }

        this.speed = speed;
        ttsAudioLoader.cancelAll();
        clearCachedFragmentsAudio();

        switch (state) {
            case SpeechorStateLoadding:
            case SpeechorStatePlaying:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                seek(fragmentIndex);
                break;

            case SpeechorStatePaused:
                break;
        }
    }

    @Override
    public SpeechorRole getRole() {
        return SpeechorRole.XiaoIce;
    }

    @Override
    public synchronized SpeechorSpeed getSpeed() {
        return speed;
    }

    @Override
    public synchronized SpeechorState getState() {
        return state;
    }

    @Override
    public synchronized int getFragmentIndex() {
        return fragmentIndex;
    }

    @Override
    public void setFragmentIndex(int fragmentIndex) {
        this.fragmentIndex = fragmentIndex;
    }

    @Override
    public synchronized List<String> getTextFragments() {
        return textFragments;
    }

    @Override
    public float getProgress() {
        synchronized (this) {
            if (this.textFragments.size() <= 0)
                return 0f;
            return (float) fragmentIndex / (float) this.textFragments.size();
        }
    }
}
