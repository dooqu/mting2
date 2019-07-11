package cn.xylink.mting.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.xylink.mting.common.Const;
import cn.xylink.mting.model.WXQQDataBean;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.utils.L;

/**
 * Created by wjn on 2018/10/31.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

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
        L.v("code", code);

        HashMap<String, String> parmas = new HashMap<>();
        parmas.put("appid", Const.WX_ID);
        parmas.put("secret", Const.WX_SECRET);
        parmas.put("code", code);
        parmas.put("grant_type", "authorization_code");

        OkGoUtils.getInstance().postParamsData(cn.xylink.mting.common.Const.WX_URL_BASE + "oauth2/access_token", parmas, new TypeToken<JsonObject>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object data) {
                L.v("data", data);
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    String access_token = jsonObject.getString("access_token");
                    String openid = jsonObject.getString("openid");
                    EventBus.getDefault().post(new WXQQDataBean(access_token, openid, "wechat",null));
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



}
