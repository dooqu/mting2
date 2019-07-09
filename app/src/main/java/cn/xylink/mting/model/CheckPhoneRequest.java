package cn.xylink.mting.model;


import cn.xylink.mting.base.BaseRequest;

public class CheckPhoneRequest extends BaseRequest {

    public String phone;
    public String source;
    public String code;
    public String codeId;

    @Override
    public String toString() {
        return "GetCodeRequest{" +
                "phone='" + phone + '\'' +
                ", source='" + source + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", token='" + token + '\'' +
                ", timestamp=" + timestamp +
                ", sign='" + sign + '\'' +
                '}';
    }


}
