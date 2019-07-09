package cn.xylink.mting.ui.activity.user;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.LoginContact;
import cn.xylink.mting.presenter.LoginPresenter;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.activity.PhoneLoginActivity;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.MD5;
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

    @OnClick(R.id.btn_next)
    public void onClick(View v){

        switch (v.getId())
        {
            case R.id.btn_next:
                if(etPwd.getText().length() == 0)
                {
                    Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd = etPwd.getText().toString();

                LoginRequset requset = new LoginRequset();
                requset.deviceId = TingUtils.getDeviceId(getApplicationContext());
                requset.setPhone(MD5.md5crypt(phone));
                requset.setPassword(pwd);
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
        }
    }

    @Override
    public void onLoginError(int code, String errorMsg) {
        Toast.makeText(this,errorMsg,Toast.LENGTH_SHORT).show();
    }
}
