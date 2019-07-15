package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;

public interface CheckLinkContact {
    interface ICheckLinkView extends IBaseView {
        void onCheckLinkSuccess(BaseResponse<LinkArticle> response);

        void onCheckLinkError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onCheckLink(CheckLinkUrlRequset request);
    }
}
