package cn.xylink.mting.speech.event;

import cn.xylink.mting.model.Article;
import cn.xylink.mting.speech.Speechor;

public class SpeechStateChangedEvent extends  RecycleEvent {
    Speechor.SpeechorState state;
    public SpeechStateChangedEvent(Speechor.SpeechorState speechorState, Article article)
    {
        this.state = speechorState;
        this.setArticle(article);
    }

    public Speechor.SpeechorState getState()
    {
        return this.state;
    }
}
