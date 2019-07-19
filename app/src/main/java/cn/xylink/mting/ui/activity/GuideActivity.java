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
    private final static int[] IMAGE = {R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3};

    @BindView(R.id.viewpager_guide)
    ViewPager mViewPager;
    @BindView(R.id.iv_guide_indicator)
    ImageView mIndicatorView;
    @BindView(R.id.btn_guide)
    Button mBtnGuide;
    @BindView(R.id.tv_break)
    TextView tvBreak;


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
                mIndicatorView.setImageResource(R.mipmap.icon_one_indicator);
            } else if (position == 1) {
                mIndicatorView.setImageResource(R.mipmap.icon_two_indicator);
            }
            switch (position) {
                case 0:
                case 1:
                    tvBreak.setVisibility(View.VISIBLE);
                    mBtnGuide.setVisibility(View.GONE);
                    mIndicatorView.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tvBreak.setVisibility(View.GONE);
                    mBtnGuide.setVisibility(View.VISIBLE);
                    mIndicatorView.setVisibility(View.GONE);
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

    @OnClick({R.id.btn_guide,R.id.tv_break})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_guide:
            case R.id.tv_break:
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
