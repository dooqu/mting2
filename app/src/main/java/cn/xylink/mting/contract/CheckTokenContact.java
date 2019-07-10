package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.model.CheckPhoneRequest;
import cn.xylink.mting.model.CheckTokenRequest;

public interface CheckTokenContact {
    interface ICheckTokenView extends IBaseView {
        void onCheckTokenSuccess(BaseResponse<UserInfo> response);

        void onCheckTokenError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onCheckToken(CheckTokenRequest request);
    }
}
