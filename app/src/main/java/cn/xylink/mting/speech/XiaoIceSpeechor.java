package cn.xylink.mting.speech;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import cn.xylink.mting.speech.data.XiaoIceTTSAudioLoader;

public abstract class XiaoIceSpeechor implements Speechor {

    static String TAG = "SPEECH_";

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
    static int LOADER_QUEUE_SIZE = 2;
    boolean isSimulatePaused;
    TTSAudioLoader ttsAudioLoader;


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
        List<String> textFragmentsNew = speechHelper.prepareTextFragments(text, 100, false);
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
                    || fragmentIndex >= this.textFragments.size()
                    || textFragments.size() == 0)
                return -SpeechError.INDEX_OUT_OF_RANGE;

            switch (this.state) {
                case SpeechorStateLoadding:
                    if (fragmentIndex == index) {
                        return index;
                    }
                    break;
                case SpeechorStatePlaying:
                    if (fragmentIndex == index) {
                        return index;
                    }
                    mediaPlayer.stop();
                    break;
            }

            this.fragmentIndex = index;
            this.fragmentIndexNext = fragmentIndex;
            ttsAudioLoader.cancelAll();
            seekAndPlay(index);
            new Thread(() -> {
                onStateChanged(SpeechorState.SpeechorStatePlaying);
            }).start();

            return index;
        }
    }


    private void seekAndPlay(int frameIndex) {
        int segmentSize = this.textFragments.size();
        for (int startIndex = frameIndex, endIndex = Math.min(startIndex + LOADER_QUEUE_SIZE, segmentSize); startIndex < endIndex; ++startIndex) {
            boolean isSegumentCurrentToPlay = startIndex == frameIndex;
            SpeechTextFragment fragment = this.speechTextFragments.get(startIndex);
            fragment.setFrameIndex(startIndex);
            switch (fragment.getFragmentState()) {
                case AudioLoadding:
                    //如果准备播放的分片正在加载，设定播放器的状态为Loadding
                    if (isSegumentCurrentToPlay == true) {
                        this.state = SpeechorState.SpeechorStateLoadding;
                    }
                    //如果当前的这片段正在loading，跳过它；
                    continue;

                case Error:
                    if (isSegumentCurrentToPlay == true) {
                        this.state = SpeechorState.SpeechorStateReady;
                        //此时fragmentText装载的是错误信息
                        this.onError(SpeechError.FRAGMENT_TTS_ERROR, fragment.getFragmentText());
                    }
                    return;

                case TextReady:
                    if (isSegumentCurrentToPlay == true) {
                        state = SpeechorState.SpeechorStateLoadding;
                    }

                    Log.d(TAG, "fragment loadding: index=" + startIndex + ", frameIndex=" + frameIndex);
                    TTSAudioLoader.LoadResult loadResult = new IceLoadResult(fragment) {
                        @Override
                        public void invoke(int errorCode, String message, String audioUrl) {
                            synchronized (XiaoIceSpeechor.this) {
                                if (isReleased == true) {
                                    return;
                                }
                                Log.d(TAG, "fragment loaded: index =" + this.fragment.getFrameIndex() + ", frameIndex=" + frameIndex + ", errorCode=" + errorCode);
                                if (errorCode == 0) {
                                    this.fragment.setFragmentState(SpeechTextFragmentState.AudioReady);
                                    this.fragment.setAudioUrl(audioUrl);
                                    if (this.fragment.getFrameIndex() == XiaoIceSpeechor.this.fragmentIndex) {
                                        if (state == SpeechorState.SpeechorStateLoadding) {
                                            //定性
                                            state = SpeechorState.SpeechorStatePlaying;
                                            //play it;
                                            playSegment(fragmentIndex);
                                        }
                                    }
                                }
                                else {
                                    //加载失败之后的逻辑分之
                                    if (++this.fragment.retryCount > Speechor.ERROR_RETRY_COUNT) {
                                        this.fragment.setFragmentState(SpeechTextFragmentState.Error);
                                        this.fragment.setFragmentText("分片加载错误:" + message);
                                        //如果当前播放的主控正在等待当前分片的加载结果，那么反向主动回应
                                        if (this.fragment.getFrameIndex() == XiaoIceSpeechor.this.fragmentIndex) {
                                            //用户在重试等待期间，可能改动了播放主控的操作，如果播放还需要继续，那么就显示错误
                                            if (state == SpeechorState.SpeechorStateLoadding) {
                                                state = SpeechorState.SpeechorStateReady;
                                                onError(SpeechError.FRAGMENT_TTS_ERROR, this.fragment.getFragmentText());
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
                    }
                    playSegment(fragmentIndex);
                    break;
            }
        }
    }


    private void playSegment(int segmentIndex) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(speechTextFragments.get(segmentIndex).getAudioUrl());
            mediaPlayer.prepareAsync();
        }
        catch (IOException ex) {
            Log.d(TAG, "playSegment error:" + ex.toString());
            onError(SpeechError.FRAGMENT_IO_ERROR, "media player 分片IO错误:" + ex.getMessage());
        }
        catch (NullPointerException ex) {
            SpeechTextFragment fragment = speechTextFragments.get(segmentIndex);
            onError(SpeechError.MEDIA_PLAYER_NULL_ERROR, "media play播放的Audio为空:" + ex.getMessage());
        }
    }

    /*
    media player播放完一个媒体切片后进行回调；
     */
    private synchronized void onMediaFragmentComplete(MediaPlayer player) {
        Log.d(TAG, "mediaPlay:onComplete:(state=" + state + ", fragmentIndex=" + fragmentIndex + ",size=" + textFragments.size() + ",frgtext=" + textFragments.get(fragmentIndex));
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

        //一个切片播放完成之后，下一步的操作：
        //1、判断播放器是否被停止：可能在一个切片播放完成之后，被外部抢入控制权
        //2、当前还存在下一个切片
        ++fragmentIndex;
        int fragmentSize = speechTextFragments.size();

        if (fragmentIndex < fragmentSize) {
            seekAndPlay(fragmentIndex);
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


    private void clearCachedFragmentsAudio() {
        for (int index = 0, size = this.speechTextFragments.size(); index < size; ++index) {
            this.speechTextFragments.get(index).setFragmentState(SpeechTextFragmentState.TextReady);
            this.speechTextFragments.get(index).setAudioUrl(null);
        }
    }

    /*
    media player完成一个媒体切片加载后进行回调调用
     */
    private synchronized void onMediaFragmentPrepared(MediaPlayer mp) {
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
        if (this.state == SpeechorState.SpeechorStatePaused) {
            if (this.isSimulatePaused == true) {
                this.isSimulatePaused = false;
                seekAndPlay(fragmentIndex);
            }
            else {
                mediaPlayer.start();
            }
            this.state = SpeechorState.SpeechorStatePlaying;
            return true;
        }
        return false;
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
        if(state == SpeechorState.SpeechorStatePlaying || state == SpeechorState.SpeechorStatePaused) {
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
        //设定好速度后，用新速度播放该片段
        if (state == SpeechorState.SpeechorStatePlaying || state == SpeechorState.SpeechorStateLoadding) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            seekAndPlay(fragmentIndex);
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
    public synchronized List<String> getTextFragments() {
        return textFragments;
    }

    @Override
    public synchronized float getProgress() {
        switch (this.state) {
            case SpeechorStateReady:
                return 0f;

            default:
                if (this.textFragments.size() <= 0)
                    return 0f;
                return (float) fragmentIndex / (float) this.textFragments.size();
        }
    }
}
