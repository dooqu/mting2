package cn.xylink.mting.model;

/**
 * Created by wjn on 2018/10/31.
 */

public class WXQQDataBean {
    private String access_token;
    private String openid;
    private String type;

    public WXQQDataBean(String access_token, String openid, String type) {
        this.access_token = access_token;
        this.openid = openid;
        this.type = type;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}