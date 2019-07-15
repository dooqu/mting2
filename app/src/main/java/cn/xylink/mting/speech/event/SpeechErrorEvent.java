package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechErrorEvent extends RecycleEvent {
    private int errorCode;
    private String message;

    public SpeechErrorEvent(int errorCode, String message, Article article) {
        this.errorCode = errorCode;
        this.message = message;

        this.setArticle(article);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return this.message;
    }
}
