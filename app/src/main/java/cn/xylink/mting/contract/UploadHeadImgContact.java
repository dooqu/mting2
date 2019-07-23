package cn.xylink.mting.contract;

import java.io.File;
import java.util.Map;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UserInfo;


public interface UploadHeadImgContact {
    interface IUploadHeadImgView extends IBaseView {
        void onSuccessUploadHeadImg(BaseResponse<UserInfo> baseResponse);

        void onErrorUploadHeadImg(int code, String errorMsg);
    }

    interface Presenter<T> {
        void uploadHeadImg(Map<String, String> data, File file);
    }
}
