package cn.xylink.mting.speech;

import android.content.Context;
import android.media.MediaPlayer;

import cn.xylink.mting.R;


/*
提供音效播放的类，比如文章播放文成后的提醒音效
内部采用mediaplayer进行轻度包装
 */
public class SoundEffector {
    /*
    音效播放完成后的回调， 采用函数接口形式
     */
    @FunctionalInterface
    public static interface EffectorCompletedCallback {
        void invoke();
    }


    MediaPlayer effector;
    EffectorCompletedCallback switchEffectorCallback;

    public SoundEffector(Context context) {
        effector = MediaPlayer.create(context, R.raw.compl);
        effector.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (switchEffectorCallback != null) {
                    switchEffectorCallback.invoke();
                }
            }
        });
    }


    public void playSwitch(EffectorCompletedCallback callback) {
        switchEffectorCallback = callback;
        effector.start();
    }

    public void release() {
        this.switchEffectorCallback = null;
        if (effector != null) {
            effector.release();
        }
    }
}
