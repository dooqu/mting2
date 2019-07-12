package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *删除已读
 *
 * -----------------------------------------------------------------
 * 2019/7/11 20:02 : Create DelReadedRequest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class DelReadedRequest extends BaseRequest {
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
