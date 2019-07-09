package cn.xylink.mting.speech.event;

import android.support.v7.widget.RecyclerView;

import cn.xylink.mting.bean.Article;

public class RecycleEvent
{
    private Article article;

    private RecycleEvent next;

    public Article getArticle()
    {
        return this.article;
    }

    public void setArticle(Article article)
    {
        this.article = article;
    }
}
