package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechArticleStatusSavedOnServerEvent extends RecycleEvent {

    private int errorCode;
    private String message;

    public SpeechArticleStatusSavedOnServerEvent(int errorCode, String mesage, Article article) {
        super(article);
    }


    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isSuccessed()
    {
        return this.errorCode == 0;
    }
}
