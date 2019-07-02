package cn.xylink.mting.speech.event;

import cn.xylink.mting.speech.Speechor;

public class SpeechStateChangedEvent {
    Speechor.SpeechorState state;
    public SpeechStateChangedEvent(Speechor.SpeechorState speechorState)
    {
        this.state = speechorState;
    }

    public Speechor.SpeechorState getState()
    {
        return this.state;
    }
}
