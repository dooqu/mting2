package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.bean.GetCodeRequest;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class GetCodePresenter extends BasePresenter<GetCodeContact.IGetCodeView> implements GetCodeContact.Presenter {
    @Override
    public void onGetCode(GetCodeRequest request) {
        L.v("request",request);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getCodeUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<CodeInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<CodeInfo> baseResponse = (BaseResponse<CodeInfo>) data;
                int code = baseResponse.code;
                L.v("coce",code);
                if (code == 200) {
                    mView.onCodeSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
                } else {
                    mView.onCodeError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onCodeError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
