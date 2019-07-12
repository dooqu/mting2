package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.AddLoveRequest;
import cn.xylink.mting.bean.DelReadedRequest;
import cn.xylink.mting.bean.DelUnreadRequest;
import cn.xylink.mting.contract.DelMainContract;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;

/*
 *删除综合
 *
 * -----------------------------------------------------------------
 * 2019/7/11 20:07 : Create DelMainPresenter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class DelMainPresenter extends BasePresenter<DelMainContract.IDelMainView> implements DelMainContract.Presenter{
    @Override
    public void delUnread(DelUnreadRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getDelUnreadUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<String>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessDel(baseResponse.message);
                } else {
                    mView.onErrorDel(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorDel(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void delReaded(DelReadedRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getDelReadedUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<String>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessDel(baseResponse.message);
                } else {
                    mView.onErrorDel(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorDel(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void delConllect(DelReadedRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getDelStoreUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<String>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessDel(baseResponse.message);
                } else {
                    mView.onErrorDel(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorDel(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void addLove(AddLoveRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getAddStoreUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<String>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessAddLove(baseResponse.message);
                } else {
                    mView.onErrorAddLove(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorAddLove(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
