package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.ui.activity.user.BindLoginPwdActivity;
import cn.xylink.mting.utils.ImageUtils;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.SharedPreHelper;
import cn.xylink.mting.widget.ZpPhoneEditText;

public class BindingPhoneQQWxActivity extends BasePresenterActivity {

    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.iv_qq_wx)
    ImageView ivQQWx;
    @BindView(R.id.iv_xyl_icon)
    ImageView ivXyl;
    @BindView(R.id.et_phone)
    ZpPhoneEditText etPhone;
    @BindView(R.id.tv_bind_name)
    TextView tvBindName;
    @BindView(R.id.tv_xyl_name)
    TextView tvXylName;

    String phone;
    String source;
    String platform;
    com.tencent.connect.UserInfo mInfo;

    SharedPreHelper sharedPreHelper;
    @Override
    protected void preView() {
        setContentView(R.layout.activity_binding_phone_wx_qq);
        sharedPreHelper = SharedPreHelper.getInstance(this);
        MTing.getActivityManager().pushActivity(this);
    }

    @Override
    protected void initView() {
        tvTitle.setText("账号绑定");


        ImageUtils.get().loadCircle(ivXyl,"");


    }

    public void getWxUserInfo(){
        String access_token = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.ACCESS_TOKEN,"");
        String openId = (String) sharedPreHelper.getSharedPreference(SharedPreHelper.SharedAttribute.OPENID,"");
        String url = cn.xylink.mting.common.Const.WX_URL_BASE + "userinfo?access_token=" + access_token + "&openid=" + openId;
        OkGoUtils.getInstance().postParamsData(url, new HashMap<>(), new TypeToken<JsonObject>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object data) {
                L.v("data", data);
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    String headimgurl = jsonObject.getString("headimgurl");
                    String nickname = jsonObject.getString("nickname");
                    ImageUtils.get().loadCircle(ivQQWx,headimgurl);
                    tvBindName.setText(nickname);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int code, String errorMsg) {
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void getUserInfo() {
        Tencent mTencent = QQApi.getInstance();
        QQToken token = mTencent.getQQToken();
        mInfo = new com.tencent.connect.UserInfo(this, token);
        mInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                JSONObject jb = (JSONObject) object;
                L.v("jb",jb);
                try {
                 String  name = jb.getString("nickname");
                 String  figureurl = jb.getString("figureurl_qq_2");  //头像图片的url

                    /*Log.i("imgUrl",figureurl.toString()+"");*/
                    /*Glide.with(MainActivity.this).load(figureurl).into(figure);*/
                    ImageUtils.get().loadCircle(ivQQWx,figureurl);
                    tvBindName.setText(name);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
            }

            @Override
            public void onCancel() {
            }
        });
    }


    @Override
    protected void initData() {
        phone = getIntent().getStringExtra(BindingPhoneActivity.EXTRA_PHONE);
        source = getIntent().getStringExtra(BindingPhoneActivity.EXTRA_SOURCE);
        platform = getIntent().getStringExtra(BindingPhoneActivity.EXTRA_PLATFORM);
        etPhone.setText(phone);

        if(platform.equals("qq")){
            getUserInfo();
        }else{
            getWxUserInfo();
        }
    }



    @OnClick({R.id.btn_binding,R.id.tv_change_phone,R.id.btn_left})
    public void onClick(View v){
        final int id = v.getId();
        switch (id){
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_binding: {
                Intent mIntent = new Intent(this, BindLoginPwdActivity.class);
                mIntent.putExtra(BindLoginPwdActivity.EXTRA_PHONE,phone);
                mIntent.putExtra(BindLoginPwdActivity.EXTRA_PLATFORM,platform);
                startActivity(mIntent);
                break;
            }
            case R.id.tv_change_phone: {
                Intent mIntent = new Intent(this, BindingPhoneActivity.class);
                mIntent.putExtra(BindingPhoneActivity.EXTRA_PLATFORM, platform);
                mIntent.putExtra(BindingPhoneActivity.EXTRA_SOURCE, source);
                finish();
                break;
            }
        }
    }


    @Override
    protected void initTitleBar() {

    }


}
