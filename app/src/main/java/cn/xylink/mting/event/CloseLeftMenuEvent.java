package cn.xylink.mting.event;

/*
 *关闭左侧菜单通知
 *
 * -----------------------------------------------------------------
 * 2019/7/23 16:44 : Create CloseLeftMenuEvent.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class CloseLeftMenuEvent {
    private boolean isShare =false;

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }
}
