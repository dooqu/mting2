package cn.xylink.mting.speech.event;

import java.util.List;

import cn.xylink.mting.bean.Article;

public class SpeechProgressEvent extends RecycleEvent {
    private int frameIndex;
    private List<String> textFragments;

    public SpeechProgressEvent(int frameIndex, List<String> textFragments, Article article) {
        this.frameIndex = frameIndex;
        this.textFragments = textFragments;
        this.setArticle(article);
    }

    public int getFrameIndex() {
        return this.frameIndex;
    }

    public List<String> getTextFragments() {
        return this.textFragments;
    }
}
