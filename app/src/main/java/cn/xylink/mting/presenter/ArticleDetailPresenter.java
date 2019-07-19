package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.base.BaseResponseArray;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.ArticleDetailRequest;
import cn.xylink.mting.contract.ArticleDetailContract;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;

/*
 *文章详情
 *
 * -----------------------------------------------------------------
 * 2019/7/19 11:54 : Create ArticleDetailPresenter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ArticleDetailPresenter extends BasePresenter<ArticleDetailContract.IArticleDetailView> implements ArticleDetailContract.Presenter {
    @Override
    public void createArticleDetail(ArticleDetailRequest request) {
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getArticDetailUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<ArticleDetailInfo>>() {
        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<ArticleDetailInfo> baseResponse = (BaseResponse<ArticleDetailInfo>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onSuccessArticleDetail(baseResponse.data);
                } else {
                    mView.onErrorArticleDetail(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onErrorArticleDetail(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
