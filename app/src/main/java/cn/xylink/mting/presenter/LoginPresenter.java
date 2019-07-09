package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.LoginContact;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class LoginPresenter extends BasePresenter<LoginContact.ILoginView> implements LoginContact.Presenter {
    @Override
    public void onLogin(LoginRequset request) {
        L.v("request",request);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.loginUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<UserInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                int code = baseResponse.code;
                L.v("coce",code);
                if (code == 200) {
                    mView.onLoginSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
                } else {
                    mView.onLoginError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onLoginError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
