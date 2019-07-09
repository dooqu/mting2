package cn.xylink.mting.base;

public class BaseResponse<T> {
    public int code;
    public String message;
    public T data;
}
