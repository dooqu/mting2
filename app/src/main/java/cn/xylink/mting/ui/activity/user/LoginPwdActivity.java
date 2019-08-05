package cn.xylink.mting.ui.activity.user;

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
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.contract.LoginContact;
import cn.xylink.mting.model.GetCodeRequest;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.HttpConst;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.presenter.LoginPresenter;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.activity.GetCodeActivity;
import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.activity.PhoneLoginActivity;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.MD5;
import cn.xylink.mting.utils.NetworkUtil;
import cn.xylink.mting.utils.TingUtils;

public class LoginPwdActivity extends BasePresenterActivity implements LoginContact.ILoginView, GetCodeContact.IGetCodeView {

    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.btn_next)
    Button mBtnNext;

    @BindView(R.id.pwd_icon)
    ImageView pwd_icon;

    private boolean isChecked = true;
    private String phone;

    private LoginPresenter loginPresenter;

    private GetCodePresenter codePresenter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_pwd_login);

        loginPresenter = (LoginPresenter) createPresenter(LoginPresenter.class);
        loginPresenter.attachView(this);

        codePresenter = (GetCodePresenter) createPresenter(GetCodePresenter.class);
        codePresenter.attachView(this);

        MTing.getActivityManager().pushActivity(this);
    }

    @Override
    protected void initView() {
        tvTitle.setText("手机号登录");
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
        phone = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_PHONE);
    }

    @Override
    protected void initTitleBar() {

    }


    @OnClick({R.id.btn_next
            , R.id.pwd_icon, R.id.btn_left, R.id.tv_forget_pwd})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_forget_pwd: {
                int netWorkStates = NetworkUtil.getNetWorkStates(context);
                if (netWorkStates == NetworkUtil.TYPE_NONE) {
                    toastShort(HttpConst.NO_NETWORK);
                    return;
                }
                requsetCode();

                break;
            }
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
                if (pwd.length() == 0) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pwd.length() < 6) {
                    toastLong("密码长度小于6位，请重新输入");
                    return;
                }

                LoginRequset requset = new LoginRequset();
                requset.setDeviceId(TingUtils.getDeviceId(getApplicationContext()));
                requset.setPhone(phone.replaceAll(" ", ""));

                byte[] pwds = null;
                try {
                    String md5Pwd = MD5.md5crypt(pwd);
                    L.v(md5Pwd);
                    pwds = EncryptionUtil.encrypt(md5Pwd, EncryptionUtil.getPublicKey(Const.publicKey));
                    L.v("pwds.length", pwds.length);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }
                L.v("pwd decode", pwd);
                requset.setPassword(new Base64().encodeToString(pwds));
                requset.doSign();
                loginPresenter.onLogin(requset);
                break;
        }
    }

    public void requsetCode() {
        if (TextUtils.isEmpty(phone))
            return;
        GetCodeRequest requset = new GetCodeRequest();
        requset.phone = phone.replaceAll(" ", "");
        requset.source = "forgot";
        requset.doSign();
        codePresenter.onGetCode(requset);
    }

    @Override
    public void onLoginSuccess(BaseResponse<UserInfo> response) {
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
    public void onLoginError(int code, String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> response) {
        L.v(response.code);
        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
        if (response.data != null) {
            Intent mIntent = new Intent(this, GetCodeActivity.class);
            mIntent.putExtra(EXTRA_PHONE, phone);
            mIntent.putExtra(EXTRA_SOURCE, "forgot");
            mIntent.putExtra(GetCodeActivity.EXTRA_CODE, response.data.getCodeId());
            startActivity(mIntent);
        }
    }

    @Override
    public void onCodeError(int code, String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg)) {
            toastShort(errorMsg);
        }
    }
}
