package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class SmsLoginRequset extends BaseRequest {

    public String phone;
    public String code;
    public String codeId;
    @Override
    public String toString() {
        return "SmsLogin{" +
                "phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                ", codeId='" + codeId + '\'' +
                '}';
    }


}
