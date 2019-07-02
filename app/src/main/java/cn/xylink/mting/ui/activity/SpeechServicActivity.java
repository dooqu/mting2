package cn.xylink.mting.ui.activity;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.event.SpeechStateChangedEvent;

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
                    service.setArticle(((MTing)getApplication()).articlesToRead.get(0));
                    service.seek(0);
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
    protected void onSpeechServiceState(SpeechStateChangedEvent event)
    {
        System.out.println(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onSpeechServiceProgress(SpeechProgressEvent event)
    {
        System.out.println(event);
    }

    @Override
    protected void initTitleBar() {
        titleBar.getLeftImageButton().setImageResource(R.mipmap.ic_launcher);
        titleBar.getRightTextView().setText("反馈");
    }
}
