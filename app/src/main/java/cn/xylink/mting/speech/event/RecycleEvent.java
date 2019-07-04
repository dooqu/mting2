package cn.xylink.mting.speech.event;

import java.util.LinkedList;
import java.util.List;

import cn.xylink.mting.model.Article;

public class RecycleEvent
{
    private Article article;

    public Article getArticle()
    {
        return this.article;
    }

    public void setArticle(Article article)
    {
        this.article = article;
    }
}
