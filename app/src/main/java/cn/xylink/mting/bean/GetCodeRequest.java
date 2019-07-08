package cn.xylink.mting.bean;


public class GetCodeRequest extends BaseRequest {

    public String phone;
    public String source;
    public String deviceId;

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
