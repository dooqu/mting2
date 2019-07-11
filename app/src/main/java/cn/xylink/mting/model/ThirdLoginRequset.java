package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class ThirdLoginRequset extends BaseRequest {

    private String access_token;
    private String openid;
    private String platform;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "ThridLoginRequset{" +
                "access_token='" + access_token + '\'' +
                ", openid='" + openid + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
