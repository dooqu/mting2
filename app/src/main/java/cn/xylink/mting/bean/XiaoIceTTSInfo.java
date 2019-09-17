package cn.xylink.mting.bean;

public class XiaoIceTTSInfo {

    private String audioUrl;
    private String text;
    private String msgId;

    private XiaoIceTTSInfo content;


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public String getAudioUrl() {
        return audioUrl;
    }


    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public XiaoIceTTSInfo getContent() {
        return content;
    }


    public void setContent(XiaoIceTTSInfo content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
