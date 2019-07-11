package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/*
 *待读
 *
 * -----------------------------------------------------------------
 * 2019/7/9 19:19 : Create UnreadRequest.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class UnreadRequest extends BaseRequest {
    Long updateAt;
    String event;//刷新(refresh)、更多(more)
    //事件为refresh时：
    //
    //1.updateAt为空，加载最新的数据
    //
    //2.updateAt有值，加载updateAt时间之后的数据
    //
    //事件为more时：
    //
    //updateAt必须有值，加载updateAt时间之前的数据

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public enum ENENT_TYPE{
        refresh,
        more,
    }
}
