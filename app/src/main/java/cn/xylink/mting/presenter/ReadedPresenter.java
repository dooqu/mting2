package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponseArray;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UnreadRequest;
import cn.xylink.mting.contract.UnreadContract;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;

/*
 *已读
 *
 * -----------------------------------------------------------------
 * 2019/7/10 14:12 : Create ReadedPresenter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ReadedPresenter extends BasePresenter<UnreadContract.IUnreadView> implements UnreadContract.Presenter {
    @Override
    public void createUnread(UnreadRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getReadedUrl(), new Gson().toJson(request), new TypeToken<BaseResponseArray<Article>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponseArray<Article> baseResponse = (BaseResponseArray<Article>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessUnread(baseResponse.list, 0);
                } else {
                    mView.onErrorUnread(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorUnread(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
