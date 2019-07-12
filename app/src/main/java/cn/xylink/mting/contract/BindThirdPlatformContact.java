package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.model.BindCheckRequest;
import cn.xylink.mting.model.ThirdPlatformRequest;

public interface BindThirdPlatformContact {
    interface IThirdPlatformView extends IBaseView {
        void onThirdPlatformSuccess(BaseResponse<UserInfo> loginInfoBaseResponse);

        void onThirdPlatformError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onThirdPlatform(ThirdPlatformRequest loginRequest);
    }
}
