package cn.xylink.mting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.BuildConfig;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.LogUtils;


public class PlayerlActivity extends BaseActivity {

    //    public static final String EXTRA_HTML = "html_url";
    public static final String PROTOCOL_URL = "http://test.xylink.cn/article/html/tutorial_v2.html";
    public static final String EXTRA_HTML = "html_url";
    public static final String EXTRA_TITLE = "title";

    //    @BindView(R.id.wv_html)
    WebView wvHtml;
    @BindView(R.id.pb_speech_bar)
    ProgressBar progressBar;
    @BindView(R.id.fl_web)
    FrameLayout flWeb;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String url;

    /**
     * 视频全屏参数
     */
    protected FrameLayout.LayoutParams COVER_SCREEN_PARAMS;
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_player);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initWebView();
    }

    public void initWebView() {
        wvHtml = new WebView(this);
        wvHtml.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        flWeb.addView(wvHtml);
        int height = getResources().getDisplayMetrics().heightPixels;
        COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//        COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            wvHtml.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        WebSettings mWebSettings = wvHtml.getSettings();
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true
        mWebSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        mWebSettings.setAppCacheEnabled(true);//是否使用缓存
        mWebSettings.setDomStorageEnabled(true);//开启本地DOM存储
        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + "xyting-android-vname/" + BuildConfig.VERSION_NAME + "-vcode/" + BuildConfig.VERSION_CODE);


        wvHtml.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100)
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
        wvHtml.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.v(url);
                String pre = "xylink://xyting";
                if (TextUtils.isEmpty(url))
                    return true;
                if (!url.contains(pre)&& url.startsWith("http")) {
                    wvHtml.loadUrl(url);
                    return false;
                }
                //该url是调用android方法的请求，通过解析url中的参数来执行相应方法
                Map<String, String> map = getParamsMap(url, pre);
                String callback = map.get("callback");
                String data = map.get("data");
                L.v(data);
                if ("open_permission".equals(data)) {
                    Intent intentNotifOpen = new Intent();
                    intentNotifOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        intentNotifOpen.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//                        intentNotifOpen.putExtra(Settings.EXTRA_APP_PACKAGE, PlayerlActivity.this.getPackageName());
//                    } else {
                    intentNotifOpen.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                        intentNotifOpen.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//                        intentNotifOpen.putExtra("app_package", PlayerlActivity.this.getPackageName());
//                        intentNotifOpen.putExtra("app_uid", PlayerlActivity.this.getApplicationInfo().uid);
                    intentNotifOpen.setData(Uri.fromParts("package", PlayerlActivity.this.getPackageName(), null));
//                    }
                    PlayerlActivity.this.startActivity(intentNotifOpen);
                }
                return true;
            }
        });

        wvHtml.loadUrl(url);

    }

    private Map<String, String> getParamsMap(String url, String pre) {
        Map<String, String> queryStringMap = new HashMap<>();
        if (url.contains(pre)) {
            int index = url.indexOf(pre);
            int end = index + pre.length();
            String queryString = url.substring(end + 1);

            String[] queryStringSplit = queryString.split("&");

            String[] queryStringParam;
            for (String qs : queryStringSplit) {
                if (qs.toLowerCase().startsWith("data=")) {
                    //单独处理data项，避免data内部的&被拆分
                    int dataIndex = qs.indexOf("data=");
                    String dataValue = qs.substring(dataIndex + 5);
                    queryStringMap.put("data", dataValue);
                } else {
                    queryStringParam = qs.split("=");

                    String value = "";
                    if (queryStringParam.length > 1) {
                        //避免后台有时候不传值,如“key=”这种
                        value = queryStringParam[1];
                    }
                    queryStringMap.put(queryStringParam[0].toLowerCase(), value);
                }
            }
        }
        return queryStringMap;
    }

    @Override
    protected void initView() {


    }

    /**
     * 视频播放全屏
     **/
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


    /**
     * 隐藏视频全屏
     */
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

    /**
     * 全屏容器界面
     */
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
        url = getIntent().getStringExtra(EXTRA_HTML);
        LogUtils.e(url);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title))
            tvTitle.setText(title);
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onBackPressed() {
        if (wvHtml.canGoBack()) {
            wvHtml.goBack();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWebView();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyWebView();
    }

    private void destroyWebView() {
        if (wvHtml != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = wvHtml.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(wvHtml);
            }

            wvHtml.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            wvHtml.getSettings().setJavaScriptEnabled(false);
            wvHtml.clearHistory();
            wvHtml.clearView();
            wvHtml.removeAllViews();
            wvHtml.destroy();

        }
    }

    @OnClick(R.id.iv_close)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
        }
    }
}
