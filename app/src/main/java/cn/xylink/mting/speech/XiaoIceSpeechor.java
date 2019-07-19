package cn.xylink.mting.speech;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import cn.xylink.mting.speech.data.XiaoIceTTSAudioLoader;

public abstract class XiaoIceSpeechor implements Speechor {

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
    }


    public abstract class LoaderResult implements XiaoIceTTSAudioLoader.LoadResult {
        SpeechTextFragment fragment;

        public LoaderResult(SpeechTextFragment fragment) {
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
    static int LOADER_QUEUE_SIZE = 5;


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
        List<String> textFragmentsNew = speechHelper.prepareTextFragments(text, false);
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
                return -3;

            if (fragmentIndex < 0
                    || fragmentIndex >= this.textFragments.size()
                    || textFragments.size() == 0)
                return -2;

            if (this.state == SpeechorState.SpeechorStateLoadding && this.fragmentIndex == index) {
                return index;
            }

            this.fragmentIndex = index;
            this.fragmentIndexNext = fragmentIndex;

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

            switch (fragment.getFragmentState()) {
                case AudioLoadding:
                    //如果当前的这片段正在loading，跳过它；
                    if (isSegumentCurrentToPlay == true) {
                        this.state = SpeechorState.SpeechorStateLoadding;
                    }
                    continue;

                case Error:
                    if (isSegumentCurrentToPlay == true) {
                        this.state = SpeechorState.SpeechorStateReady;
                        this.onError(-100, "加载错误");
                    }
                    return;

                case TextReady:
                    if (isSegumentCurrentToPlay == true) {
                        state = SpeechorState.SpeechorStateLoadding;
                    }

                    XiaoIceTTSAudioLoader loader = new XiaoIceTTSAudioLoader();
                    loader.textToSpeech(fragment.getFragmentText(), speed, new LoaderResult(fragment) {
                        @Override
                        public void invoke(int errorCode, String message, String audioUrl) {
                            synchronized (XiaoIceSpeechor.this) {
                                if (errorCode == 0) {
                                    this.fragment.setFragmentState(SpeechTextFragmentState.AudioReady);
                                    this.fragment.setAudioUrl(audioUrl);

                                    if (this.fragment == XiaoIceSpeechor.this.speechTextFragments.get(fragmentIndex)) {
                                        if (state == SpeechorState.SpeechorStateLoadding) {
                                            //定性
                                            state = SpeechorState.SpeechorStatePlaying;
                                            //play it;
                                            playSegment(fragmentIndex);
                                        }
                                    }
                                }
                                else {
                                    this.fragment.setFragmentState(SpeechTextFragmentState.Error);
                                    this.fragment.setFragmentText(message);
                                }
                            } // end synchornized
                        } // end invoke
                    });
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
            onError(-1, ex.getMessage());
        }
        catch (NullPointerException ex) {
            SpeechTextFragment fragment = speechTextFragments.get(segmentIndex);
            onError(-100, "播放的Audio为空");
        }
    }

    /*
    media player播放完一个媒体切片后进行回调；
     */
    private synchronized void onMediaFragmentComplete(MediaPlayer player) {
        Log.d("SPEECH", "mediaPlay:onComplete:(state=" + state + ", fragmentIndex=" + fragmentIndex + ",size=" + textFragments.size() + ",frgtext=" + textFragments.get(fragmentIndex));
        //一个切片播放完成之后，下一步的操作：
        //1、判断播放器是否被停止：可能在一个切片播放完成之后，被外部抢入控制权
        //2、当前还存在下一个切片
        if (state != SpeechorState.SpeechorStatePlaying) {
            return;
        }

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
        for (int index = 0, size = this.textFragments.size(); index < size; ++index) {
            this.speechTextFragments.get(index).setFragmentState(SpeechTextFragmentState.TextReady);
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
            onError(what, "media player播放错误");
        });
        return false;
    }


    @Override
    public synchronized boolean pause() {
        if (isReleased) {
            return false;
        }
        if (this.state == SpeechorState.SpeechorStatePlaying || this.state == SpeechorState.SpeechorStateLoadding) {
            if (this.state == SpeechorState.SpeechorStatePlaying && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
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
            mediaPlayer.start();
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
            XiaoIceTTSAudioLoader.cancel();
            this.onStateChanged(SpeechorState.SpeechorStateReady);
        }
    }

    @Override
    public synchronized void release() {
        if (isReleased) {
            return;
        }

        XiaoIceTTSAudioLoader.cancel();

        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnPreparedListener(null);

        if (this.state == SpeechorState.SpeechorStatePlaying) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    @Override
    public void setRole(SpeechorRole role) {
        //nothing to do
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
        clearCachedFragmentsAudio();
        //设定好速度后，用新速度播放该片段
        if (state == SpeechorState.SpeechorStatePlaying) {
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
