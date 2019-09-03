package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.BindThirdPlatformContact;
import cn.xylink.mting.contract.CheckPhoneContact;
import cn.xylink.mting.model.CheckPhoneRequest;
import cn.xylink.mting.model.ThirdPlatformRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class ThirdPlatformPresenter extends BasePresenter<BindThirdPlatformContact.IThirdPlatformView> implements BindThirdPlatformContact.Presenter {
    @Override
    public void onThirdPlatform(ThirdPlatformRequest request) {
        L.v("request",request);
        String json = new Gson().toJson(request);
        L.v("json",json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.v1PlatformBind(),json , new TypeToken<BaseResponse<UserInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
                mView.showLoading();
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                int code = baseResponse.code;
                L.v("coce",code);
                if(code == 200) {
                    mView.onThirdPlatformSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
                }else
                {
                    mView.onThirdPlatformError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onThirdPlatformError(code, errorMsg);
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
            }
        });
    }
}
