package cn.xylink.mting.http;

import cn.xylink.mting.model.IModel;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public abstract class JsonCallBack {
    public void onPre() {

    }

    public abstract void onError();

    public abstract void onFinish();

    public abstract void onSuccess(IModel json);
}
