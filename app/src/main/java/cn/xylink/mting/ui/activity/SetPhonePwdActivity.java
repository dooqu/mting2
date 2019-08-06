package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apaches.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.BindThirdPlatformContact;
import cn.xylink.mting.contract.RegisterContact;
import cn.xylink.mting.model.RegisterRequset;
import cn.xylink.mting.model.ThirdPlatformRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.HttpConst;
import cn.xylink.mting.presenter.RegisterPresenter;
import cn.xylink.mting.presenter.ThirdPlatformPresenter;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.MD5;
import cn.xylink.mting.utils.NetworkUtil;
import cn.xylink.mting.utils.SharedPreHelper;
import cn.xylink.mting.utils.TingUtils;

public class SetPhonePwdActivity extends BasePresenterActivity implements RegisterContact.IRegisterView, BindThirdPlatformContact.IThirdPlatformView {

    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    public static final String EXTRA_TYPE = "extra_register_type";

    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.btn_next)
    Button mBtnNext;
    @BindView(R.id.pwd_icon)
    ImageView pwd_icon;

    private String phone;
    private String ticket;
    private String source;
    private String platform;
    private boolean isChecked;

    private RegisterPresenter registerPresenter;
    private int type = 0;

    private ThirdPlatformPresenter thirdPlatformPresenter;

    private SharedPreHelper sharedPreHelper;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_set_phone_password);

        registerPresenter = (RegisterPresenter) createPresenter(RegisterPresenter.class);
        registerPresenter.attachView(this);

        thirdPlatformPresenter = (ThirdPlatformPresenter) createPresenter(ThirdPlatformPresenter.class);
        thirdPlatformPresenter.attachView(this);

        sharedPreHelper = SharedPreHelper.getInstance(this);
    }

    @Override
    protected void initView() {
        tvTitle.setText("设置密码");
        mBtnNext.setEnabled(false);
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBtnNext.setBackground(getResources().getDrawable(R.drawable.bg_phone_click_btn));
                    mBtnNext.setEnabled(true);
                } else {
                    mBtnNext.setEnabled(false);
                    mBtnNext.setBackground(getResources().getDrawable(R.drawable.bg_phone_default_btn));
                }
            }
        });
    }

    @Override
    protected void initData() {
        phone = getIntent().getStringExtra(GetCodeActivity.EXTRA_PHONE);
        ticket = getIntent().getStringExtra(GetCodeActivity.EXTRA_TICKET);
        type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        platform = getIntent().getStringExtra(BindingPhoneActivity.EXTRA_PLATFORM);
        L.v("phone", phone, "ticket", ticket, "type", type);
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
    protected void initTitleBar() {

    }

    @OnClick({R.id.btn_next, R.id.pwd_icon,R.id.btn_left})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.pwd_icon:
                if (isChecked) {
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 输入为密码且可见
                    pwd_icon.setImageResource(R.mipmap.pwd_show);
                } else {
                    pwd_icon.setImageResource(R.mipmap.pwd_hide);
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);// 设置文本类密码（默认不可见），这两个属性必须同时设置
                }
                etPwd.setSelection(etPwd.length());
                isChecked = !isChecked;
                break;
            case R.id.btn_next:
                int netWorkStates = NetworkUtil.getNetWorkStates(context);
                if (netWorkStates == NetworkUtil.TYPE_NONE) {
                    toastShort(HttpConst.NO_NETWORK);
                    return;
                }
                String pwd = etPwd.getText().toString();
                if (etPwd.getText().length() == 0) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if (etPwd.getText().length() > 20) {
//                    Toast.makeText(this, "长度不超过20位", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                else if (pwd.length() < 6) {
                    toastLong("密码长度小于6位，请重新输入");
                    return;
                }
                L.v("pwd", pwd);
                if (TextUtils.isEmpty(platform)) {
                    commonRegister(pwd);
                } else {
                    thirdPlatformRequest(pwd);
                }
                break;
        }
    }

    public void commonRegister(String pwd) {
        RegisterRequset requset = new RegisterRequset();
        requset.setDeviceId(TingUtils.getDeviceId(getApplicationContext()));
        requset.setPhone(phone);

        byte[] pwds = null;
        try {
            pwds = EncryptionUtil.encrypt(MD5.md5crypt(pwd), EncryptionUtil.getPublicKey(Const.publicKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        L.v("pwd decode", pwd);
        requset.setPassword(new Base64().encodeToString(pwds));
        requset.setTicket(ticket);
        requset.doSign();
        registerPresenter.onRegister(requset, type);
    }


    @Override
    public void onRegisterSuccess(BaseResponse<UserInfo> response) {
        if (response.data != null) {
            L.v("token", response.data.getToken());
            ContentManager.getInstance().setLoginToken(response.data.getToken());
            ContentManager.getInstance().setUserInfo(response.data);
            Intent mIntent = new Intent(this, MainActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            MTing.getActivityManager().popAllActivitys();
        }
    }


    public void thirdPlatformRequest(String pwd) {

        ThirdPlatformRequest requset = new ThirdPlatformRequest();

        String access_token = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.ACCESS_TOKEN, "");
        String appid = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.OPENID, "");
        String ticket = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.TICKET, "");

        requset.setAccess_token(access_token);
        requset.setOpenid(appid);
        requset.setTicket(ticket);
        requset.setPhone(phone.replaceAll(" ", ""));
        requset.setPlatform(platform);

        byte[] pwds = null;
        try {
            pwds = EncryptionUtil.encrypt(MD5.md5crypt(pwd), EncryptionUtil.getPublicKey(Const.publicKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        requset.setPassword(new Base64().encodeToString(pwds));
        requset.doSign();
        thirdPlatformPresenter.onThirdPlatform(requset);
    }

    @Override
    public void onRegisterError(int code, String errorMsg) {

    }


    @Override
    public void onThirdPlatformSuccess(BaseResponse<UserInfo> response) {
        L.v(response);
        if (response.data != null) {
            L.v("message", response.message);
            L.v("token", response.data.getToken());
            ContentManager.getInstance().setLoginToken(response.data.getToken());
            ContentManager.getInstance().setUserInfo(response.data);
            Intent mIntent = new Intent(this, MainActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            MTing.getActivityManager().popAllActivitys();
        }
    }

    @Override
    public void onThirdPlatformError(int code, String errorMsg) {
            toastShort(errorMsg);
    }
}
