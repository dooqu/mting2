package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.contract.CheckPhoneContact;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.model.CheckPhoneRequest;
import cn.xylink.mting.model.GetCodeRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.HttpConst;
import cn.xylink.mting.presenter.CheckPhonePresenter;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.SafeUtils;
import cn.xylink.mting.utils.SharedPreHelper;
import cn.xylink.mting.utils.TingUtils;
import cn.xylink.mting.widget.PhoneCode;
import cn.xylink.mting.widget.ZpPhoneEditText;

public class GetCodeActivity extends BasePresenterActivity implements GetCodeContact.IGetCodeView, CheckPhoneContact.ICheckPhoneView {

    private GetCodePresenter codePresenter;
    private CheckPhonePresenter checkPhonePresenter;
    private CodeInfo codeInfo;
    public static final String EXTRA_TICKET = "extra_ticket";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    public static final String EXTRA_platform = "extra_platform";

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
                tvCountDown.setText(millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                isFinished = true;
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
        if ("register".equals(source))
            resetDownTimer(ONE_MINUTE);
        pCcode.setOnCompleteListener(new PhoneCode.Listener() {
            @Override
            public void onComplete(String content) {
                L.v("content", content);
                codeLength = content.length();
                if (TextUtils.isEmpty(phone))
                    return;
                CheckPhoneRequest requset = new CheckPhoneRequest();
                requset.source = "register";
                requset.codeId = codeID;
                requset.phone = phone.replaceAll(" ", "");
                requset.setDeviceId(TingUtils.getDeviceId(getApplicationContext()));
                try {
                    requset.code = SafeUtils.getRsaString(content, Const.publicKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requset.doSign();
                checkPhonePresenter.onCheckPhone(requset);


            }
        });

        etPhone.setText(phone);
        tvTitle.setText("手机号验证");
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

        checkPhonePresenter = (CheckPhonePresenter) createPresenter(CheckPhonePresenter.class);
        checkPhonePresenter.attachView(this);
        if (!"register".equals(source))
            requsetCode();
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> response) {
        L.v(response.code);
        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
        if (response.data != null) {
            codeID = response.data.getCodeId();
        }
        if (response.code == 200) {
            resetDownTimer(ONE_MINUTE);
            return;
        }
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
        requset.source = source;
        requset.doSign();
        codePresenter.onGetCode(requset);
    }

    @OnClick(R.id.tv_count_down)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_count_down:
                if (isFinished) {
                    pCcode.clearText();
                    requsetCode();

                }
                break;
        }
    }

    @Override
    public void onCheckPhoneSuccess(BaseResponse<CheckInfo> response) {
        L.v("code", response.code);
        timer.onFinish();
        if (response.data != null) {
            ticket = response.data.getTicket();
            SharedPreHelper.getInstance(this).put(SharedPreHelper.SharedAttribute.TICKET, ticket);

            Intent mIntent = new Intent(this, SetPhonePwdActivity.class);
            mIntent.putExtra(EXTRA_TICKET, ticket);
            mIntent.putExtra(BindingPhoneActivity.EXTRA_PLATFORM, platform);
            mIntent.putExtra(EXTRA_PHONE, phone.replaceAll(" ", ""));
            if (source.equals("register")) {
                mIntent.putExtra(SetPhonePwdActivity.EXTRA_TYPE, 1);
            } else if (source.equals("forgot")) {
                mIntent.putExtra(SetPhonePwdActivity.EXTRA_TYPE, 2);
            }
            startActivity(mIntent);

        }
    }

    @Override
    public void onCheckPhoneError(int code, String errorMsg) {
        switch (code) {
            case -3:
                pCcode.clearText();
                toastShort("验证码输入错误，请重新输入");
                break;
            default:
                toastShort(errorMsg);
                break;
        }
        if (timer != null)
            timer.onFinish();
    }
}
