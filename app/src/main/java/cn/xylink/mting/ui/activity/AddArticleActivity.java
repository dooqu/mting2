package cn.xylink.mting.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.event.OneArticleEvent;
import cn.xylink.mting.event.TwoArticleEvent;
import cn.xylink.mting.ui.adapter.FragmentAdapter;
import cn.xylink.mting.ui.fragment.AddOneNoteFragment;
import cn.xylink.mting.ui.fragment.AddTwoNoteFragment;

public class AddArticleActivity extends BasePresenterActivity {

    @BindView(R.id.vp_content)
    ViewPager vpContent;
    @BindView(R.id.vp_tab)
    TabLayout tabLayout;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;

    private int pageIndex;

    private FragmentManager fm;
    {
        fm = getSupportFragmentManager();
    }
    @Override
    protected void preView() {
        setContentView(R.layout.activity_add_aricle);
    }

    @Override
    protected void initView() {
        tvTitle.setText("添加文章");
        tvRight.setText("保存");
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pageIndex = tab.getPosition();
                //tab被选的时候回调
                vpContent.setCurrentItem(pageIndex, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //tab未被选择的时候回调
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //tab重新选择的时候回调
            }
        });
        tabLayout.setupWithViewPager(vpContent);

        AddOneNoteFragment oneNoteFragment = AddOneNoteFragment.newInstance(null);
        AddTwoNoteFragment twoNoteFragment = AddTwoNoteFragment.newInstance(null);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(oneNoteFragment);
        fragments.add(twoNoteFragment);
        FragmentAdapter adapter = new FragmentAdapter(fm,fragments,new String[]{"手动输入","从文章链接输入"});
        tabLayout.setTabsFromPagerAdapter(adapter);
        vpContent.setAdapter(adapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick(R.id.tv_right)
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_right:
                if(pageIndex == 0){
                    EventBus.getDefault().post(new OneArticleEvent());
                }else {
                    EventBus.getDefault().post(new TwoArticleEvent());
                }
            break;
        }
    }

}
