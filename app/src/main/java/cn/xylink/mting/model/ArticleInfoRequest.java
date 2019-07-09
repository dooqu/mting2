package cn.xylink.mting.model;

import cn.xylink.mting.bean.Article;

public class ArticleInfoRequest extends cn.xylink.mting.base.BaseRequest {

    protected String articleId;

    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public String getArticleId()
    {
        return this.articleId;
    }
}
