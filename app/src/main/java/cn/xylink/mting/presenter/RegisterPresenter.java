package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.RegisterRequset;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.RegisterContact;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class RegisterPresenter extends BasePresenter<RegisterContact.IRegisterView> implements RegisterContact.Presenter {

    private static final int REGISTER_TYPE = 1;
    private static final int FORGOT_TYPE = 2;

    @Override
    public void onRegister(RegisterRequset request, final int requsetType) {
        L.v("request", request);
        String json = new Gson().toJson(request);
        L.v("json", json);
        String url = null;
        switch (requsetType) {
            case REGISTER_TYPE:
                url = RemoteUrl.registerUrl();
                break;
            case FORGOT_TYPE:
                url = RemoteUrl.forgotUrl();
                break;
            default:
                url = RemoteUrl.registerUrl();
                break;
        }
        L.v(url);
        OkGoUtils.getInstance().postData(mView, url, json, new TypeToken<BaseResponse<UserInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                int code = baseResponse.code;
                L.v("coce", code);
                if (code == 200) {
                    mView.onRegisterSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
                } else {
                    mView.onRegisterError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onRegisterError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
