package cn.xylink.mting.contract;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.model.InputCreateRequest;

public interface EditArticleContact {
    interface ICreateView extends IBaseView {
        void onSaveSuccess();

        void onSaveError();
    }

    interface Presenter<T> {
        void onEditNote(String id,String title,String content);
    }
}
