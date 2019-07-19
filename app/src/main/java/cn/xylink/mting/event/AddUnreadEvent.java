package cn.xylink.mting.event;

/*
 *添加到未读通知
 *
 * -----------------------------------------------------------------
 * 2019/7/19 15:38 : Create AddUnreadEvent.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class AddUnreadEvent {
    private String articleID;

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }
}
