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

public class HtmlActivity extends BaseActivity {

    public static final String EXTRA_HTML = "html_url";

    @BindView(R.id.wv_html)
    WebView wvHtml;
    @BindView(R.id.pb_speech_bar)
    ProgressBar progressBar;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_webview);
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            wvHtml.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wvHtml.getSettings().setJavaScriptEnabled(true);//启用js
        wvHtml.getSettings().setBlockNetworkImage(false);
        wvHtml.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                L.v("newProgress",newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });
    }

    @Override
    protected void initData() {
        String url = getIntent().getStringExtra(EXTRA_HTML);
        wvHtml.loadUrl(url);
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
