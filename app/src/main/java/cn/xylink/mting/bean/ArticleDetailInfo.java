package cn.xylink.mting.bean;

/*
 *文章详情
 *
 * -----------------------------------------------------------------
 * 2019/7/19 11:50 : Create ArticleDetailInfo.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ArticleDetailInfo {

    /**
     * articleId : 2018121314432636857507904
     * title : 孟晚舟获保释 华为发布声明:期待美国加拿大给出公正结论_手机搜狐网
     * url : https://m.sohu.com/a/281228304_116897?_f=m-index_important_news_2&strategyid=00014&
     * picture : null
     * content : 中国网12月12日讯
     * 被加拿大拘押的中国公民孟晚舟当地时间11日下午获得保释。华为随后在官方声明中表示，相信加拿大和美国的法律体系后续会给出公正的结论。华为遵守业务所在国的所有适用法律法规，包括联合国、美国和欧盟适用的出口管制和制裁法律法规。华为期待美国和加拿大政府能及时、公正的结束这一事件。具体内容如下：
     各位关心华为的朋友们，
     针对11日听证会，华为官方声明如下：
     全新BMW X5，瞩目登场 广告
     我们的CFO
     孟晚舟女士近期在被加拿大当局代表美国政府暂时扣留之后，今天法庭做出判决，同意保释。我们相信加拿大和美国的法律体系后续会给出公正的结论。正如我们一直强调的，华为遵守业务所在国的所有适用法律法规，包括联合国、美国和欧盟适用的出口管制和制裁法律法规。我们期待美国和加拿大政府能及时、公正的结束这一事件。
     The Canadian authorities have agreed to release our CFO, Ms. Meng Wanzhou, on bail following her detainment on behalf of the United States of
     America. We have every confidence that the Canadian and US legal systems will reach a just conclusion in the following proceedings. As we have
     stressed all along, Huawei complies with all applicable laws and regulations in the countries and regions where we operate, including export
     control and sanction laws of the UN, US, and EU. We look forward to a timely resolution of this matter.
     华为
     作者：辛闻
     * sourceName :
     * sourceLogo : null
     * store : 0
     * read : 0
     * createAt : 1544683406368
     * updateAt : 1544683406368
     * progress : 0
     * shareUrl : http://localhost:8080/api/sct/common/details/2018121314432636857507904
     */

    private String articleId;
    private String title;
    private String url;
    private Object picture;
    private String content;
    private String sourceName;
    private Object sourceLogo;
    private int store;
    private int read;
    private long createAt;
    private long updateAt;
    private float progress;
    private String shareUrl;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getPicture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Object getSourceLogo() {
        return sourceLogo;
    }

    public void setSourceLogo(Object sourceLogo) {
        this.sourceLogo = sourceLogo;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
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

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
