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
    //忘记密码接口
    public static String forgotUrl() {
        return URL_BASE + "/api/user/common/v3/forgot";
    }
    //已读
    public static String getReadedUrl(){
        return URL_BASE + "/api/sct/v2/article/existread/list";
    }
    //删除待读
    public static String getDelUnreadUrl(){
        return URL_BASE + "/api/sct/v2/article/unread/delete";
    }
    //删除已读
    public static String getDelReadedUrl(){
        return URL_BASE + "/api/sct/v2/article/existread/delete";
    }
    //删除收藏
    public static String getDelStoreUrl(){
        return URL_BASE + "/api/sct/v2/article/store/delete";
    }
    //收藏列表
    public static String getStoreUrl(){
        return URL_BASE + "/api/sct/v2/article/store/list";
    }
    //添加收藏
    public static String getAddStoreUrl(){
        return URL_BASE + "/api/sct/v2/article/store";
    }

}
