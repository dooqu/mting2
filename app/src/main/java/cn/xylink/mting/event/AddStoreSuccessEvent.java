package cn.xylink.mting.event;

import cn.xylink.mting.bean.Article;

/*
 *收藏成功通知
 *
 * -----------------------------------------------------------------
 * 2019/7/12 13:58 : Create AddStoreSuccessEvent.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class AddStoreSuccessEvent {
    private Article article;


    public AddStoreSuccessEvent() {
    }

    public AddStoreSuccessEvent(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
