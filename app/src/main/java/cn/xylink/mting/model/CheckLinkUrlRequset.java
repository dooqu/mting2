package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class CheckLinkUrlRequset extends BaseRequest {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
