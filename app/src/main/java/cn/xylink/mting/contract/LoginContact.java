package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.LoginRequset;
import cn.xylink.mting.bean.UserInfo;

public interface LoginContact {
    interface ILoginView extends IBaseView {
        void onLoginSuccess(BaseResponse<UserInfo> response);

        void onLoginError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onLogin(LoginRequset request);
    }
}
