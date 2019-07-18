package cn.xylink.mting.contract;

import java.util.List;

import cn.xylink.mting.bean.SearchRequeest;
import cn.xylink.mting.bean.SearchResultInfo;

/*
 *
 *搜索
 * -----------------------------------------------------------------
 * 2019/7/18 15:48 : Create SearchContract.java (JoDragon);
 * -----------------------------------------------------------------
 */
public interface SearchContract {
    interface ISearchView extends IBaseView {
        void onSuccessSearch(List<SearchResultInfo> unreadList);

        void onErrorSearch(int code, String errorMsg);
    }

    interface Presenter<T> {
        void search(SearchRequeest request);
    }
}
