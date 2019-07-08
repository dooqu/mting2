package cn.xylink.mting.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.ui.adapter.MainFragmentAdapter;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_main_tabar_unread)
    TextView mUnreadTextView;
    @BindView(R.id.tv_main_tabar_readed)
    TextView mReadedTextView;
    @BindView(R.id.tv_main_tabar_love)
    TextView mLoveTextView;
    @BindView(R.id.dl_main)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.vp_main)
    ViewPager mViewPager;
    private TAB_ENUM mCurrentTabIndex = TAB_ENUM.TAB_UNREAD;

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
        TAB_ENUM.TAB_LOVE.setView(mLoveTextView);
        TAB_ENUM.TAB_READED.setView(mReadedTextView);
        TAB_ENUM.TAB_UNREAD.setView(mUnreadTextView);
        mViewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(TAB_ENUM.values().length);
    }

    @Override
    protected void initTitleBar() {

    }

    public enum TAB_ENUM {
        TAB_UNREAD(0, null),
        TAB_READED(1, null),
        TAB_LOVE(2, null);

        TAB_ENUM(int index, TextView view) {
            this.index = index;
        }

        private int index;
        private TextView view;

        public TextView getView() {
            return view;
        }

        public void setView(TextView view) {
            this.view = view;
        }

        public int getIndex() {
            return index;
        }

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
            case R.id.tv_main_tabar_unread:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_UNREAD);
                break;
            case R.id.tv_main_tabar_readed:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_READED);
                break;
            case R.id.tv_main_tabar_love:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_LOVE);
                break;
        }
    }

    private void doAnim(TAB_ENUM currentTab, TAB_ENUM goTab) {
        if (currentTab != goTab) {
            mViewPager.setCurrentItem(goTab.getIndex(),false);
            mCurrentTabIndex = goTab;
            ObjectAnimator ccAnimator = ObjectAnimator.ofInt(currentTab.getView(), "textColor", 0xff333333, 0xff999999);
            ccAnimator.setEvaluator(new ArgbEvaluator());
            ccAnimator.setDuration(180);
            ObjectAnimator gcAnimator = ObjectAnimator.ofInt(goTab.getView(), "textColor", 0xff999999, 0xff333333);
            gcAnimator.setEvaluator(new ArgbEvaluator());
            gcAnimator.setDuration(180);
            ObjectAnimator csAnimator = ObjectAnimator.ofFloat(currentTab.getView(), "textSize", 24f, 15f);
            csAnimator.setDuration(180);
            ObjectAnimator gsAnimator = ObjectAnimator.ofFloat(goTab.getView(), "textSize", 15f, 24f);
            gsAnimator.setDuration(180);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ccAnimator, gcAnimator, csAnimator, gsAnimator);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.start();
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
