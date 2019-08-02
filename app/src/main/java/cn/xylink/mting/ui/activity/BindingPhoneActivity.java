package cn.xylink.mting.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.contract.BindCheckContact;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.model.BindCheckRequest;
import cn.xylink.mting.model.GetCodeRequest;
import cn.xylink.mting.model.data.HttpConst;
import cn.xylink.mting.presenter.BindCheckPresenter;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.ui.activity.user.LoginPwdActivity;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.NetworkUtil;
import cn.xylink.mting.utils.PhoneNumberUtils;
import cn.xylink.mting.utils.TingUtils;
import cn.xylink.mting.widget.ZpPhoneEditText;

public class BindingPhoneActivity extends BasePresenterActivity implements BindCheckContact.IBindCheckView, GetCodeContact.IGetCodeView {

    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    public static final String EXTRA_CODE = "extra_code";
    public static final String EXTRA_PLATFORM = "extra_platform";
    @BindView(R.id.et_phone)
    ZpPhoneEditText etPhone;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.btn_next)
    Button mBtnNext;

    @BindView(R.id.iv_del_et)
    ImageView ivDelEt;

    private BindCheckPresenter bindCheckPresenter;
    private GetCodePresenter codePresenter;

    private String phone;
    private String source;
    private String platform;

    private String pausePhone = "";


    @Override
    protected void preView() {
        setContentView(R.layout.activity_binding_phone);

        bindCheckPresenter = (BindCheckPresenter) createPresenter(BindCheckPresenter.class);
        bindCheckPresenter.attachView(this);
        codePresenter = (GetCodePresenter) createPresenter(GetCodePresenter.class);
        codePresenter.attachView(this);

        MTing.getActivityManager().pushActivity(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        pausePhone = etPhone.getPhoneText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.v(pausePhone);
        if (!TextUtils.isEmpty(pausePhone)) {
            etPhone.setText(pausePhone);
            etPhone.setSelection(etPhone.getText().length());
        }
    }

    @Override
    protected void initView() {
        tvTitle.setText("绑定手机号");
        mBtnNext.setEnabled(false);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ivDelEt.setVisibility(View.VISIBLE);
                    mBtnNext.setEnabled(true);
                    mBtnNext.setBackground(getResources().getDrawable(R.drawable.bg_phone_click_btn));
                } else {
                    mBtnNext.setBackground(getResources().getDrawable(R.drawable.bg_phone_default_btn));
                    ivDelEt.setVisibility(View.GONE);
                    mBtnNext.setEnabled(false);
                }
            }
        });
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
    protected void onStop() {
        super.onStop();
        etPhone.setText("");
    }

    @Override
    protected void initData() {
        source = getIntent().getStringExtra(EXTRA_SOURCE);
        platform = getIntent().getStringExtra(EXTRA_PLATFORM);
    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick({R.id.btn_next, R.id.iv_del_et, R.id.btn_left})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.iv_del_et:
                etPhone.setText("");
                break;
            case R.id.btn_next:
                int netWorkStates = NetworkUtil.getNetWorkStates(context);
                if(netWorkStates == NetworkUtil.TYPE_NONE)
                {
                    toastShort(HttpConst.NO_NETWORK);
                    return;
                }
                phone = etPhone.getText().toString();
                phone = phone.replaceAll(" ", "");
                if (phone.length() == 0) {
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (phone.length() < 11 ){
                    Toast.makeText(this,R.string.incomplete_telephone_number,Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!PhoneNumberUtils.isMobileNO(phone))
                {
                    Toast.makeText(this,"手机号码输入有误，请重新输入",Toast.LENGTH_SHORT).show();
                    return;
                }
                BindCheckRequest requset = new BindCheckRequest();
                requset.setPhone(phone);
                requset.setPlatform(platform);
                requset.doSign();
                bindCheckPresenter.onBindCheck(requset);
                break;
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

    @Override
    public void onBindCheckSuccess(BaseResponse<String> response) {
        final int code = response.code;

        switch (code) {
            //注册验证码
            case 200: {
                requsetCode();
                break;
            }
            case 201: {

                Intent mIntent = new Intent(this, BindingPhoneQQWxActivity.class);
                mIntent.putExtra(EXTRA_PHONE, phone);
                mIntent.putExtra(EXTRA_SOURCE, source);
                mIntent.putExtra(EXTRA_PLATFORM, platform);
                startActivity(mIntent);

                break;
            }
            case -2: {
                Intent mIntent = new Intent(this, LoginPwdActivity.class);
                mIntent.putExtra(EXTRA_PHONE, phone);
                startActivity(mIntent);
                break;
            }
        }

    }

    @Override
    public void onBindCheckError(int code, String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg)) {
            toastShort(errorMsg);
        }
    }

    private String codeID;

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> response) {
        L.v(response.code);
        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
        if (response.data != null) {
            Intent mIntent = new Intent(this, GetCodeActivity.class);
            mIntent.putExtra(EXTRA_PHONE, phone);
            mIntent.putExtra(EXTRA_SOURCE, source);
            mIntent.putExtra(EXTRA_PLATFORM, platform);
            mIntent.putExtra(EXTRA_CODE, response.data.getCodeId());
            startActivity(mIntent);
        }
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
}
