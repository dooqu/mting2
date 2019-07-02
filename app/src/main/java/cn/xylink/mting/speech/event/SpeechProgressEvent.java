package cn.xylink.mting.speech.event;

import java.util.List;

public class SpeechProgressEvent
{
    private int frameIndex;
    private List<String> textFragments;

    public SpeechProgressEvent(int frameIndex, List<String> textFragments)
    {
        this.frameIndex = frameIndex;
        this.textFragments = textFragments;
    }

    public int getFrameIndex()
    {
        return this.frameIndex;
    }

    public List<String> getTextFragments()
    {
        return this.textFragments;
    }
}
