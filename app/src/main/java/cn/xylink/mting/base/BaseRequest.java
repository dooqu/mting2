package cn.xylink.mting.base;

import cn.xylink.mting.MTing;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.SignKit;
import cn.xylink.mting.utils.TingUtils;

public class BaseRequest {
    public static final String desKey = "xylink&20180427&inbeijing";

    public String token = "";
    public long timestamp;
    public String sign;
    public String deviceId;

    public BaseRequest() {
        token = ContentManager.getInstance().getLoginToken();
        timestamp = System.currentTimeMillis();
        deviceId = TingUtils.getDeviceId(MTing.getInstance());
    }

    public void doSign() {
        this.sign = SignKit.sign(this);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getDeviceId() {
        return TingUtils.getDeviceId(MTing.getInstance());
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
