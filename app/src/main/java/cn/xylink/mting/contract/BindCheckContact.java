package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.model.BindCheckRequest;

public interface BindCheckContact {
    interface IBindCheckView extends IBaseView {
        void onBindCheckSuccess(BaseResponse<String> loginInfoBaseResponse);

        void onBindCheckError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onBindCheck(BindCheckRequest loginRequest);
    }
}
