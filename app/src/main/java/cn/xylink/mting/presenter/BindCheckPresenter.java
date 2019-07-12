package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.contract.BindCheckContact;
import cn.xylink.mting.contract.CheckPhoneContact;
import cn.xylink.mting.model.BindCheckRequest;
import cn.xylink.mting.model.CheckPhoneRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class BindCheckPresenter extends BasePresenter<BindCheckContact.IBindCheckView> implements BindCheckContact.Presenter {
    @Override
    public void onBindCheck(BindCheckRequest request) {
        L.v("request",request);
        String json = new Gson().toJson(request);
        L.v("json",json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.bindCheckUrl(),json , new TypeToken<BaseResponse<String>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                L.v("baseResponse",baseResponse);
                    mView.onBindCheckSuccess(baseResponse);
                    String userInfoData = new Gson().toJson(baseResponse.data);
                    FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO_LOGIN, userInfoData);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onBindCheckError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
