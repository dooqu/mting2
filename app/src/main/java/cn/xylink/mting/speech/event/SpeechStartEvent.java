package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechStartEvent extends RecycleEvent {

    public SpeechStartEvent(Article article) {
        this.setArticle(article);
    }
}
