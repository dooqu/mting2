package cn.xylink.mting.contract;

import java.util.List;

import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UnreadRequest;

/*
 *待读
 *
 * -----------------------------------------------------------------
 * 2019/7/9 19:28 : Create UnreadContract.java (JoDragon);
 * -----------------------------------------------------------------
 */
public interface UnreadContract {
    interface IUnreadView extends IBaseView {
        void onSuccessUnread(List<Article> unreadList);

        void onErrorUnread(int code, String errorMsg);
    }

    interface Presenter<T> {
        void createUnread(UnreadRequest request);
    }
}
