package cn.xylink.mting.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.CheckTokenContact;
import cn.xylink.mting.contract.ShareAddContract;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.model.ArticleInfoRequest;
import cn.xylink.mting.model.CheckTokenRequest;
import cn.xylink.mting.model.data.FileCache;
import cn.xylink.mting.presenter.CheckTokenPresenter;
import cn.xylink.mting.presenter.ShareAddPresenter;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.ui.activity.ArticleDetailActivity;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.activity.GuideActivity;
import cn.xylink.mting.ui.activity.LoginActivity;
import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.fragment.UnreadFragment;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.L;

public class SplashActivity extends BasePresenterActivity implements CheckTokenContact.ICheckTokenView, ShareAddContract.IShareAddView {

    private CheckTokenPresenter tokenPresenter;
    private ShareAddPresenter shareAddPresenter;
    private final int SPLASH_TIME = 3000;
    private long startTime;
    private long endTime;
    private String codeId;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initView() {
        initPermission();
    }

    @Override
    protected void initData() {
        tokenPresenter = (CheckTokenPresenter) createPresenter(CheckTokenPresenter.class);
        tokenPresenter.attachView(this);

        shareAddPresenter = (ShareAddPresenter) createPresenter(ShareAddPresenter.class);
        shareAddPresenter.attachView(this);

        Intent intent = getIntent();//在这个Activity里，我们可以通过getIntent()，来获取外部跳转传过来的信息。
        if (intent.getDataString() != null) {
            String data = intent.getDataString();//接收到网页传过来的数据：sharetest://data/http://www.huxiu.com/
            L.v("data", data);
            // mting://mting:20198/homePage?code=2019080715393763146809148
            String url = data.substring(data.indexOf("homePage?code="), data.length());
            codeId = url.substring("homePage?code=".length(), url.length());
            L.v("url", codeId);
        }
    }

    @Override
    protected void initTitleBar() {
    }

    private void initPermission() {

        ArrayList<String> toApplyList = new ArrayList<String>();
        String permissions[] = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.REQUEST_INSTALL_PACKAGES
        };
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        } else {
            startTime = SystemClock.elapsedRealtime();
            CheckTokenRequest requset = new CheckTokenRequest();
            requset.doSign();
            tokenPresenter.onCheckToken(requset);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean flag = true;
        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
                if (grantResults[i] == -1) {
                    flag = false;
                }
            } else if (Manifest.permission.READ_PHONE_STATE.equals(permissions[i])) {
                if (grantResults[i] == -1) {
                    flag = false;
                }
            }
        }
        if (flag) {
            startTime = SystemClock.elapsedRealtime();
            CheckTokenRequest requset = new CheckTokenRequest();
            requset.doSign();
            tokenPresenter.onCheckToken(requset);
        } else {
            finish();
        }
    }


    @Override
    public void onCheckTokenSuccess(BaseResponse<UserInfo> response) {
        endTime = SystemClock.elapsedRealtime();
//        if (Build.VERSION.SDK_INT < 23) {
//            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
//            finish();
//        } else {
//            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//            finish();
//        }
        L.v("code", response.code);
        Message msg = mHandler.obtainMessage();
        msg.obj = response.code;
        msg.what = SUCCESS;
        long takeTime = endTime - startTime;
        L.v("(takeTime < SPLASH_TIME", (takeTime < SPLASH_TIME));
        if (takeTime < SPLASH_TIME) {
            takeTime = (SPLASH_TIME - takeTime);
            L.v("takeTime", takeTime);
            mHandler.sendMessageDelayed(msg, takeTime);
        } else {
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onCheckTokenError(int code, String errorMsg) {
        L.v("code", code, "errorMsg", errorMsg);
        endTime = SystemClock.elapsedRealtime();
        Message msg = mHandler.obtainMessage();
        msg.obj = code;
        msg.what = ERROR;

        long takeTime = endTime - startTime;
        L.v("(takeTime < SPLASH_TIME", (takeTime < SPLASH_TIME));
        if (takeTime < SPLASH_TIME) {
            takeTime = (SPLASH_TIME - takeTime);
            L.v("takeTime", takeTime);
            mHandler.sendMessageDelayed(msg, takeTime);
        } else {
            mHandler.sendMessage(msg);
        }
    }

    private final static int SUCCESS = 1;
    private final static int ERROR = 2;

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int code = (int) msg.obj;
            switch (msg.what) {
                case SUCCESS:
                    switch (code) {
                        case 200:
                            if (!TextUtils.isEmpty(codeId)) {
                                ArticleInfoRequest request = new ArticleInfoRequest();
                                request.setArticleId(codeId);
                                request.doSign();
                                shareAddPresenter.shareAdd(request);
                            } else {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                            break;
                    }
                    break;
                case ERROR:
                    L.v("isGuideFirst", FileCache.getInstance().isGuideFirst());
                    if (!TextUtils.isEmpty(codeId)) {
                        ArticleInfoRequest request = new ArticleInfoRequest();
                        request.setArticleId(codeId);
                        request.doSign();
                        shareAddPresenter.shareAdd(request);
                    } else {
                        if (FileCache.getInstance().isGuideFirst()) {
                            FileCache.getInstance().setHasGuide();
                            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                            finish();
                        } else {
                            if (code != -999) {
                                if (TextUtils.isEmpty(ContentManager.getInstance().getLoginToken())) {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }
                            } else {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onShareAddSuccess(BaseResponse<ArticleDetailInfo> info) {
        addLocalUnread(info.data);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ARTICLE_ID, info.data.getArticleId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    private void addLocalUnread(ArticleDetailInfo linkArticle) {
        if (linkArticle != null && UnreadFragment.ISINIT) {
            Article article = new Article();
            article.setProgress(0);
            article.setTitle(linkArticle.getTitle());
            article.setArticleId(linkArticle.getArticleId());
            article.setSourceName(linkArticle.getSourceName());
            article.setShareUrl(linkArticle.getShareUrl());
            article.setStore(linkArticle.getStore());
            article.setRead(linkArticle.getRead());
            article.setUpdateAt(linkArticle.getUpdateAt());
            List<Article> list = new ArrayList<>();
            list.add(article);
            SpeechList.getInstance().pushFront(list);
            EventBus.getDefault().post(new AddUnreadEvent());
        }
    }

    @Override
    public void onShareAddError(int code, String errorMsg) {
        if (FileCache.getInstance().isGuideFirst()) {
            FileCache.getInstance().setHasGuide();
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            finish();
        } else {
            if (code != -999) {
                if (TextUtils.isEmpty(ContentManager.getInstance().getLoginToken())) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }
    }
}
