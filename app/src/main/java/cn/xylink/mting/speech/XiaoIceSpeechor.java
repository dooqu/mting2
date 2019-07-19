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
    SpeechHelper speechHelper;
    MediaPlayer mediaPlayer;
    static int LOADER_QUEUE_SIZE = 5;


    public XiaoIceSpeechor() {
        state = SpeechorState.SpeechorStateReady;
        speechHelper = new SpeechHelper();
        textFragments = new ArrayList<>();
        speechTextFragments = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
        Log.i("xylink", "xiaoIce.seekAndPlay:" + frameIndex);
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
                    loader.textToSpeech(fragment.getFragmentText(), new LoaderResult(fragment) {
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
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    synchronized (XiaoIceSpeechor.this) {
                        if (state == SpeechorState.SpeechorStatePlaying) {
                            new Thread(() -> {
                                onProgress(textFragments, fragmentIndex);
                                mp.start();
                            }).start();
                        }
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    synchronized (XiaoIceSpeechor.this) {
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
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });

            mediaPlayer.setDataSource(speechTextFragments.get(segmentIndex).getAudioUrl());
            mediaPlayer.prepareAsync();
        }
        catch (IOException ex) {
            onError(-1, ex.getMessage());
        }
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

        if (currState != SpeechorState.SpeechorStateReady) {
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnPreparedListener(null);
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
        if (this.state == SpeechorState.SpeechorStatePlaying) {
            mediaPlayer.stop();
        }

        XiaoIceTTSAudioLoader.cancel();
        mediaPlayer.release();
    }

    @Override
    public void setRole(SpeechorRole role) {
        //nothing to do
    }

    @Override
    public void setSpeed(SpeechorSpeed speed) {

    }

    @Override
    public SpeechorRole getRole() {
        return SpeechorRole.XiaoIce;
    }

    @Override
    public SpeechorSpeed getSpeed() {
        return null;
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
        return 0;
    }
}
