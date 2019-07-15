package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.model.BindCheckRequest;
import cn.xylink.mting.model.InputCreateRequest;

public interface InputCreateContact {
    interface ICreateView extends IBaseView {
        void onCreateSuccess(BaseResponse<Article> loginInfoBaseResponse);

        void onCreateError(int code, String errorMsg);
    }

    interface Presenter<T> {
        void onCreateNote(InputCreateRequest request);
    }
}
