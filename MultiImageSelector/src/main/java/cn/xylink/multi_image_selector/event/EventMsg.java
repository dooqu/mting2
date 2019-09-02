package cn.xylink.multi_image_selector.event;

public class EventMsg {

    private Object[] obj;

    public EventMsg(Object[] _obj) {
        this.obj = _obj;
    }

    public Object[] getObj() {
        return obj;
    }

    public void setObj(Object[] obj) {
        this.obj = obj;
    }
}
