package cn.xylink.mting.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.xylink.mting.MTing;
import cn.xylink.mting.bean.BaseResponse;
import cn.xylink.mting.bean.LoginInfo;
import cn.xylink.mting.bean.WxTokenInfo;
import cn.xylink.mting.bean.WxTokenRequset;
import cn.xylink.mting.common.Const;
import cn.xylink.mting.contract.WxChatContact;
import cn.xylink.mting.contract.WxChatContact.IWxTokenView;
import cn.xylink.mting.model.WXQQDataBean;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.presenter.WxTokenPresenter;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;
import okhttp3.Call;

/**
 * Created by wjn on 2018/10/31.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler , WxChatContact.IWxTokenView{
    private IWXAPI api;
    private WxTokenPresenter wxTokenPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Const.WX_ID, true);
        api.registerApp(Const.WX_ID);
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wxTokenPresenter = new WxTokenPresenter();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    //微信发送请求到第三方应用时，会回调该方法
    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        L.v("nana", "errStr: " + baseResp.errStr + ",openId: " + baseResp.openId
                + ",transaction: " + baseResp.transaction + ",errCode: " + baseResp.errCode);
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                String code = ((SendAuth.Resp) baseResp).code;
                L.v("nana", "baseResp code:::: " + code);
                getAccessToken(code);

                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                finish();
                break;
            default:
                result = "发送返回";
                finish();
                break;
        }

    }

    private void getAccessToken(String code) {
        L.v("code",code);
        WxTokenRequset request = new WxTokenRequset();
        request.setAppid( Const.WX_ID);
        request.setSecret( Const.WX_SECRET);
        request.setCode(code);
        request.setGrant_type("authorization_code");
        L.v(request.toString());
        L.v("wxTokenPresenter",wxTokenPresenter);

        OkGoUtils.getInstance().postData(this, cn.xylink.mting.common.Const.WX_URL_BASE + "oauth2/access_token", new Gson().toJson(request), new TypeToken<BaseResponse<LoginInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<WxTokenInfo> baseResponse = (BaseResponse<WxTokenInfo>) data;
                int code = baseResponse.code;
                if (code == 200) {
                   onTokenSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), cn.xylink.mting.model.data.Const.FileName.USER_INFO_LOGIN, userInfoData);
                } else {
                    onTokenError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
               onTokenError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
//        wxTokenPresenter.onGetCode(requset);
//        OkHttpUtils.get().url(Const.WX_URL_BASE + "oauth2/access_token")
//                .addParams("appid", Const.WX_ID)
//                .addParams("secret", Const.WX_SECRET)
//                .addParams("code", code)
//                .addParams("grant_type", "authorization_code")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        L.v("nana", "getAccessToken response::: " + response);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String access_token = jsonObject.getString("access_token");
//                            String openid = jsonObject.getString("openid");
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        super.onAfter(id);
//                    }
//                });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onTokenSuccess(BaseResponse<WxTokenInfo> loginInfoBaseResponse) {
        WxTokenInfo info =  loginInfoBaseResponse.data;
        EventBus.getDefault().post(new WXQQDataBean(info.getAccess_token(), info.getOpenid(), "wechat"));
    }

    @Override
    public void onTokenError(int code, String errorMsg) {

    }

}
