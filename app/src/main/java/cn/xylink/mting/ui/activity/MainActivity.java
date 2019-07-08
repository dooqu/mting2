package cn.xylink.mting.ui.activity;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @BindView(R.id.dl_main)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.vp_main)
    ViewPager mViewPage;
    private int mCurrentTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void preView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setFocusableInTouchMode(false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }

    public enum TAB_ENUM {
        TAB_UNREAD,
        TAB_READED,
        TAB_LOVE,
    }


    @OnClick({R.id.iv_main_title_my, R.id.iv_main_title_search, R.id.iv_main_title_add
            , R.id.tv_main_tabar_readed, R.id.tv_main_tabar_unread, R.id.tv_main_tabar_love})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_title_my:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_main_title_search:
                break;
            case R.id.iv_main_title_add:
                break;
            case R.id.tv_main_tabar_readed:
                mCurrentTabIndex = TAB_ENUM.TAB_UNREAD.ordinal();
                break;
            case R.id.tv_main_tabar_unread:
                mCurrentTabIndex = TAB_ENUM.TAB_READED.ordinal();
                break;
            case R.id.tv_main_tabar_love:
                mCurrentTabIndex = TAB_ENUM.TAB_LOVE.ordinal();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }


}
