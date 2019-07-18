package cn.xylink.mting.speech;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import cn.xylink.mting.speech.data.XiaoIceTTSAudioLoader;

public class XiaoIceSpeechor implements Speechor {

    enum SpeechTextFragmentState {
        TextReady,
        AudioLoadding,
        AudioReady,
        Error
    }

    public class SpeechTextFragment {
        String fragmentText;
        String audioUrl;
        SpeechTextFragmentState fragmentState;

        public SpeechTextFragment() {
            this.fragmentState = SpeechTextFragmentState.TextReady;
        }


        public String getFragmentText() {
            return fragmentText;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public SpeechTextFragmentState getFragmentState() {
            return fragmentState;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public void setFragmentState(SpeechTextFragmentState fragmentState) {
            this.fragmentState = fragmentState;
        }

        public void setFragmentText(String fragmentText) {
            this.fragmentText = fragmentText;
        }
    }


    int fragmentIndex;
    int fragmentIndexNext;
    boolean isReleased;
    List<String> textFragments;
    List<SpeechTextFragment> speechTextFragments;
    SpeechorState state;
    SpeechorSpeed speed;
    boolean isSimulatedPaused = false;
    int loaddingIndex;
    SpeechHelper speechHelper = new SpeechHelper();
    static int LOADER_QUEUE_SIZE = 5;

    @Override
    public void prepare(String text) {
        synchronized (this) {
            if (isReleased)
                return;

            if (this.state != SpeechorState.SpeechorStateReady) {
                this.reset();
            }
            List<String> textFragmentsNew = speechHelper.prepare(text);

            for (int i = 0, size = textFragmentsNew.size(); i < size; i++) {
                SpeechTextFragment fragment = new SpeechTextFragment();
                fragment.setFragmentText(textFragmentsNew.get(i));
                fragment.setFragmentState(SpeechTextFragmentState.TextReady);
                speechTextFragments.add(fragment);
            }
            this.textFragments.addAll(textFragmentsNew);
        }
    }

    @Override
    public int seek(int index) {
        synchronized (this) {
            if (isReleased)
                return -3;

            if (fragmentIndex < 0
                    || fragmentIndex >= this.textFragments.size()
                    || textFragments.size() == 0)
                return -2;

            if (this.state == SpeechorState.SpeechorStateLoadding && this.fragmentIndex == index) {
                return -6;
            }

            this.fragmentIndex = index;
            this.fragmentIndexNext = fragmentIndex;

            return 0;
            //return seekAndPlay(fragmentIndex);
        }
    }


    private void seekAndPlay(int frameIndex) {
        int segmentSize = this.textFragments.size();
        for(int startIndex = frameIndex, endIndex = Math.min(startIndex + LOADER_QUEUE_SIZE, segmentSize); startIndex < endIndex; ++startIndex) {

            boolean isSegumentWillPlaying = startIndex == frameIndex;
            SpeechTextFragment fragment = this.speechTextFragments.get(startIndex);
            switch (fragment.getFragmentState()) {
                case AudioLoadding:
                    if(isSegumentWillPlaying == true) {
                        this.state = SpeechorState.SpeechorStateLoadding;
                        return;
                    }
                    else {
                        continue;
                    }
                case Error:
                    if(isSegumentWillPlaying == true) {
                        this.state = SpeechorState.SpeechorStateReady;
                        this.onError(-100, "加载错误");
                        return;
                    }
                    else {
                        break;
                    }

                case TextReady:
                    state = SpeechorState.SpeechorStateLoadding;
                    break;

                case AudioReady:
                    state = SpeechorState.SpeechorStatePlaying;
                    break;
            }
        }
    }


    private void playSegment(SpeechTextFragment fragment) {

    }


    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean resume() {
        return false;
    }

    @Override
    public synchronized void stop() {

        this.reset();
    }

    @Override
    public void reset() {
        synchronized (this) {

            SpeechorState currState = this.state;
            this.state = SpeechorState.SpeechorStateReady;
            this.fragmentIndex = 0;
            this.fragmentIndexNext = 0;

            if (this.textFragments != null) {
                this.textFragments.clear();
            }
            this.textFragments = new ArrayList<>();

            if (this.speechTextFragments != null) {
                this.speechTextFragments.clear();
            }
            this.textFragments = new ArrayList<>();

            if (currState != SpeechorState.SpeechorStateReady) {
                // this.speechSynthesizer.stop();
                this.onStateChanged(SpeechorState.SpeechorStateReady);
            }
        }
    }

    @Override
    public void release() {

    }

    @Override
    public void setRole(SpeechorRole role) {

    }

    @Override
    public void setSpeed(SpeechorSpeed speed) {

    }

    @Override
    public SpeechorRole getRole() {
        return null;
    }

    @Override
    public SpeechorSpeed getSpeed() {
        return null;
    }

    @Override
    public SpeechorState getState() {
        return null;
    }

    @Override
    public int getFragmentIndex() {
        return fragmentIndex;
    }

    @Override
    public List<String> getTextFragments() {
        return textFragments;
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public void onStateChanged(SpeechorState speakerState) {

    }

    @Override
    public void onProgress(List<String> textFragments, int index) {

    }

    @Override
    public void onError(int errorCode, String message) {

    }
}
