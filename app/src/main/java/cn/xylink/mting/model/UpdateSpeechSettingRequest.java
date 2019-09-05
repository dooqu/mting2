package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class UpdateSpeechSettingRequest extends BaseRequest {
    Integer sound;
    Float speed;
    Integer font;

    public UpdateSpeechSettingRequest() {
        sound = null;
        speed = null;
        font = null;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public Integer getFont() {
        return font;
    }

    public void setFont(Integer font) {
        this.font = font;
    }
}
