package cn.xylink.mting.model.data;

public class RemoteUrl {
    private static final String URL_BASE = "http://service.xylink.net";//外网2019-4-9


    //获取短信验证码
    public static String getCodeUrl() {
        return URL_BASE + "/api/common/v1/sms/get";
    }

    //登录
    public static String onLogin() {
        return URL_BASE + "/api/sms/common/v2/code/get";
    }

}
