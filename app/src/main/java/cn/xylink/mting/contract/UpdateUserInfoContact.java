package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.model.BindCheckRequest;
import cn.xylink.mting.model.UpdateUserRequset;

public interface UpdateUserInfoContact {
    interface IUpdateUserView extends IBaseView {
        void onUpdateUserSuccess(BaseResponse<UserInfo>  response);

        void onUpdateUserError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onUpdateUser(UpdateUserRequset request);
    }
}
