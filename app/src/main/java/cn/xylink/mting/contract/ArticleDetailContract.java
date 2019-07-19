package cn.xylink.mting.contract;

import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.ArticleDetailRequest;

/*
 *文章详情
 *
 * -----------------------------------------------------------------
 * 2019/7/19 11:53 : Create ArticleDetailContract.java (JoDragon);
 * -----------------------------------------------------------------
 */
public interface ArticleDetailContract {
    interface IArticleDetailView extends IBaseView {
        void onSuccessArticleDetail(ArticleDetailInfo info);

        void onErrorArticleDetail(int code, String errorMsg);
    }

    interface Presenter<T> {
        void createArticleDetail(ArticleDetailRequest request);
    }
}
