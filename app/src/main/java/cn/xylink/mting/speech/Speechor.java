package cn.xylink.mting.speech;

import java.util.List;

public interface Speechor {
    static int ERROR_RETRY_COUNT = 3;
    @FunctionalInterface
    public static interface StateChangedListener
    {
        void onChanged(SpeechorState state);
    }

    @FunctionalInterface
    public static interface SpeakProgressListener
    {
        void onProgress(List<String> fragments, int index);
    }

    @FunctionalInterface
    public static interface ErrorListener
    {
        void onError(int errorCode, String message);
    }


    static enum  SpeechorSpeed
    {
        /*正常语速*/
        SPEECH_SPEED_NORMAL,
        /*1.5倍语速*/
        SPEECH_SPEED_MULTIPLE_1_POINT_5,
        /*2倍语速*/
        SPEECH_SPEED_MULTIPLE_2,
        /*2.5倍语速*/
        SPEECH_SPEED_MULTIPLE_2_POINT_5
    }

    static enum SpeechorState
    {
        /*就绪中*/
        SpeechorStateReady,
        /*播放中*/
        SpeechorStatePlaying,
        /*暂停中*/
        SpeechorStatePaused,

        SpeechorStateLoadding
    }

    static enum SpeechorRole
    {
        XiaoMei,
        XiaoYao,
        YaYa,
        XiaoIce
    }

    //base operation method

    /*
    设定要读取的文本，内部需要按照标点进行拆分
     */
    void prepare(String text);

    /*
    从指定的进度开始读取，percentage为浮点数
     */
    int seek(int fragmentIndex);

    /*

     */
    boolean pause();
    boolean resume();
    void stop();
    void reset();
    void release();


    //set method, and state were set by system;
    void setRole(SpeechorRole role);
    void setSpeed(SpeechorSpeed speed);
    // void setContext(Context context);

    //get method
    SpeechorRole getRole();
    SpeechorSpeed getSpeed();
    SpeechorState getState();
    int getFragmentIndex();
    List<String> getTextFragments();
    float getProgress();

    //event method
    /*
    invoke when speechor's state is changed;
    Playing
    Pauseding
    Loadding
    三种状态切换
     */
    void onStateChanged(SpeechorState speakerState);

    /*
    invoke when speaking progress is changed;
    parameter index is the index of current text fragment ;
     */
    void onProgress(List<String> textFragments, int index);



    void onError(int errorCode, String message);

}
