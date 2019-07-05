package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.view.View;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.common.Const;
import cn.xylink.mting.model.WXQQDataBean;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.LogUtils;

public class LoginActivity extends BaseActivity {


    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String OPEN_ID = "OPEN_ID";
    public static final String HEAD_IMG = "HEAD_IMG";
    public static final String NICK_NAME = "NICK_NAME";
    public static final String TYPE = "TYPE";

    private Tencent mTencent;

    @Override
    protected void preView() {
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_login);
        mTencent =  QQApi.getInstance();
        L.v("mTencent",mTencent);
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Const.QQ_ID, getApplicationContext());
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.imv_login_weChat, R.id.imv_login_qq,R.id.tv_phone})
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imv_login_weChat:
                if (WXapi.isInstallWX()) {
                    WXapi.loginWX();
                } else {
                    toastShort("您还未安装微信客户端！");
                }
                break;
            case R.id.imv_login_qq:
                mTencent.login(this, "all", new BaseUiListener());
                break;
            case R.id.tv_phone:
                startActivity(new Intent(LoginActivity.this,PhoneLoginActivity.class));
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.v("requestCode",requestCode,"resultCode",resultCode);
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WXQQDataBean event) {
        final String eventType = event.getType();
        L.v("eventType",eventType);
        if (eventType.equals("wechat")) {
           String access_token = event.getAccess_token();
           String  openId = event.getOpenid();
            LogUtils.e("nana", "WX access_token: " + access_token + ",,,openId: " + openId);
            Intent intent = new Intent(this,BindUserPhoneThirdPlatformActivity.class);
            startActivity(intent);
        } else if (eventType.equals("qq")) {
            String access_token = event.getAccess_token();
            String  openId = event.getOpenid();
            LogUtils.e("nana", "WX access_token: " + access_token + ",,,openId: " + openId);
            Intent intent = new Intent(this,BindUserPhoneThirdPlatformActivity.class);
            startActivity(intent);
        }

    }
}


class BaseUiListener implements IUiListener {

    private String accessTokenQQ;
    private String openIdQQ;

    @Override
    public void onComplete(Object response) {
        L.v("nana", "QQ登录成功");
        try {
            accessTokenQQ = ((JSONObject) response).getString("access_token");
            openIdQQ = ((JSONObject) response).getString("openid");
            L.v("nana", "accessTokenQQ: " + accessTokenQQ + "\nopenIdQQ: " + openIdQQ);
            EventBus.getDefault().post(new WXQQDataBean(accessTokenQQ, openIdQQ, "qq"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(UiError e) {
        L.v("nana", "code:" + e.errorCode + ", msg:"

                + e.errorMessage + ", detail:" + e.errorDetail);

    }

    @Override
    public void onCancel() {
        L.v("nana", "qq cancel...");

    }

}