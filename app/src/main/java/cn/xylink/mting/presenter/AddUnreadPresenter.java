package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.AddUnreadRequest;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.contract.AddUnreadContract;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;

/*
 *加入待读
 *
 * -----------------------------------------------------------------
 * 2019/7/15 16:13 : Create AddUnreadPresenter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class AddUnreadPresenter extends BasePresenter<AddUnreadContract.IAddUnreadView> implements AddUnreadContract.Presenter {
    @Override
    public void addUnread(AddUnreadRequest request) {
            OkGoUtils.getInstance().postData(mView, RemoteUrl.addUnreadUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<Article>>() {
            }.getType(), new OkGoUtils.ICallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(Object data) {
                    BaseResponse<Article> baseResponse = (BaseResponse<Article>) data;
                    int code = baseResponse.code;
                    if (code == 200) {
                        mView.onSuccessAddUnread(baseResponse.data);
                    } else {
                        mView.onErrorAddUnread(code, baseResponse.message);
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    mView.onErrorAddUnread(code, errorMsg);
                }

                @Override
                public void onComplete() {

                }
            });
        }
}
