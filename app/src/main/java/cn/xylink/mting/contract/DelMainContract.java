package cn.xylink.mting.contract;


import cn.xylink.mting.bean.AddLoveRequest;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.DelReadedRequest;
import cn.xylink.mting.bean.DelUnreadRequest;

/*
 *删除综合
 *
 * -----------------------------------------------------------------
 * 2019/7/11 20:03 : Create DelMainContract.java (JoDragon);
 * -----------------------------------------------------------------
 */
public interface DelMainContract {
    interface IDelMainView extends IBaseView {
        void onSuccessDel(String str);

        void onErrorDel(int code, String errorMsg);

        void onSuccessAddLove(String str, Article article);

        void onErrorAddLove(int code, String errorMsg);
    }

    interface Presenter<T> {
        void delUnread(DelUnreadRequest request);

        void delReaded(DelReadedRequest request);

        void delConllect(DelReadedRequest request);

        void addLove(AddLoveRequest request);

        void addLove(AddLoveRequest request, Article article);
    }
}
