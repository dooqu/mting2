package cn.xylink.mting.presenter;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.Map;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.UploadHeadImgContact;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;

public class UploadHeadImgPresenter extends BasePresenter<UploadHeadImgContact.IUploadHeadImgView> implements UploadHeadImgContact.Presenter {
    @Override
    public void uploadHeadImg(Map data, File file) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.upLoadHeadImg(), data, file,
                new TypeToken<BaseResponse<UserInfo>>() {

                }.getType(), new OkGoUtils.ICallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(Object data) {
                        BaseResponse<UserInfo> baseResponse = (BaseResponse<UserInfo>) data;
                        int code = baseResponse.code;
                        if (code == 200) {
                            mView.onSuccessUploadHeadImg(baseResponse);
                        } else {
                            mView.onErrorUploadHeadImg(code, baseResponse.message);
                        }

                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        mView.onErrorUploadHeadImg(code, errorMsg);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
