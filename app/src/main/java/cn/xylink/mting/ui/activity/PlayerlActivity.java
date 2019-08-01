package cn.xylink.mting.ui.activity;

import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.utils.L;

public class PlayerlActivity extends BaseActivity {

//    public static final String EXTRA_HTML = "html_url";
    public final String PROTOCOL_URL = "http://test.xylink.cn/article/html/tutorial.html";

    @BindView(R.id.wv_html)
    WebView wvHtml;
    @BindView(R.id.pb_speech_bar)
    ProgressBar progressBar;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_player);
    }

    @Override
    protected void initView() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            wvHtml.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        WebSettings mWebSettings = wvHtml.getSettings();
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true
        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebSettings.setAppCacheEnabled(true);//是否使用缓存
        mWebSettings.setDomStorageEnabled(true);//开启本地DOM存储
        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放


        wvHtml.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                L.v("newProgress", newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if(newProgress == 100)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initData() {
//        String url = getIntent().getStringExtra(EXTRA_HTML);
//        L.v(url);
        wvHtml.loadUrl(PROTOCOL_URL);
    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick(R.id.iv_close)
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_close:
                finish();
                break;
        }
    }
}
