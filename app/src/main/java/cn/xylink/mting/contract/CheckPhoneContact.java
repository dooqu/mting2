package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.model.CheckPhoneRequest;
import cn.xylink.mting.model.GetCodeRequest;

public interface CheckPhoneContact {
    interface ICheckPhoneView extends IBaseView {
        void onCheckPhoneSuccess(BaseResponse<CheckInfo> loginInfoBaseResponse);

        void onCheckPhoneError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onCheckPhone(CheckPhoneRequest loginRequest);
    }
}
