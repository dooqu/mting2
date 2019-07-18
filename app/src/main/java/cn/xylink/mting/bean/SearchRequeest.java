package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *搜索
 *
 * -----------------------------------------------------------------
 * 2019/7/18 15:42 : Create SearchRequeest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SearchRequeest extends BaseRequest {
    private String query;
    private int page;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
