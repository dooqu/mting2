package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.model.SmsLoginRequset;

public interface SmsLoginContact {
    interface ISmsLoginView extends IBaseView {
        void onSmsLoginSuccess(BaseResponse<UserInfo> loginInfoBaseResponse);

        void onSmsLoginError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onSmsLogin(SmsLoginRequset loginRequest);
    }
}
