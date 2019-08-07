package cn.xylink.mting.contract;

import cn.xylink.mting.bean.AddUnreadRequest;
import cn.xylink.mting.bean.Article;

/*
 *加入待读
 *
 * -----------------------------------------------------------------
 * 2019/7/15 16:12 : Create AddUnreadContract.java (JoDragon);
 * -----------------------------------------------------------------
 */
public interface AddUnreadContract {

    interface IAddUnreadView extends IBaseView {
        void onSuccessAddUnread(Article article);

        void onErrorAddUnread(int code, String errorMsg);
    }

    interface Presenter<T> {
        void addUnread(AddUnreadRequest request);
    }
}
