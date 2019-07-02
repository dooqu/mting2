package cn.xylink.mting.model;

public class Article
{
    String articleId;
    String title;
    String textBody;


    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setTextBody(String textBody)
    {
        this.textBody = textBody;
    }

    public String getArticleId()
    {
        return this.articleId;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getTextBody()
    {
        return this.textBody;
    }
}
