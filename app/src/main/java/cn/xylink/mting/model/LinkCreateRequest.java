package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class LinkCreateRequest extends BaseRequest {

    private int inType;

    public int getInType() {
        return inType;
    }

    public void setInType(int inType) {
        this.inType = inType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

}
