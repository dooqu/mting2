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
    @Override
    public void onRegister(RegisterRequset request) {
        L.v("request",request);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.registerUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<UserInfo>>() {

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
