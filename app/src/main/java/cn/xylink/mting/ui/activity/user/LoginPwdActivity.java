package cn.xylink.mting.ui.activity.user;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
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
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.LoginContact;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.presenter.LoginPresenter;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.activity.PhoneLoginActivity;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.TingUtils;

public class LoginPwdActivity extends BasePresenterActivity implements LoginContact.ILoginView  {

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

    @Override
    protected void preView() {
        setContentView(R.layout.activity_pwd_login);

        loginPresenter = (LoginPresenter) createPresenter(LoginPresenter.class);
        loginPresenter.attachView(this);
    }

    @Override
    protected void initView() {
        tvTitle.setText("手机号登录");
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0)
                {
                    mBtnNext.setBackground(getResources().getDrawable(R.drawable.bg_phone_click_btn));
                }else{
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
    ,R.id.pwd_icon})
    public void onClick(View v){

        switch (v.getId())
        {
            case R.id.pwd_icon:
                if (isChecked){
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);// 输入为密码且可见
                    pwd_icon.setImageResource(R.mipmap.pwd_show);
                }else {
                    pwd_icon.setImageResource(R.mipmap.pwd_hide);
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);// 设置文本类密码（默认不可见），这两个属性必须同时设置
                }
                etPwd.setSelection(etPwd.length());
                isChecked = !isChecked;
                break;
            case R.id.btn_next:
                if(etPwd.getText().length() == 0)
                {
                    Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd = etPwd.getText().toString();

                LoginRequset requset = new LoginRequset();
                requset.setDeviceId(TingUtils.getDeviceId(getApplicationContext()));
                requset.setPhone(phone.replaceAll(" ",""));

                byte[] pwds = null;
                try {
                    pwds =  EncryptionUtil.encrypt(pwd, EncryptionUtil.getPublicKey(Const.publicKey));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                }
                L.v("pwd decode",pwd);
                requset.setPassword( new Base64().encodeToString(pwds));
                requset.doSign();
                loginPresenter.onLogin(requset);
                break;
        }
    }




    @Override
    public void onLoginSuccess(BaseResponse<UserInfo> response) {
        if(response.data != null)
        {
            L.v("message",response.message);
            L.v("token",response.data.getToken());
            ContentManager.getInstance().setLoginToken(response.data.getToken());
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
        }
    }

    @Override
    public void onLoginError(int code, String errorMsg) {
        Toast.makeText(this,errorMsg,Toast.LENGTH_SHORT).show();
    }
}
