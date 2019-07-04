package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.ui.fragment.GuideFragment;

public class GuideActivity extends BaseActivity {
    private final static int[] TITLE = {R.string.guide_one_title, R.string.guide_two_title, R.string.guide_three_title};
    private final static int[] MSG = {R.string.guide_one_msg, R.string.guide_two_msg, R.string.guide_three_msg};
    private final static int[] IMAGE = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    @BindView(R.id.viewpager_guide)
    ViewPager mViewPager;
    @BindView(R.id.iv_guide_indicator)
    ImageView mIndicatorView;
    @BindView(R.id.tv_guide)
    TextView mTvGuide;


    @Override
    protected void initView() {
        mViewPager.setAdapter(new GuidePagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(OnPageChangeListener);
        mViewPager.setCurrentItem(0);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(OnPageChangeListener);
    }

    @Override
    protected void preView() {
        setContentView(R.layout.guide_activity);
    }

    ViewPager.OnPageChangeListener OnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                mIndicatorView.setImageResource(R.drawable.icon_one_indicator);
            } else if (position == 1) {
                mIndicatorView.setImageResource(R.drawable.icon_two_indicator);
            }
            switch (position) {
                case 0:
                case 1:
                    mTvGuide.setText("跳过");
                    break;
                case 2:
                    mTvGuide.setText("开始使用");
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    class GuidePagerAdapter extends FragmentPagerAdapter {

        public GuidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GuideFragment.newInstance(TITLE[position], MSG[position], IMAGE[position]);
        }

        @Override
        public int getCount() {
            return IMAGE.length;
        }
    }

    @OnClick(R.id.tv_guide)
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.tv_guide:
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
