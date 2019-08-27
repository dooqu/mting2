package cn.xylink.mting.speech.event;

import java.util.List;

import cn.xylink.mting.bean.Article;

public class SpeechBufferingEvent extends RecycleEvent {
    private int frameIndex;
    private List<String> textFragments;

    public SpeechBufferingEvent(int frameIndex, List<String> textFragments, Article article) {
        this.frameIndex = frameIndex;
        this.textFragments = textFragments;
        this.setArticle(article);
    }

    public int getBufferingFrameIndex() {
        return this.frameIndex;
    }

    public List<String> getTextFragments() {
        return this.textFragments;
    }
}
