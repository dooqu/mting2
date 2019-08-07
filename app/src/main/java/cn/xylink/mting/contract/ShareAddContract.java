package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.ArticleDetailRequest;
import cn.xylink.mting.model.ArticleInfoRequest;

public interface ShareAddContract {
    interface IShareAddView extends IBaseView {
        void onShareAddSuccess(BaseResponse<ArticleDetailInfo> info);

        void onShareAddError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void shareAdd(ArticleInfoRequest request);
    }
}
