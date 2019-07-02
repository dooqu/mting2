package cn.xylink.mting.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechServiceProxy;
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
                    service.prepare("习近平强调，当前国际形势正在发生巨大变化，中土要坚定维护以联合国为核心、以国际法为基础的国际体系，维护多边主义和国际公平正义，维护以世界贸易组织为核心的多边贸易体制，努力深化两国战略合作关系，捍卫两国和广大发展中国家的共同利益，共同构建相互尊重、公平正义、合作共赢的新型国际关系。双方要在地区事务中保持沟通与协调，共同推动政 治解决有关热点问题，为实现地区和平、稳定、发展作出贡献。");
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
