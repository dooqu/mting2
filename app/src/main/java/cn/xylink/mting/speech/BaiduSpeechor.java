package cn.xylink.mting.speech;

import android.content.Context;
import android.media.AudioManager;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.util.ArrayList;
import java.util.List;

public abstract class BaiduSpeechor implements Speechor {
    List<String> textFragments;
    SpeechHelper speechOperator;
    SpeechorState state;
    SpeechorRole role;
    SpeechorSpeed speed;
    protected int fragmentIndex;
    protected int fragmentIndexNext;
    boolean isReleased;
    SpeechSynthesizer speechSynthesizer;

    // SpeakProgressListener progressListener;
    //StateChangedListener stateListener;
    // ErrorListener errorListener;

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
        speechOperator = new SpeechHelper();
        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(context);
        speechSynthesizer.setAppId("11676777"/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        speechSynthesizer.setApiKey("aRazCPQBXok8zEt0yZUNqTGV", "c4qpC1f4dKExrhjQvU4FW7iICfVjvQFP");
        speechSynthesizer.auth(TtsMode.ONLINE);
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        speechSynthesizer.initTts(TtsMode.MIX);


        speechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
            }

            @Override
            public void onSynthesizeFinish(String s) {
            }

            @Override
            public void onSpeechStart(String s) {
                //System.out.println("onSpeechStart:" + s);
                synchronized (self) {
                    self.fragmentIndex = self.fragmentIndexNext;

                    System.out.println(self.getRole() + "设定frameIndex=" + self.fragmentIndex);
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
                //System.out.println("onSpeechProgressChanged:" + s + "," + i);
            }

            @Override
            public void onSpeechFinish(final String s) {
                //System.out.println("onSpeechFinish:" + s + ",");
                synchronized (self) {

                    System.out.println("onSpeechFinish:" + self);
                    ++self.fragmentIndexNext;
                    if (self.fragmentIndexNext == self.textFragments.size()) {
                        self.fragmentIndexNext = 0;
                        self.fragmentIndex = 0;
                        self.state = SpeechorState.SpeechorStateReady;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("onStateChange:" + self);
                                self.onStateChanged(SpeechorState.SpeechorStateReady);
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                self.onError(speechError.code, speechError.description);
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
            List<String> textFragmentsNew = speechOperator.prepare(text);
            this.textFragments.addAll(textFragmentsNew);
        }
    }


    /*
    seek 传入参数是浮点数， 是进度的百分比；
    返回-2，说明Speechor对象还未prepare；
    返回-1，说明索引错误
     */
    @Override
    public int seek(int fragmentIndex) {
        synchronized (this) {
            if (isReleased)
                return -3;

            if (fragmentIndex < 0 || fragmentIndex >= this.textFragments.size())
                return -2;

            speechSynthesizer.stop();

            this.fragmentIndex = fragmentIndex;
            this.fragmentIndexNext = fragmentIndex;
            this.onStateChanged(SpeechorState.SpeechorStatePlaying);

            return seekAndPlay(fragmentIndex);
        }
    }


    private int seekAndPlay(int frameIndex) {
        if (frameIndex >= this.textFragments.size())
            return -2;

        if (this.textFragments.size() == 0)
            return -2;

        state = SpeechorState.SpeechorStatePlaying;
        for (int currentIndex = frameIndex, fragmentsSize = this.textFragments.size();
             currentIndex < fragmentsSize; ++currentIndex) {
            speechSynthesizer.speak(this.textFragments.get(currentIndex));
        }

        return frameIndex;
    }

    @Override
    public void setRole(SpeechorRole roleSet) {

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
    public void setSpeed(SpeechorSpeed speed) {

        if (isReleased)
            return;

        String paramSpeed = "5";
        switch (speed) {
            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                paramSpeed = "8";
                break;
            case SPEECH_SPEED_MULTIPLE_2:
                paramSpeed = "10";
                break;
            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                paramSpeed = "12";
                break;
            default:
                paramSpeed = "5";
                break;
        }

        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, paramSpeed);
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
    public List<String> getTextFragments()
    {
        return this.textFragments;
    }

    @Override
    public float getProgress()
    {
        synchronized (this)
        {
            switch (this.state)
            {
                case SpeechorStateReady:
                    return 0f;

                default:
                    if(this.textFragments.size() <= 0)
                        return 0f;
                    return (float)fragmentIndex / (float)this.textFragments.size();
            }
        }
    }

    @Override
    public boolean pause() {
        synchronized (this) {
            if (isReleased)
                return false;

            if (state == SpeechorState.SpeechorStatePlaying) {
                speechSynthesizer.pause();
                this.state = SpeechorState.SpeechorStatePaused;
                this.onStateChanged(SpeechorState.SpeechorStatePaused);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean resume() {
        synchronized (this) {
            if (isReleased)
                return false;

            if (state == SpeechorState.SpeechorStatePaused) {
                speechSynthesizer.resume();
                state = SpeechorState.SpeechorStatePlaying;
                this.onStateChanged(SpeechorState.SpeechorStatePlaying);
                return true;
            }

            return false;
        }
    }

    @Override
    public void stop()
    {
        synchronized (this)
        {
            if(isReleased)
                return;

            if(state != SpeechorState.SpeechorStateReady)
            {
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

            if (this.textFragments != null) {
                this.textFragments.clear();
            }
            this.textFragments = new ArrayList<>();

            if (currState != SpeechorState.SpeechorStateReady) {
                this.speechSynthesizer.stop();
                this.onStateChanged(SpeechorState.SpeechorStateReady);
            }
        }
    }

    @Override
    public void release() {
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