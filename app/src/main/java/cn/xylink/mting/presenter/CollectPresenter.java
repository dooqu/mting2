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
 *收藏
 *
 * -----------------------------------------------------------------
 * 2019/7/11 15:38 : Create CollectPresenter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class CollectPresenter extends BasePresenter<UnreadContract.IUnreadView> implements UnreadContract.Presenter {
    @Override
    public void createUnread(UnreadRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getStoreUrl(), new Gson().toJson(request), new TypeToken<BaseResponseArray<Article>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponseArray<Article> baseResponse = (BaseResponseArray<Article>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessUnread(baseResponse.list);
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
