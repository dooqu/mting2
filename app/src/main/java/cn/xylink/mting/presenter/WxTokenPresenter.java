package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.bean.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.bean.GetCodeRequest;
import cn.xylink.mting.bean.LoginInfo;
import cn.xylink.mting.bean.WxTokenInfo;
import cn.xylink.mting.bean.WxTokenRequset;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.contract.WxChatContact;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class WxTokenPresenter extends BasePresenter<WxChatContact.IWxTokenView> implements WxChatContact.Presenter {
    @Override
    public void onGetCode(WxTokenRequset request) {
        L.v(request.toString());
        OkGoUtils.getInstance().postData(mView, cn.xylink.mting.common.Const.WX_URL_BASE + "oauth2/access_token", new Gson().toJson(request), new TypeToken<BaseResponse<LoginInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<WxTokenInfo> baseResponse = (BaseResponse<WxTokenInfo>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onTokenSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
                } else {
                    mView.onTokenError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onTokenError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
