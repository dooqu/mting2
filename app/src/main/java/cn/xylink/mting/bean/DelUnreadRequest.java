package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *删除待读
 *
 * -----------------------------------------------------------------
 * 2019/7/11 20:00 : Create DelUnreadRequest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class DelUnreadRequest extends BaseRequest {
    private String articleIds;

    public String getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(String articleIds) {
        this.articleIds = articleIds;
    }
}
