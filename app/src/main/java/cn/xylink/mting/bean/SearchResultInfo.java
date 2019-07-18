package cn.xylink.mting.bean;

/*
 *搜索
 *
 * -----------------------------------------------------------------
 * 2019/7/18 15:46 : Create SearchResultInfo.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SearchResultInfo {


    /**
     * userId : 20181026104248577819
     * articleId : 20181213145222710222
     * url : https://m.sohu.com/a/281228304_116897?_f=m-index_important_news_2&strategyid=00014&1
     * title : 孟晚舟获保释 <font color='#007d7b'>华</font><font color='#007d7b'>为</font>发布声明:期待美国加拿大给出公正结论_手机搜狐网
     * content : 123
     * createAt : 1544683942875
     * updateAt : 1
     */

    private String userId;
    private String articleId;
    private String url;
    private String title;
    private String content;
    private long createAt;
    private long updateAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }
}
