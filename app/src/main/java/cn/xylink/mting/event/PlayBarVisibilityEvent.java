package cn.xylink.mting.event;

/*
 *
 *
 * -----------------------------------------------------------------
 * 2019/8/8 16:49 : Create PlayBarVisibilityEvent.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class PlayBarVisibilityEvent {
    private int visibility;

    public PlayBarVisibilityEvent(int visibility) {
        this.visibility = visibility;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
