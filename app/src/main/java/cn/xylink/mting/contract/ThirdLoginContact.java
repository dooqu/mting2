package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.model.ThirdLoginRequset;

public interface ThirdLoginContact {
    interface IThirdLoginView extends IBaseView {
        void onThirdLoginSuccess(BaseResponse<UserInfo> loginInfoBaseResponse);

        void onThirdLoginError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onThirdLogin(ThirdLoginRequset loginRequest);
    }
}
