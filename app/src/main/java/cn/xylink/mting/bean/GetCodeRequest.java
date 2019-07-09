package cn.xylink.mting.bean;


import cn.xylink.mting.base.BaseRequest;

public class GetCodeRequest extends BaseRequest {

    public String phone;
    public String source;

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
