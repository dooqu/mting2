package cn.xylink.mting.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.song.videoplayer.DemoQSVideoView;
import org.song.videoplayer.IVideoPlayer;
import org.song.videoplayer.PlayListener;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.utils.LogUtils;

/**
 * Created by wjn on 2018/12/29.
 */
public class VideoViewActivity extends BaseActivity {
    @BindView(R.id.video_view)
    DemoQSVideoView video_view;
    @BindView(R.id.iv_break)
    ImageView iv_break;
    @BindView(R.id.iv_voice)
    ImageView iv_voice;
    private boolean isHasVoice;
    public static String ISFINISH = "isFinish";



    @Override
    protected void preView() {
        setContentView(R.layout.activity_video_view);
    }

    @Override
    protected void initView() {
        initVideoView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }


    @OnClick({R.id.iv_voice, R.id.iv_break})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                video_view.release();
//                video_view.quitWindowFullscreen();//退出全屏
                //将播放完成的标识传给闪屏页
                putIntent();
                VideoViewActivity.this.finish();
                break;
            case R.id.iv_voice:
                isHasVoice = !isHasVoice;
                break;
        }
    }

    //添加一个播放完成的回调标识，主要用于闪屏页
    private void putIntent() {
        Intent intent = new Intent();
        intent.putExtra(ISFINISH, "isFinish");
        setResult(Activity.RESULT_OK, intent);
    }

    private void initVideoView() {
        try {
            Class<?> cls = video_view.getClass().getSuperclass();
            Field[] field =  cls.getDeclaredFields();
            for(Field f : field)
            {
                f.setAccessible(true);
               if(f.getName().equals("controlContainer")){
                    ViewGroup v = (ViewGroup) f.get(video_view);
                    v.setVisibility(View.GONE);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String url = "android.resource://" + getPackageName() + "/" + R.raw.mting_640_368;
        video_view.setUp(url, "");
        video_view.setPlayListener(new PlayListener() {
            @Override
            public void onStatus(int status) {
                if (status == IVideoPlayer.STATE_AUTO_COMPLETE) {
                    LogUtils.e("VideoViewActivity", "播放完成,,,,");
                    video_view.quitWindowFullscreen();//播放完成退出全屏
                    putIntent();
                    VideoViewActivity.this.finish();
                } else if (status == IVideoPlayer.EVENT_PLAY) {
                }

            }

            @Override
            public void onMode(int i) {
                video_view.enterWindowFullscreen();//全屏播放
            }

            @Override
            public void onEvent(int i, Integer... integers) {
                LogUtils.e("VideoViewActivity", "设置静音,,,," + isHasVoice);
                if (isHasVoice) {
                    video_view.setMute(false);
//                    iv_voice.setImageResource(R.mipmap.imp_voice_open);
                } else {
                    video_view.setMute(true);
//                    iv_voice.setImageResource(R.mipmap.imp_voice_close);
                }
            }
        });
        video_view.enterFullMode = 3;
        video_view.play();
    }

    @Override
    public void onBackPressed() {
        if (video_view.onBackPressed())
            return;
        super.onBackPressed();
    }
}
