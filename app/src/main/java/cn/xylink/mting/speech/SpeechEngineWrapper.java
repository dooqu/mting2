package cn.xylink.mting.speech;


import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.List;

import static android.content.Context.AUDIO_SERVICE;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

/*
Speechor 装饰器
 */
public abstract class SpeechEngineWrapper implements Speechor {
    static String TAG = SpeechEngineWrapper.class.getSimpleName();

    Context context;
    Speechor speechor;
    Speechor baiduSpeechor;
    Speechor xiaoiceSpeechor;
    protected AudioManager audioManager;
    boolean isPausedByExternal;


    public SpeechEngineWrapper(Context context) {
        this.context = context;
        initEngine(context);
    }


    protected void initEngine(Context context) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        baiduSpeechor = new BaiduSpeechor(context) {

            @Override
            public void onStateChanged(SpeechorState speakerState) {
                SpeechEngineWrapper.this.onStateChanged(speakerState);
            }

            @Override
            public void onProgress(List<String> textFragments, int index) {
                SpeechEngineWrapper.this.onProgress(textFragments, index);

            }

            @Override
            public void onError(int errorCode, String message) {
                this.stop();
                SpeechEngineWrapper.this.onError(errorCode, message);
            }
        };


        xiaoiceSpeechor = new XiaoIceSpeechor() {
            @Override
            public void onStateChanged(SpeechorState speakerState) {
                SpeechEngineWrapper.this.onStateChanged(speakerState);
            }

            @Override
            public void onProgress(List<String> textFragments, int index) {
                SpeechEngineWrapper.this.onProgress(textFragments, index);

            }

            @Override
            public void onError(int errorCode, String message) {
                this.stop();
                SpeechEngineWrapper.this.onError(errorCode, message);
            }
        };

        speechor = baiduSpeechor;
    }


    AudioManager.OnAudioFocusChangeListener focusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

            synchronized (SpeechEngineWrapper.this) {

                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        //此状态表示，焦点被其他应用获取 AUDIOFOCUS_GAIN 时，触发此回调，需要暂停播放。
                        speechor.pause();
                        isPausedByExternal = true;
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        //短暂性丢失焦点，如播放视频，打电话等，需要暂停播放
                        speechor.pause();
                        isPausedByExternal = true;
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        //短暂性丢失焦点并作降音处理，看需求处理而定。
                        speechor.pause();
                        isPausedByExternal = true;
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        //别的应用申请焦点之后又释放焦点时，就会触发此回调，恢复播放音乐
                        Log.d(TAG, "AUDIOFOCUS_GAIN");
                        if (SpeechEngineWrapper.this.getState() == SpeechorState.SpeechorStatePaused
                                && isPausedByExternal == true) {
                            //这里有个问题， 这期间可能更换了语音包了，导致必须使用seek
                            if (speechor.resume() == false) {
                                //如果resume失败， 重读分片
                                speechor.seek(speechor.getFragmentIndex());
                            }
                        }
                        isPausedByExternal = false;
                        //player.start();
                        break;

                    default:

                        break;
                }
            }
        }
    };


    private boolean requestFocus() {
        return audioManager.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AUDIOFOCUS_REQUEST_GRANTED;
    }

    private boolean abandonFocus() {
        return audioManager.abandonAudioFocus(focusListener) == AUDIOFOCUS_REQUEST_GRANTED;
    }

    @Override
    public void prepare(String text) {
        baiduSpeechor.prepare(text);
        xiaoiceSpeechor.prepare(text);
    }

    @Override
    public int seek(int fragmentIndex) {
        synchronized (this) {
            requestFocus();
            return speechor.seek(fragmentIndex);
        }
    }

    @Override
    public boolean pause() {
        synchronized (this) {
            isPausedByExternal = false;
            boolean result = speechor.pause();
            if (result) {
                abandonFocus();
            }
            return result;
        }
    }

    @Override
    public boolean resume() {
        synchronized (this) {
            boolean result = speechor.resume();
            if (result) {
                requestFocus();
            }
            return result;
        }
    }

    @Override
    public void stop() {
        synchronized (this) {
            abandonFocus();
            speechor.stop();

        }
    }

    @Override
    public void reset() {
        baiduSpeechor.reset();
        xiaoiceSpeechor.reset();
    }

    @Override
    public void release() {
        baiduSpeechor.release();
        xiaoiceSpeechor.release();
    }

    @Override
    public synchronized void setRole(SpeechorRole role) {
        if (speechor.getRole() == role)
            return;

        Speechor destSpeechor = null;

        switch (role) {
            case XiaoMei:
            case YaYa:
            case XiaoYao:
                destSpeechor = baiduSpeechor;
                break;
            case XiaoIce:
                destSpeechor = xiaoiceSpeechor;
                break;
            default:
                return;
        }

        int preIndex = 0;
        SpeechorState preState;
        SpeechorSpeed preSpeed;
        Speechor preSpeechor = speechor;

        synchronized (preSpeechor) {
            preIndex = preSpeechor.getFragmentIndex();
            preState = preSpeechor.getState();
            preSpeed = preSpeechor.getSpeed();

            if (destSpeechor != preSpeechor) {
                synchronized (destSpeechor) {
                    //通过换解决，停掉发音
                    if (preState == SpeechorState.SpeechorStatePlaying) {
                        preSpeechor.stop();
                    }
                    //更换主发音引擎
                    speechor = destSpeechor;
                    //在更换后的 speechor上调用setRole;
                    speechor.setRole(role);
                    //speechor.setSpeed(preSpeed);
                    //如果原来在播放状态，继续播放
                    speechor.setFragmentIndex(preIndex);

                    if(preState == SpeechorState.SpeechorStatePlaying) {
                        speechor.seek(preIndex);
                    }
                }
            }
            else {
                preSpeechor.setRole(role);
                if (preState == SpeechorState.SpeechorStatePlaying) {
                    preSpeechor.seek(preIndex);
                }
            } // end else
        } // end sync
    }

    @Override
    public void setSpeed(SpeechorSpeed speed) {
        baiduSpeechor.setSpeed(speed);
        xiaoiceSpeechor.setSpeed(speed);
    }

    @Override
    public SpeechorRole getRole() {
        synchronized (this) {
            return speechor.getRole();
        }
    }

    @Override
    public SpeechorSpeed getSpeed() {
        synchronized (this) {
            return speechor.getSpeed();
        }
    }

    @Override
    public SpeechorState getState() {
        synchronized (this) {
            return speechor.getState();
        }
    }

    @Override
    public int getFragmentIndex() {
        synchronized (this) {
            return speechor.getFragmentIndex();
        }
    }

    @Override
    public void setFragmentIndex(int frameIndex) {

    }

    @Override
    public List<String> getTextFragments() {
        synchronized (this) {
            return speechor.getTextFragments();
        }
    }

    @Override
    public float getProgress() {
        synchronized (this) {
            return speechor.getProgress();
        }
    }
}
