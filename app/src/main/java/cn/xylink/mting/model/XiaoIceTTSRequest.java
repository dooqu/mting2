package cn.xylink.mting.model;

public class XiaoIceTTSRequest  extends cn.xylink.mting.base.BaseRequest {
    private String text;

    private String speed;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
