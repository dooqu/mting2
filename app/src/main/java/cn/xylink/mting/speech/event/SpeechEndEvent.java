package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechEndEvent extends RecycleEvent {
    float progress;
    public SpeechEndEvent(Article article, float progress)
    {
        super(article);
        this.progress = progress;
    }

    public float getProgress()
    {
        return this.progress;
    }
}
