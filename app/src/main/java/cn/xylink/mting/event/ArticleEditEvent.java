package cn.xylink.mting.event;


public class ArticleEditEvent {

    public ArticleEditEvent(String articleID) {
        this.articleID = articleID;
    }

    private String articleID;

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }
}
