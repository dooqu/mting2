package cn.xylink.mting.base;

import java.util.List;

public class BaseResponseArray<T> {
    public int code;
    public String message;
    public int used;
    public List<T> data;
    public List<T> list;
}
