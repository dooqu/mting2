package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;
import cn.xylink.mting.speech.event.RecycleEvent;

public class SpeechPauseEvent extends RecycleEvent {

    public SpeechPauseEvent(Article article) {
        super(article);
    }
}
