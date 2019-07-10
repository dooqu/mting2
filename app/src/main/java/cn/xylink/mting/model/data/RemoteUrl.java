package cn.xylink.mting.model.data;

public class RemoteUrl {
    private static final String URL_BASE = "http://service.xylink.net";//外网2019-4-9


    //获取短信验证码
    public static String getCodeUrl() {
        return URL_BASE + "/api/sms/common/v2/code/get";
    }
    //注册
    public static String registerUrl(){
        return URL_BASE + "/api/user/common/v2/register";
    }
    //登录
    public static String loginUrl(){
        return URL_BASE + "/api/user/common/v2/login";
    }
    //验证
    public static String checkCodeUrl(){
        return URL_BASE + "/api/sms/common/v2/code/check";
    }
    //验证token有效接口
    public static String checkTokenUrl() {
        return URL_BASE + "/api/user/common/v2/token/check";
    }
    //待读
    public static String getUnreadUrl(){
        return URL_BASE + "/api/sct/v2/article/unread/list";
    }
    //已读
    public static String getReadedUrl(){
        return URL_BASE + "/api/sct/v2/article/existread/list";
    }
    //收藏
    public static String getStoreUrl(){
        return URL_BASE + "/api/sct/v2/article/store/list";
    }

}
