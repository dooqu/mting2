package cn.xylink.mting.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.CheckTokenContact;
import cn.xylink.mting.model.CheckTokenRequest;
import cn.xylink.mting.presenter.CheckTokenPresenter;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.activity.GuideActivity;
import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.activity.SpeechServicActivity;

public class SplashActivity extends BasePresenterActivity implements CheckTokenContact.ICheckTokenView {


    private CheckTokenPresenter tokenPresenter;
    @Override
    protected void preView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initView() {

        if(false)
        {
            startActivity(new Intent(this, SpeechServicActivity.class));
            return;
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //闪屏页显示3秒才跳转
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                CheckTokenRequest requset = new CheckTokenRequest();
                requset.doSign();
                tokenPresenter.onCheckToken(requset);

                return false;
            }
        }).sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void initData() {

        tokenPresenter = (CheckTokenPresenter) createPresenter(CheckTokenPresenter.class);
        tokenPresenter.attachView(this);

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
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
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
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
        }
        finish();
    }

    @Override
    public void onCheckTokenSuccess(BaseResponse<UserInfo> response) {

//        if (Build.VERSION.SDK_INT < 23) {
//            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
//            finish();
//        } else {
//            initPermission();
//        }
        switch (response.code)
        {
            case 200:
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                break;
            default:
                if (Build.VERSION.SDK_INT < 23) {
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    finish();
                } else {
                    initPermission();
                }
                break;
        }
    }

    @Override
    public void onCheckTokenError(int code, String errorMsg) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
