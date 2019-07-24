package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *
 *收藏
 * -----------------------------------------------------------------
 * 2019/7/11 20:09 : Create AddLoveRequest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class AddLoveRequest extends BaseRequest {
    private String articleId;
    private String type;//cancel：取消收藏，store：收藏

    public enum TYPE{
        CANCEL,
        STORE;
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
