package cn.xylink.mting.speech;

public interface TTSAudioLoader {
    public static interface LoadResult
    {
        void invoke(int errorCode, String message, String audioUrl);
    }

    void textToSpeech(String text, Speechor.SpeechorSpeed speechorSpeed, LoadResult result);

    void cancelAll();
}
