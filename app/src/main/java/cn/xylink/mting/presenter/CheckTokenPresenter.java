package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.CheckTokenContact;
import cn.xylink.mting.model.CheckTokenRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class CheckTokenPresenter extends BasePresenter<CheckTokenContact.ICheckTokenView> implements CheckTokenContact.Presenter {
    @Override
    public void onCheckToken(CheckTokenRequest request) {
        String json = new Gson().toJson(request);
        L.v("json", json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.checkTokenUrl(), json, new TypeToken<BaseResponse<UserInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                int code = baseResponse.code;
                L.v("coce", code);
                mView.onCheckTokenSuccess(baseResponse);
                String userInfoData = new Gson().toJson(baseResponse.data);
                FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
                if (baseResponse.data != null)
                    ContentManager.getInstance().setUserInfo(baseResponse.data);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onCheckTokenError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
