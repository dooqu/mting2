package cn.xylink.mting.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.event.RecycleEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.ui.dialog.ArticleDetailFont;
import cn.xylink.mting.ui.dialog.ArticleDetailSetting;
import cn.xylink.mting.ui.dialog.ArticleDetailShare;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class ArticleDetailActivity extends BaseActivity {


    private ArticleDetailSetting mArticleDetailSetting;
    private ArticleDetailFont mArticleDetailFont;
    private ArticleDetailShare mArticleDetailShare;
    private SpeechService service;
    private SpeechServiceProxy proxy;
    @BindView(R.id.tv_content)
    TextView tvContent;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail);
    }

    private void initServiceData() {
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {
    }

    @OnClick({R.id.ll_setting, R.id.iv_setting, R.id.tv_setting})
    void onSettingClick(View v) {
        if (mArticleDetailSetting == null) {
            mArticleDetailSetting = new ArticleDetailSetting();
        }
        mArticleDetailSetting.showDialog(this);
    }

    @OnClick({R.id.ll_font, R.id.iv_font, R.id.tv_font})
    void onFontClick(View v) {
        if (mArticleDetailFont == null) {
            mArticleDetailFont = new ArticleDetailFont();
        }
        mArticleDetailFont.showDialog(this);
    }

    @OnClick({R.id.ll_share, R.id.iv_share, R.id.tv_share})
    void onShareClick(View v) {
        if (mArticleDetailShare == null) {
            mArticleDetailShare = new ArticleDetailShare();
        }
        mArticleDetailShare.showDialog(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(RecycleEvent event) {
        if (event instanceof SpeechStartEvent) {
            SpeechStartEvent sse = (SpeechStartEvent) event;
            tvContent.setText(event.getArticle().getTitle());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service) {
                if (connected) {
                    ArticleDetailActivity.this.service = service;
                    initServiceData();
                }
            }
        };
        proxy.bind();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册
        EventBus.getDefault().unregister(this);
        //断开服务
        proxy.unbind();
    }
}
