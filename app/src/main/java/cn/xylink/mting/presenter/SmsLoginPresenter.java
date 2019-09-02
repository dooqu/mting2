package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.SmsLoginContact;
import cn.xylink.mting.model.SmsLoginRequset;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.L;

public class SmsLoginPresenter extends BasePresenter<SmsLoginContact.ISmsLoginView> implements SmsLoginContact.Presenter {
    @Override
    public void onSmsLogin(SmsLoginRequset request) {
        L.v("request",request);


        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(request);
        L.v(json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.smsLogin(),json , new TypeToken<BaseResponse<UserInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
                mView.showLoading();
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                int code = baseResponse.code;
                if(code == 200) {
                    mView.onSmsLoginSuccess(baseResponse);
                }else
                {
                    mView.onSmsLoginError(code,baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onSmsLoginError(code, errorMsg);
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
            }
        });
    }
}
