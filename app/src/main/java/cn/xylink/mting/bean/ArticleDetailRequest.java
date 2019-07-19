package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *文章详情
 *
 * -----------------------------------------------------------------
 * 2019/7/19 11:49 : Create ArticleDetailRequest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ArticleDetailRequest extends BaseRequest {
    private String articleId;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
}
