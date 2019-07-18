package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class FavoriteArticleRequest extends BaseRequest {
    String articleId;
    String type;

    public FavoriteArticleRequest(String articleId, boolean isStore) {
        this.articleId = articleId;
        this.type = (isStore)? "store" : "cancel";
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
