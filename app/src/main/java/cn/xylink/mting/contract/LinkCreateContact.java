package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.model.LinkCreateRequest;

public interface LinkCreateContact {
    interface IPushView extends IBaseView {
        void onPushSuccess(BaseResponse<LinkArticle> loginInfoBaseResponse);

        void onPushError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onPush(LinkCreateRequest loginRequest);
    }
}
