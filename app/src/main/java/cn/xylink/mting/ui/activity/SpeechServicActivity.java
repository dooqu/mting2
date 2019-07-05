package cn.xylink.mting.ui.activity;

import android.util.Log;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class SpeechServicActivity extends BaseActivity {


    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    SpeechServiceProxy proxy;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service)
            {
                if(connected)
                {
                    service.selectAndPlay("1");
                }
            }
        };
        EventBus.getDefault().register(this);
        proxy.bind();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        proxy.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onSpeechStart(SpeechStartEvent event)
    {
        Log.d("xylink", "onSpeechStart:" + event.getArticle().getTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onSpeechProgress(SpeechProgressEvent event)
    {
        Log.d("xylink", "onSpeechProgress: " + event.getArticle().getTitle() + "," + event.getTextFragments().get(event.getFrameIndex()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onSpeechStop(SpeechStopEvent event)
    {
        Log.d("xylink", "onSpeechStop");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onSpeechError(SpeechErrorEvent event)
    {
    }

    @Override
    protected void initTitleBar() {
        titleBar.getLeftImageButton().setImageResource(R.mipmap.ic_launcher);
        titleBar.getRightTextView().setText("反馈");
    }
}
