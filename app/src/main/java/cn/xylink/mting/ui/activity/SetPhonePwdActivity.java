package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apaches.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseRequest;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.RegisterRequset;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.RegisterContact;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.presenter.RegisterPresenter;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.TingUtils;

public class SetPhonePwdActivity extends BasePresenterActivity implements RegisterContact.IRegisterView  {

    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.btn_next)
    Button mBtnNext;

    private String phone;
    private String ticket;

    private RegisterPresenter registerPresenter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_set_phone_password);

        registerPresenter = (RegisterPresenter) createPresenter(RegisterPresenter.class);
        registerPresenter.attachView(this);
    }

    @Override
    protected void initView() {
        tvTitle.setText("设置密码");
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
        phone = getIntent().getStringExtra(GetCodeActivity.EXTRA_PHONE);
        ticket = getIntent().getStringExtra(GetCodeActivity.EXTRA_TICKET);
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
                }else if (etPwd.getText().length() > 16){
                    Toast.makeText(this,"长度不超过16位",Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd = etPwd.getText().toString();
                L.v("pwd",pwd);
                RegisterRequset requset = new RegisterRequset();
                requset.deviceId = TingUtils.getDeviceId(getApplicationContext());
                requset.setPhone(phone);

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
                requset.setTicket(ticket);
                requset.doSign();
                registerPresenter.onRegister(requset);
                break;
        }
    }



    @Override
    public void onRegisterSuccess(BaseResponse<UserInfo> response) {
            if(response.data != null)
            {
                L.v("token",response.data.getToken());
                ContentManager.getInstance().setLoginToken(response.data.getToken());
                Intent mIntent = new Intent(this,MainActivity.class);
                startActivity(mIntent);
            }
    }

    @Override
    public void onRegisterError(int code, String errorMsg) {

    }
}
