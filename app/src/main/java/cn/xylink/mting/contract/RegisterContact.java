package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.RegisterRequset;
import cn.xylink.mting.bean.UserInfo;

public interface RegisterContact {
    interface IRegisterView extends IBaseView {
        void onRegisterSuccess(BaseResponse<UserInfo> loginInfoBaseResponse);

        void onRegisterError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onRegister(RegisterRequset request,int requsetType);
    }
}
