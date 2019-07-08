package cn.xylink.mting.contract;

import cn.xylink.mting.bean.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.bean.GetCodeRequest;
import cn.xylink.mting.bean.WxTokenInfo;
import cn.xylink.mting.bean.WxTokenRequset;

public interface WxChatContact {
    public interface IWxTokenView extends IBaseView {
        void onTokenSuccess(BaseResponse<WxTokenInfo> loginInfoBaseResponse);

        void onTokenError(int code, String errorMsg);
    }

   public  interface Presenter<T> {
        void onGetCode(WxTokenRequset loginRequest);
    }
}
