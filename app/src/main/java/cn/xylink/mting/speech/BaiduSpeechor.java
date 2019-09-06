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

import cn.xylink.mting.BuildConfig;

import static cn.xylink.mting.speech.SpeechError.FRAGMENT_LOAD_INNTERNAL_ERROR;

public abstract class BaiduSpeechor implements Speechor {
    static String TAG = BaiduSpeechor.class.getSimpleName();
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
                //Log.d(TAG, "onSynthesizeStart:" + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
            }

            @Override
            public void onSynthesizeFinish(String s) {
                //Log.d(TAG, "onSynthesizeFinish:" + s);
            }

            /*
            该方法调用的时机是在每一个分片被调用的时候，所以要依靠多次的
            onSpeechStart事件，来累计分片索引，计算进度
             */
            @Override
            public void onSpeechStart(String s) {
                BaiduSpeechor self = BaiduSpeechor.this;
                synchronized (self) {
                    self.fragmentIndex = self.fragmentIndexNext;
                    Log.d(TAG, "onSpeechStart: s=" + s + ",fragmentIndex=" + self.fragmentIndex);
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
                BaiduSpeechor self = BaiduSpeechor.this;
                //System.out.println("onSpeechFinish:" + s + ",");
                synchronized (self) {
                    int finishIndex = Integer.parseInt(s);
                    Log.d(TAG, "onSpeechFinish:s=" + s + ",fragIndexNext=" + String.valueOf(self.fragmentIndexNext + 1));
                    //System.out.println("onSpeechFinish:" + self);
                    self.fragmentIndexNext = finishIndex + 1;
                    if (self.fragmentIndexNext >= self.textFragments.size()) {
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

                    else if (fragmentErrorMap.containsKey(self.fragmentIndexNext)) {
                        Log.d(TAG, "当前帧:" + self.fragmentIndexNext + "侦测到错误,之前的重试次数是:" + fragmentErrorMap.get(self.fragmentIndexNext));
                        //如果下一篇文章有出错的标识
                        int errorRetyCount = fragmentErrorMap.get(self.fragmentIndexNext) + 1;
                        if (errorRetyCount <= Speechor.ERROR_RETRY_COUNT) {
                            fragmentErrorMap.put(self.fragmentIndexNext, errorRetyCount);
                            seekAndPlay(self.fragmentIndexNext);
                        }
                        else {
                            self.onError(cn.xylink.mting.speech.SpeechError.FRAGMENT_LOAD_INNTERNAL_ERROR, BuildConfig.DEBUG? "语音分片加载错误:" + cn.xylink.mting.speech.SpeechError.FRAGMENT_LOAD_INNTERNAL_ERROR  : "语音缓冲错误");
                        }
                    }
                }
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                synchronized (self) {
                    if (isReleased) {
                        return;
                    }

                    int errorFrameIndex = Integer.parseInt(s);
                    int currentFragmentRetryCount = fragmentErrorMap.containsKey(errorFrameIndex) ? fragmentErrorMap.get(errorFrameIndex) : 0;

                    if (fragmentIndex == errorFrameIndex) {
                        if ((currentFragmentRetryCount + 1) <= Speechor.ERROR_RETRY_COUNT) {
                            Log.d(TAG, "BaiduSpeechor.onError:s=" + s + "frameIndex=" + fragmentIndex + ",error=" + speechError.description + ", retrycount=" + currentFragmentRetryCount);
                            fragmentErrorMap.put(errorFrameIndex, ++currentFragmentRetryCount);
                            seekAndPlay(errorFrameIndex);
                        }
                        else {
                            self.onError(cn.xylink.mting.speech.SpeechError.FRAGMENT_LOAD_INNTERNAL_ERROR, BuildConfig.DEBUG? "语音分片内部加载错误:" + speechError.description : "音频缓冲错误");
                        }
                    }
                    else {
                        Log.d(TAG, "在加载第" + errorFrameIndex + "贞时出现错误，当前frameindex=:" + fragmentIndex + "贞， retrycount=" + currentFragmentRetryCount + "，加入错误重试队列");
                        fragmentErrorMap.put(errorFrameIndex, currentFragmentRetryCount);
                    }
                }
            }
        });
    }


    @Override
    public void prepare(String text) {
        synchronized (this) {
            if (isReleased) {
                return;
            }
            if (this.state != SpeechorState.SpeechorStateReady) {
                this.reset();
            }
            List<String> textFragmentsNew = speechOperator.prepareTextFragments(text, 90, false);
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
        if (isReleased) {
            return -cn.xylink.mting.speech.SpeechError.TARGET_IS_RELEASED;
        }

        if(this.textFragments.size() <= 0) {
            return  -cn.xylink.mting.speech.SpeechError.HAS_NO_FRAGMENTS;
        }

        if (index < 0 || index >= this.textFragments.size()) {
            return -cn.xylink.mting.speech.SpeechError.INDEX_OUT_OF_RANGE;
        }

        this.onStateChanged(SpeechorState.SpeechorStatePlaying);
        return seekAndPlay(index);
    }


    private int seekAndPlay(int index) {
        speechSynthesizer.stop();
        this.fragmentIndex = index;
        this.fragmentIndexNext = fragmentIndex;
        state = SpeechorState.SpeechorStatePlaying;
        for (int currentIndex = index, fragmentsSize = this.textFragments.size();
             currentIndex < fragmentsSize; ++currentIndex) {
            speechSynthesizer.speak(this.textFragments.get(currentIndex), String.valueOf(currentIndex));
        }
        return index;
    }

    @Override
    public synchronized void setRole(SpeechorRole roleSet) {
        if (isReleased) {
            return;
        }
        String paramRole = "0";

        switch (roleSet) {
            case XiaoMei:
                role = roleSet;
                paramRole = "0";
                break;

            case XiaoYu:
                role = roleSet;
                paramRole = "1";
                break;

            case XiaoYao:
                role = roleSet;
                paramRole = "3";
                break;

            case YaYa:
                role = roleSet;
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
        if (isReleased) {
            return;
        }
        String paramSpeed;
        switch (speed) {
            case SPEECH_SPEED_HALF:
                paramSpeed = "5";
                break;
            case SPEECH_SPEED_NORMAL:
                paramSpeed = "7";
                break;
            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                paramSpeed = "9";
                break;
            case SPEECH_SPEED_MULTIPLE_2:
                paramSpeed = "13";
                break;
            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                paramSpeed = "15";
                break;
            default:
                paramSpeed = "7";
                break;
        }

        this.speed = speed;
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, paramSpeed);
        //百度的引擎没有用到Loadding状态
        if (this.state == SpeechorState.SpeechorStatePlaying) {
            this.stop();
            this.seekAndPlay(fragmentIndex);
        }
        else if (this.state == SpeechorState.SpeechorStatePaused) {
            this.stop();
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
    public void setFragmentIndex(int fragmentIndex) {
        this.fragmentIndex = fragmentIndex;
    }

    @Override
    public List<String> getTextFragments() {
        return this.textFragments;
    }

    @Override
    public float getProgress() {
        synchronized (this) {
            if (this.textFragments.size() <= 0)
                return 0f;
            return (float) fragmentIndex / (float) this.textFragments.size();
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
            if (isReleased) {
                return;
            }
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
        if (isReleased) {
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
