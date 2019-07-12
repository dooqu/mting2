package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.LoginContact;
import cn.xylink.mting.contract.ThirdLoginContact;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.model.ThirdLoginRequset;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class ThirdLoginPresenter extends BasePresenter<ThirdLoginContact.IThirdLoginView> implements ThirdLoginContact.Presenter {
    @Override
    public void onThirdLogin(ThirdLoginRequset request) {
        String json = new Gson().toJson(request);
        L.v(json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.thirdLoginUrl(), json, new TypeToken<BaseResponse<UserInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                int code = baseResponse.code;
                L.v("baseResponse",baseResponse.toString() );
                mView.onThirdLoginSuccess(baseResponse);
                String userInfoData = new Gson().toJson(baseResponse.data);
                FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onThirdLoginError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
