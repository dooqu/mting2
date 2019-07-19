package cn.xylink.mting.event;

/*
 *通知主页播放
 *
 * -----------------------------------------------------------------
 * 2019/7/19 15:47 : Create notifyMainPlayEvent.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class NotifyMainPlayEvent {
    private String id;

    public NotifyMainPlayEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
