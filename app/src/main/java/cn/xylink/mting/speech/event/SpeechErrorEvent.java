package cn.xylink.mting.speech.event;

public class SpeechErrorEvent extends RecycleEvent<SpeechErrorEvent>
{
    private int errorCode;
    private String message;

    public SpeechErrorEvent(int errorCode, String message)
    {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage()
    {
        return this.message;
    }
}
