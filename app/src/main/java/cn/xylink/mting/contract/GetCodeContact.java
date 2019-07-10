package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.model.GetCodeRequest;

public interface GetCodeContact {
    interface IGetCodeView extends IBaseView {
        void onCodeSuccess(BaseResponse<CodeInfo> loginInfoBaseResponse);

        void onCodeError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onGetCode(GetCodeRequest loginRequest);
    }
}
