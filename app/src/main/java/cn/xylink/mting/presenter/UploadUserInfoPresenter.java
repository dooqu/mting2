package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.Map;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.UpdateUserInfoContact;
import cn.xylink.mting.contract.UploadHeadImgContact;
import cn.xylink.mting.model.UpdateUserRequset;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.L;

public class UploadUserInfoPresenter extends BasePresenter<UpdateUserInfoContact.IUpdateUserView> implements UpdateUserInfoContact.Presenter {
    @Override
    public void onUpdateUser(UpdateUserRequset request) {
        String json = new Gson().toJson(request);
        L.v(json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.updateUserUrl(), json,
                new TypeToken<BaseResponse<UserInfo>>() {

                }.getType(), new OkGoUtils.ICallback() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onSuccess(Object data) {
                        BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                        int code = baseResponse.code;
                        if (code == 200) {
                            mView.onUpdateUserSuccess(baseResponse);
                        } else {
                            mView.onUpdateUserError(code, baseResponse.message);
                        }

                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        mView.onUpdateUserError(code, errorMsg);
                    }

                    @Override
                    public void onComplete() {
                        mView.hideLoading();
                    }
                });
    }
}
