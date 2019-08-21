package cn.xylink.mting.ui.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tendcloud.tenddata.TCAgent;

import java.lang.reflect.Method;

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
    @BindView(R.id.fl_web)
    FrameLayout flWeb;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;


    /** 视频全屏参数 */
    protected  FrameLayout.LayoutParams COVER_SCREEN_PARAMS ;
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_player);
    }

    @Override
    protected void initView() {
        TCAgent.onEvent(this,"sys_course");
        int height = getResources().getDisplayMetrics().heightPixels;
        COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//        COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);

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

            /*** 视频播放相关的方法 **/

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(PlayerlActivity.this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }

        });
        wvHtml.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });



    }

    /** 视频播放全屏 **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }


        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(PlayerlActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);

        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
//        decor.setSystemUiVisibility(flag);
        customView = view;


        setStatusBarVisibility(true);
        customViewCallback = callback;
    }


    /** 隐藏视频全屏 */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }
        rlTop.setVerticalGravity(View.VISIBLE);
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        wvHtml.setVisibility(View.VISIBLE);
    }

    /** 全屏容器界面 */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    @Override
    public void onBackPressed() {
        L.v("canGoBack",wvHtml.canGoBack());
        if(wvHtml.canGoBack())
        {
            wvHtml.goBack();
        }else{
            super.onBackPressed();
        }
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
