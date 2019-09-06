package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.BindThirdPlatformContact;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.model.GetCodeRequest;
import cn.xylink.mting.model.ThirdPlatformRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.HttpConst;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.presenter.ThirdPlatformPresenter;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.SafeUtils;
import cn.xylink.mting.utils.SharedPreHelper;
import cn.xylink.mting.widget.PhoneCode;
import cn.xylink.mting.widget.ZpPhoneEditText;

public class BindingThirdCodeActivity extends BasePresenterActivity implements GetCodeContact.IGetCodeView, BindThirdPlatformContact.IThirdPlatformView {

    private GetCodePresenter codePresenter;
    private ThirdPlatformPresenter thirdPlatformPresenter;
    private CodeInfo codeInfo;
    public static final String EXTRA_TICKET = "extra_ticket";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    public static final String EXTRA_platform = "extra_platform";
    public static final String EXTRA_CODE = "extra_code";

    private static final int ONE_MINUTE = 60 * 1000;

    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    @BindView(R.id.et_phone)
    ZpPhoneEditText etPhone;
    @BindView(R.id.pc_code)
    PhoneCode pCcode;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;


    private boolean isFinished;
    private String phone;
    private String codeID;
    private String ticket;
    private String source;
    private String platform;

    CountDownTimer timer;

    private int codeLength;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_get_code);
        MTing.getActivityManager().pushActivity(this);
    }


    public void resetDownTimer(long minute) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new CountDownTimer(minute, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isFinished = false;
                tvCountDown.setTextColor(getResources().getColorStateList(R.color.color_login_text_gray));
                tvCountDown.setText(millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                isFinished = true;

                tvCountDown.setTextColor(getResources().getColorStateList(R.color.color_blue));
                tvCountDown.setText("重新获取");
            }
        }.start();
    }


    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }

    @Override
    protected void initView() {

        L.v("source", source);
//        if ("register".equals(source))
        resetDownTimer(ONE_MINUTE);
        pCcode.setOnCompleteListener(new PhoneCode.Listener() {
            @Override
            public void onComplete(String content) {
                L.v("content", content);
                codeLength = content.length();
                if (TextUtils.isEmpty(phone))
                    return;
                smsLogin(content);


            }
        });

        etPhone.setText(phone);
        tvTitle.setText("手机号验证");
    }


    private void smsLogin(String smsContent)
    {
        L.v(smsContent.length());
        ThirdPlatformRequest requset = new ThirdPlatformRequest();
        try {
            requset.setCode( SafeUtils.getRsaString(smsContent, Const.publicKey) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreHelper sharedPreHelper = SharedPreHelper.getInstance(this);
        String access_token = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.ACCESS_TOKEN, "");
        String appid = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.OPENID, "");

        requset.setAccess_token(access_token);
        requset.setOpenid(appid);
        requset.setPhone(phone.replaceAll(" ", ""));
        requset.setPlatform(platform);
        requset.setCodeId(codeID);
        requset.doSign();
        thirdPlatformPresenter.onThirdPlatform(requset);
    }

    @Override
    protected void initData() {

        phone = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_PHONE);
        codeID = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_CODE);
        source = getIntent().getStringExtra(EXTRA_SOURCE);
        platform = getIntent().getStringExtra(BindingPhoneActivity.EXTRA_PLATFORM);


        L.v("phone", phone, "ticket", ticket, "codeID", codeID);
        codePresenter = (GetCodePresenter) createPresenter(GetCodePresenter.class);
        codePresenter.attachView(this);


        thirdPlatformPresenter = (ThirdPlatformPresenter) createPresenter(ThirdPlatformPresenter.class);
        thirdPlatformPresenter.attachView(this);
//        if (!"register".equals(source))
//            requsetCode();
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> response) {
        L.v(response.code);
        toastShort(response.message);
        if (response.data != null) {
            codeID = response.data.getCodeId();
        }
        resetDownTimer(ONE_MINUTE);
    }

    private static final String PAUSE_TIME = "code_pause_time";

    @Override
    protected void onPause() {
        super.onPause();
        long pauseTime = SystemClock.elapsedRealtime();
        SharedPreHelper.getInstance(this).put(PAUSE_TIME, pauseTime);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onCodeError(int code, String errorMsg) {
        switch (code) {
            case HttpConst.STATUS_910:
                toastShort(errorMsg);
                break;
        }
        if (code == -910) {
//            long resumeTime = SystemClock.elapsedRealtime();
//            long pauseTime = (long) SharedPreHelper.getInstance(this).getSharedPreference(PAUSE_TIME, 0l);
//            L.v("resumeTime", resumeTime, "pauseTime", pauseTime);
//            long endTime = resumeTime - pauseTime;
//            L.v("endTime", endTime, "ONE_MINUTE", ONE_MINUTE);
//            if (endTime < ONE_MINUTE) {
////                resetDownTimer(endTime);
//            }
        }
    }


    public void requsetCode() {
        if (TextUtils.isEmpty(phone))
            return;
        GetCodeRequest requset = new GetCodeRequest();
        requset.phone = phone.replaceAll(" ", "");
        requset.source = "";
        requset.doSign();
        codePresenter.onGetCode(requset);
    }

    @OnClick({R.id.tv_count_down, R.id.btn_left})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_count_down:
                if (isFinished) {
                    pCcode.clearText();
                    requsetCode();

                }
                break;
            case R.id.btn_left:
                finish();
                break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer.onFinish();
        }
        if(pCcode != null){
            pCcode.clearText();
        }

    }

    @Override
    public void onThirdPlatformSuccess(BaseResponse<UserInfo> response) {
        if (response.data != null) {
            L.v("token", response.data.getToken());
            ContentManager.getInstance().setLoginToken(response.data.getToken());
            ContentManager.getInstance().setUserInfo(response.data);

            TCAgent.onRegister(ContentManager.getInstance().getUserInfo().getUserId(), TDAccount.AccountType.ANONYMOUS, "");
            TCAgent.onLogin(ContentManager.getInstance().getUserInfo().getUserId(), TDAccount.AccountType.ANONYMOUS,"");
            Intent mIntent = new Intent(this, MainActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            MTing.getActivityManager().popAllActivitys();
        }
    }

    @Override
    public void onThirdPlatformError(int code, String errorMsg) {
        L.v("code",code);
        switch (code) {
            case -3:
                pCcode.clearText();
                toastShort("验证码输入错误，请重新输入");
                break;
            default:
                pCcode.clearText();
                toastShort(errorMsg);
                break;
        }
    }
}
