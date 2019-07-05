package cn.xylink.mting.bean;

public class BaseResponse<T> {
    public int code;
    public String message;
    public T data;
}
