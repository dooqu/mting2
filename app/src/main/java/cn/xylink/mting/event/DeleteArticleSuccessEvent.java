package cn.xylink.mting.event;

import cn.xylink.mting.ui.fragment.BaseMainTabFragment;

/*
 *
 *删除成功通知
 * -----------------------------------------------------------------
 * 2019/7/12 13:59 : Create DeleteArticleSuccessEvent.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class DeleteArticleSuccessEvent {
    private BaseMainTabFragment.TAB_TYPE tab_type;
    private String ids;

    public DeleteArticleSuccessEvent(BaseMainTabFragment.TAB_TYPE tab_type) {
        this.tab_type = tab_type;
    }

    public BaseMainTabFragment.TAB_TYPE getTab_type() {
        return tab_type;
    }

    public void setTab_type(BaseMainTabFragment.TAB_TYPE tab_type) {
        this.tab_type = tab_type;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public DeleteArticleSuccessEvent(BaseMainTabFragment.TAB_TYPE tab_type, String ids) {
        this.tab_type = tab_type;
        this.ids = ids;
    }
}
