package cn.xylink.mting.bean;


public class Article
{
    String articleId;
    String title;
    String content;
    int inType;
    String url;
    String shareUrl;
    String sourceName;
    String sourceLogo;
    String picture;
    long updateAt;
    float progress;
    int store;
    int read;
    String id;
    boolean isChecked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public String getArticleId()
    {
        return this.articleId;
    }


    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return this.content;
    }

    public void setTextBody(String content) {
        this.content = content;
    }


    public String getTextBody()
    {
        return this.content;
    }

    public void setShareUrl(String shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getShareUrl()
    {
        return shareUrl;
    }

    public void setSourceName(String sourceName)
    {
        this.sourceName = sourceName;
    }

    public String getSourceName()
    {
        return this.sourceName;
    }

    public void setSourceLogo(String sourceLogo)
    {
        this.sourceLogo = sourceLogo;
    }

    public String getSourceLogo()
    {
        return this.sourceLogo;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    public String getPicture()
    {
        return picture;
    }

    public void setUpdateAt(long updateAt){
        this.updateAt = updateAt;
    }

    public long getUpdateAt()
    {
        return updateAt;
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
    }

    public float getProgress()
    {
        return progress;
    }

    public int getRead()
    {
        return read;
    }

    public void setRead( int read)
    {
        this.read = read;
    }

    public void setStore(int store)
    {
        this.store = store;
    }

    public int getStore()
    {
        return store;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getInType() {
        return inType;
    }

    public void setInType(int inType) {
        this.inType = inType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public Article clone() {
        Article article = new Article();
        article.setArticleId(this.getArticleId());
        article.setTitle(this.getTitle());
        article.setContent(this.getContent());
        article.setTextBody(this.getTextBody());
        article.setShareUrl(this.getShareUrl());
        article.setSourceName(this.getSourceName());
        article.setSourceLogo(this.getSourceLogo());
        article.setPicture(this.getPicture());
        article.setUpdateAt(this.getUpdateAt());
        article.setProgress(this.getProgress());
        article.setRead(this.getRead());
        article.setId(this.getId());
        article.setChecked(this.isChecked());
        article.setUrl(this.getUrl());
        article.setInType(this.getInType());
        return article;
    }
}
