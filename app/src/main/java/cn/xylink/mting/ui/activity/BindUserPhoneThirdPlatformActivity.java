package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.utils.LogUtils;

/**
 * Created by wjn on 2018/10/31.
 */
//绑定用户手机号页面
public class BindUserPhoneThirdPlatformActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.imv_head_img)
    ImageView imv_head_img;
    @BindView(R.id.tv_nick_name)
    TextView tv_nick_name;
    @BindView(R.id.et_phone_bind_wx)
    EditText et_phone_bind_wx;
    @BindView(R.id.btn_next_no)
    Button btn_next_no;
    @BindView(R.id.btn_next_yes)
    Button btn_next_yes;
    private String access_token;
    private String openId;
    private String type;

    public static final String ACCESSTOKEN = "ACCESSTOKEN";
    public static final String OPENID = "OPENID";
    public static final String TYPE = "TYPE";


    @Override
    protected void preView() {
        setContentView(R.layout.activity_bind_user_phone_from_wechat);
    }

    @Override
    protected void initView() {
        et_phone_bind_wx.addTextChangedListener(this);

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        access_token = intent.getStringExtra(LoginActivity.ACCESS_TOKEN);
        openId = intent.getStringExtra(LoginActivity.OPEN_ID);
        type = intent.getStringExtra(LoginActivity.TYPE);
        String nickName = intent.getStringExtra(LoginActivity.NICK_NAME);
        String headImg = intent.getStringExtra(LoginActivity.HEAD_IMG);
        LogUtils.e("nana", "access_token: " + access_token + "\n openId: " + openId + "\n nickName: " + nickName + "\nheadImg: " + headImg + "\ntype: " + type);
        if (TextUtils.isEmpty(nickName)) {
            tv_nick_name.setText("亲爱的");
        } else {
            tv_nick_name.setText(nickName);
        }

//        if (TextUtils.isEmpty(headImg)) {
//            Glide.with(BindUserPhoneThirdPlatformActivity.this).load(R.mipmap.ic_launcher)
//                    .transform(new GlideCircleTransform(BindUserPhoneThirdPlatformActivity.this))
//                    .into(imv_head_img);
//        } else {
//            Glide.with(BindUserPhoneThirdPlatformActivity.this).load(headImg)
//                    .transform(new GlideCircleTransform(BindUserPhoneThirdPlatformActivity.this))
//                    .into(imv_head_img);
//        }

    }

    @Override
    protected void initTitleBar() {

    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (et_phone_bind_wx.getText().toString().replace(" ", "").trim().length() == 11) {
            btn_next_no.setVisibility(View.GONE);
            btn_next_yes.setVisibility(View.VISIBLE);
        } else {
            btn_next_no.setVisibility(View.VISIBLE);
            btn_next_yes.setVisibility(View.GONE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @OnClick({R.id.root, R.id.btn_next_yes})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.root:
                hideSoftInput();
                break;
            case R.id.btn_next_yes:
//                String phoneNo = et_phone_bind_wx.getText().toString().trim().replace(" ", "");
//                if (phoneNo.length() == 11 && PhoneNumberUtils.isMobileNO(phoneNo)) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Const.AccountInfo.FLAG, Const.AccountInfo.BIND_USER_PHONE);
//                    bundle.putString(Const.AccountInfo.PHONE_NO, phoneNo);
//                    bundle.putString(ACCESSTOKEN, access_token);
//                    bundle.putString(OPENID, openId);
//                    bundle.putString(TYPE, type);
//                    jumpActivity(GetCodeActivity.class, bundle);
//                } else {
//                    toastShort("手机号输入有误");
//                }
                break;
        }
    }
}
