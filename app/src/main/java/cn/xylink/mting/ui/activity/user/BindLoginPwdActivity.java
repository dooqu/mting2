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
import cn.xylink.mting.contract.BindThirdPlatformContact;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.contract.LoginContact;
import cn.xylink.mting.model.GetCodeRequest;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.model.ThirdPlatformRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.presenter.LoginPresenter;
import cn.xylink.mting.presenter.ThirdPlatformPresenter;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.activity.GetCodeActivity;
import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.activity.PhoneLoginActivity;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.MD5;
import cn.xylink.mting.utils.SharedPreHelper;
import cn.xylink.mting.utils.TingUtils;

public class BindLoginPwdActivity extends BasePresenterActivity implements BindThirdPlatformContact.IThirdPlatformView, GetCodeContact.IGetCodeView {

    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    public static final String EXTRA_PLATFORM = "extra_platform";
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
    private String platform;

    private ThirdPlatformPresenter thirdPlatformPresenter;
    private GetCodePresenter codePresenter;

    private SharedPreHelper sharedPreHelper;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_pwd_login);

        thirdPlatformPresenter = (ThirdPlatformPresenter) createPresenter(ThirdPlatformPresenter.class);
        thirdPlatformPresenter.attachView(this);

        codePresenter = (GetCodePresenter) createPresenter(GetCodePresenter.class);
        codePresenter.attachView(this);

        sharedPreHelper = SharedPreHelper.getInstance(this);

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
        platform = getIntent().getStringExtra(EXTRA_PLATFORM);
    }

    public void requsetCode() {
        if (TextUtils.isEmpty(phone))
            return;
        GetCodeRequest requset = new GetCodeRequest();
        requset.phone = phone.replaceAll(" ", "");
        requset.source = "bind_phone";
        requset.doSign();
        codePresenter.onGetCode(requset);
    }

    @Override
    protected void initTitleBar() {

    }


    @OnClick({R.id.btn_next
    ,R.id.pwd_icon,R.id.btn_left,R.id.tv_forget_pwd})
    public void onClick(View v){

        switch (v.getId())
        {
            case R.id.tv_forget_pwd:{
                requsetCode();
                break;
            }
            case R.id.btn_left:
                finish();
                break;
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
                String pwd = etPwd.getText().toString();
                if(pwd.length() == 0)
                {
                    Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if(pwd.length() < 6)
                {
                    toastLong("密码长度小于6位，请重新输入");
                    return;
                }
                thirdPlatformRequest();
                break;
        }
    }

    public void thirdPlatformRequest(){

        ThirdPlatformRequest requset = new ThirdPlatformRequest();

        String access_token = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.ACCESS_TOKEN,"");
        String appid = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.OPENID,"");
        String ticket = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.TICKET,"");

        requset.setAccess_token(access_token);
        requset.setOpenid(appid);
        requset.setTicket(ticket);
        requset.setPhone(phone.replaceAll(" ",""));
        requset.setPlatform(platform);

        byte[] pwds = null;
        try {
            pwds =  EncryptionUtil.encrypt(MD5.md5crypt(etPwd.getText().toString()), EncryptionUtil.getPublicKey(Const.publicKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        requset.setPassword( new Base64().encodeToString(pwds));
        requset.doSign();
        thirdPlatformPresenter.onThirdPlatform(requset);
    }
    @Override
    public void onThirdPlatformSuccess(BaseResponse<UserInfo> response) {
        L.v(response);
//        Toast.makeText(this,response.message,Toast.LENGTH_SHORT).show();
        if(response.data != null)
        {
            L.v("message",response.message);
            L.v("token",response.data.getToken());
            ContentManager.getInstance().setLoginToken(response.data.getToken());
            ContentManager.getInstance().setUserInfo(response.data);
            Intent mIntent = new Intent(this, MainActivity.class);
            startActivity(mIntent);
            finish();
            MTing.getActivityManager().popAllActivitys();
        }
    }

    @Override
    public void onThirdPlatformError(int code, String errorMsg) {
        Toast.makeText(this,errorMsg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> response) {
        L.v(response.code);
        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
        if (response.data != null) {
            Intent mIntent = new Intent(this, GetCodeActivity.class);
            mIntent.putExtra(EXTRA_PHONE, phone);
            mIntent.putExtra(EXTRA_SOURCE, "bind_phone");
            mIntent.putExtra(GetCodeActivity.EXTRA_CODE, response.data.getCodeId());
            startActivity(mIntent);
        }
    }

    @Override
    public void onCodeError(int code, String errorMsg) {
        if(!TextUtils.isEmpty(errorMsg)) {
            toastShort(errorMsg);
        }
    }
}
