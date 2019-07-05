package cn.xylink.mting.bean;

import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.SignKit;

public class BaseRequest {
    public static final String desKey = "xylink&20180427&inbeijing";

    public String token = "";
    public long timestamp;
    public String sign;

    public BaseRequest() {
        token = ContentManager.getInstance().getLoginToken();
        timestamp = System.currentTimeMillis();
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
}
