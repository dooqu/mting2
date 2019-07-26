package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class LinkCreateRequest extends BaseRequest {

    private int inType;
    private String url;
    private String content;
    private String type;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
