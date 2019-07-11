package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class BindCheckRequest extends BaseRequest {

    private String phone;

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "BindCheckRequset{" +
                "phone='" + phone + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String platform;


    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

}
