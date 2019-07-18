package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponseArray;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.SearchRequeest;
import cn.xylink.mting.bean.SearchResultInfo;
import cn.xylink.mting.contract.SearchContract;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;

/*
 *搜索
 *
 * -----------------------------------------------------------------
 * 2019/7/18 15:49 : Create SearchPresenter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SearchPresenter extends BasePresenter<SearchContract.ISearchView> implements SearchContract.Presenter{
    @Override
    public void search(SearchRequeest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getSearchUrl(), new Gson().toJson(request), new TypeToken<BaseResponseArray<SearchResultInfo>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponseArray<SearchResultInfo> baseResponse = (BaseResponseArray<SearchResultInfo>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessSearch(baseResponse.data);
                } else {
                    mView.onErrorSearch(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorSearch(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
