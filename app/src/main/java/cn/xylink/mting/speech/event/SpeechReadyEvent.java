package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechReadyEvent extends RecycleEvent {
    public SpeechReadyEvent(Article article) {
        super(article);
    }
}
