package cn.xylink.mting.speech.event;

import cn.xylink.mting.bean.Article;

public class SpeechStopEvent extends  RecycleEvent{
    public static enum StopReason {
        ListIsNull,
        CountDownToZero
    }

    public SpeechStopEvent() {
        this.stopReason = StopReason.ListIsNull;
    }

    public SpeechStopEvent(StopReason reason) {
        this.stopReason = reason;
    }

    private StopReason stopReason;

    public StopReason getStopReason() {
        return stopReason;
    }

    public void setStopReason(StopReason stopReason) {
        this.stopReason = stopReason;
    }
}
