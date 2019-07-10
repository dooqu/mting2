package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechEndEvent extends RecycleEvent {
    public SpeechEndEvent(Article article)
    {
        this.setArticle(article);
    }
}
