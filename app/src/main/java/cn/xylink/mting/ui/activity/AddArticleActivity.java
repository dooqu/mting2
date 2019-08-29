package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.event.AddArticleHomeEvent;
import cn.xylink.mting.event.OneArticleEvent;
import cn.xylink.mting.event.TwoArticleEvent;
import cn.xylink.mting.ui.adapter.FragmentAdapter;
import cn.xylink.mting.ui.fragment.AddOneNoteFragment;
import cn.xylink.mting.ui.fragment.AddTwoNoteFragment;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.widget.CustomViewPager;

public class AddArticleActivity extends BasePresenterActivity {

    @BindView(R.id.vp_content)
    CustomViewPager vpContent;
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
                vpContent.setCurrentItem(pageIndex);
                hideSoftInput();
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
//        tabLayout.setTabsFromPagerAdapter(adapter);
        vpContent.setAdapter(adapter);

//        vpContent.setCanScroll(true);
    }



    @Override
    protected void initData() {
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddArticleHomeEvent event) {
        L.v("event 1");
        switch (event.type)
        {
            case 1:  // 变色
                tvRight.setTextColor(getResources().getColorStateList(R.color.color_blue));
                tvRight.setEnabled(true);
                break;
            case 0: //不变色
                tvRight.setTextColor(getResources().getColorStateList(R.color.color_login_text_gray));
                tvRight.setEnabled(false);
                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        L.v(requestCode,resultCode);
//        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        L.v("onBackPressed");
        EventBus.getDefault().post(new OneArticleEvent(OneArticleEvent.TYPE_BACK));
        super.onBackPressed();
    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick({R.id.tv_right,R.id.btn_left})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_left:
                EventBus.getDefault().post(new OneArticleEvent(OneArticleEvent.TYPE_BACK));
                finish();
                break;
            case R.id.tv_right:
                if(pageIndex == 0){
                    EventBus.getDefault().post(new OneArticleEvent(OneArticleEvent.TYPE_SAVE));
                }else {
                    EventBus.getDefault().post(new TwoArticleEvent());
                }
            break;
        }
    }

}
