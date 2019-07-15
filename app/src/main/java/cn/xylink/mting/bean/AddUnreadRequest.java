package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *加入待读
 *
 * -----------------------------------------------------------------
 * 2019/7/15 16:11 : Create AddUnreadRequest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class AddUnreadRequest extends BaseRequest {
    String articleIds;

    public String getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(String articleIds) {
        this.articleIds = articleIds;
    }
}
