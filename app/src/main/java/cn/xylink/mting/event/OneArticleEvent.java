package cn.xylink.mting.event;

public class OneArticleEvent {

    public static final int TYPE_BACK = 0;
    public static final int TYPE_SAVE = 1;

    public int type;
    public OneArticleEvent(int type){
        this.type = type;
    }
}
