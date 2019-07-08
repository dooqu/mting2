package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

public class Article
{
    String articleId;
    String title;
    String content;
    String shareUrl;
    String sourceName;
    String sourceLogo;
    String picture;
    long updateAt;
    float progress;
    int store;
    int read;


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
}
