package cn.xylink.mting.speech;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaiduSpeechor implements Speechor {
    static String TAG = "SPEECH_";
    List<String> textFragments;
    SpeechHelper speechOperator;
    SpeechorState state;
    SpeechorRole role;
    SpeechorSpeed speed;
    protected int fragmentIndex;
    protected int fragmentIndexNext;
    boolean isReleased;
    SpeechSynthesizer speechSynthesizer;
    Map<Integer, Integer> fragmentErrorMap;

    public BaiduSpeechor(Context context) {
        state = SpeechorState.SpeechorStateReady;
        this.initSpeechor(context);
        this.setRole(SpeechorRole.XiaoMei);
        this.setSpeed(SpeechorSpeed.SPEECH_SPEED_NORMAL);
        this.reset();
    }

    /*
    对百度的SpeechSynthesizer进行初始化
     */
    protected void initSpeechor(Context context) {

        final BaiduSpeechor self = this;
        isReleased = false;
        fragmentErrorMap = new HashMap<>();
        textFragments = new ArrayList<>();
        speechOperator = new SpeechHelper();
        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(context);
        speechSynthesizer.setAppId("16690943");
        speechSynthesizer.setApiKey("yl2y2gxuV2sVcGTyz1Sl3FyN", "N8kYSQnucGTBt7uNI7Stdui6eBoezZWW");
        speechSynthesizer.auth(TtsMode.ONLINE);
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        speechSynthesizer.initTts(TtsMode.ONLINE);

        speechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
                Log.d(TAG, "onSynthesizeStart:" + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.d(TAG, "onSynthesizeFinish:" + s);
            }

            /*
            该方法调用的时机是在每一个分片被调用的时候，所以要依靠多次的
            onSpeechStart事件，来累计分片索引，计算进度
             */
            @Override
            public void onSpeechStart(String s) {
                Log.d(TAG, "onSpeechStart:" + s);
                synchronized (self) {
                    self.fragmentIndex = self.fragmentIndexNext;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            self.onProgress(self.textFragments, self.fragmentIndex);
                        }
                    }).start();
                }
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
            }

            @Override
            public void onSpeechFinish(final String s) {
                //System.out.println("onSpeechFinish:" + s + ",");
                synchronized (self) {
                    //System.out.println("onSpeechFinish:" + self);
                    ++self.fragmentIndexNext;
                    if (self.fragmentIndexNext == self.textFragments.size()) {
                        self.fragmentIndexNext = 0;
                        self.fragmentIndex = 0;
                        self.state = SpeechorState.SpeechorStateReady;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // System.out.println("onStateChange:" + self);
                                self.onStateChanged(SpeechorState.SpeechorStateReady);
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                synchronized (self) {
                    if (isReleased) {
                        return;
                    }
                    int currentFragmentRetryCount = fragmentErrorMap.containsKey(fragmentIndex) ? fragmentErrorMap.get(fragmentIndex) : 0;
                    //发生错误后，回来要看一下当前的播放状态
                    if (state == SpeechorState.SpeechorStatePlaying && ++currentFragmentRetryCount <= Speechor.ERROR_RETRY_COUNT) {
                        Log.d(TAG, "BaiduSpeechor.onError:" + speechError.description + ", retrycount=" + currentFragmentRetryCount);
                        fragmentErrorMap.put(fragmentIndex, currentFragmentRetryCount);
                        seekAndPlay(fragmentIndex);
                    }
                    else {
                        self.onError(speechError.code, speechError.description);
                    }
                }
            }
        });
    }


    @Override
    public void prepare(String text) {
        synchronized (this) {
            if (isReleased)
                return;

            if (this.state != SpeechorState.SpeechorStateReady) {
                this.reset();
            }
            List<String> textFragmentsNew = speechOperator.prepareTextFragments(text, 100, false);
            this.textFragments.addAll(textFragmentsNew);
        }
    }


    /*
    seek 传入参数是浮点数， 是进度的百分比；
    返回-2，说明Speechor对象还未prepare；
    返回-1，说明索引错误
     */
    @Override
    public synchronized int seek(int index) {

        if (isReleased)
            return -cn.xylink.mting.speech.SpeechError.TARGET_IS_RELEASED;

        if (index < 0 || index >= this.textFragments.size())
            return -cn.xylink.mting.speech.SpeechError.INDEX_OUT_OF_RANGE;

        if (state == SpeechorState.SpeechorStatePlaying) {
            if (index == fragmentIndex) {
                return index;
            }
            else {
                speechSynthesizer.stop();
            }
        }

        this.fragmentIndex = index;
        this.fragmentIndexNext = fragmentIndex;
        this.onStateChanged(SpeechorState.SpeechorStatePlaying);
        return seekAndPlay(fragmentIndex);
    }


    private int seekAndPlay(int frameIndex) {

        state = SpeechorState.SpeechorStatePlaying;
        for (int currentIndex = frameIndex, fragmentsSize = this.textFragments.size();
             currentIndex < fragmentsSize; ++currentIndex) {
            speechSynthesizer.speak(this.textFragments.get(currentIndex), String.valueOf(currentIndex));
        }
        return frameIndex;
    }

    @Override
    public synchronized void setRole(SpeechorRole roleSet) {

        if (isReleased)
            return;

        String paramRole = "0";

        switch (roleSet) {
            case XiaoMei:
                role = roleSet;
                paramRole = "0";
                break;

            case XiaoYao:
                role = roleSet;
                paramRole = "3";
                break;

            case YaYa:
                roleSet = roleSet;
                paramRole = "4";
                break;

            default:
                roleSet = SpeechorRole.XiaoMei;
                break;
        }

        this.role = roleSet;
        //重新设定语音参数
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, paramRole);

        synchronized (this) {
            if (this.state != SpeechorState.SpeechorStateReady) {
                //获取当前的frameIndex，后续继续断点续播
                //int currFrameIndex = this.fragmentIndex;
                //将播放停止
                speechSynthesizer.stop();
                this.state = SpeechorState.SpeechorStateReady;
            }
        }
    }


    @Override
    public synchronized void setSpeed(SpeechorSpeed speed) {
        if (isReleased)
            return;

        String paramSpeed = "5";
        switch (speed) {
            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                paramSpeed = "7";
                break;
            case SPEECH_SPEED_MULTIPLE_2:
                paramSpeed = "12";
                break;
            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                paramSpeed = "15";
                break;
            default:
                paramSpeed = "5";
                break;
        }

        this.speed = speed;
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, paramSpeed);
        if (this.state == SpeechorState.SpeechorStatePlaying) {
            this.stop();
            this.seekAndPlay(fragmentIndex);
        }
    }


    @Override
    public SpeechorSpeed getSpeed() {
        synchronized (this) {
            return this.speed;
        }
    }

    @Override
    public SpeechorRole getRole() {
        synchronized (this) {
            return this.role;
        }
    }

    @Override
    public SpeechorState getState() {
        synchronized (this) {
            return this.state;
        }
    }

    @Override
    public int getFragmentIndex() {
        synchronized (this) {
            return this.fragmentIndex;
        }
    }

    @Override
    public List<String> getTextFragments() {
        return this.textFragments;
    }

    @Override
    public float getProgress() {
        synchronized (this) {
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

    @Override
    public boolean pause() {
        synchronized (this) {
            if (isReleased) {
                return false;
            }

            if (state == SpeechorState.SpeechorStatePlaying) {
                if (speechSynthesizer.pause() == 0) {
                    this.state = SpeechorState.SpeechorStatePaused;
                    this.onStateChanged(SpeechorState.SpeechorStatePaused);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean resume() {

        synchronized (this) {
            if (isReleased) {
                return false;
            }

            if (state == SpeechorState.SpeechorStatePaused) {
                if (speechSynthesizer.resume() == 0) {
                    state = SpeechorState.SpeechorStatePlaying;
                    this.onStateChanged(SpeechorState.SpeechorStatePlaying);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void stop() {
        synchronized (this) {
            if (isReleased)
                return;

            if (state != SpeechorState.SpeechorStateReady) {
                state = SpeechorState.SpeechorStateReady;
                speechSynthesizer.stop();
            }
        }
    }

    @Override
    public void reset() {
        synchronized (this) {

            SpeechorState currState = this.state;
            this.state = SpeechorState.SpeechorStateReady;
            this.fragmentIndex = 0;
            this.fragmentIndexNext = 0;
            this.textFragments.clear();
            this.textFragments.clear();
            this.fragmentErrorMap.clear();

            if (currState == SpeechorState.SpeechorStatePlaying) {
                this.speechSynthesizer.stop();
                this.onStateChanged(SpeechorState.SpeechorStateReady);
            }
        }
    }

    @Override
    public void release() {
        if(isReleased) {
            return;
        }
        synchronized (this) {
            if (isReleased)
                return;

            if (this.state != SpeechorState.SpeechorStateReady) {
                this.reset();
            }
            this.speechSynthesizer.setSpeechSynthesizerListener(null);
            this.speechSynthesizer.release();
            isReleased = true;
        }
    }
}
