package cn.xylink.mting.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
import cn.xylink.mting.utils.TingUtils;
import cn.xylink.mting.widget.CustomViewPager;
import cn.xylink.multi_image_selector.MultiImageSelector;
import io.reactivex.annotations.NonNull;

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
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

    public void checkPermission() {
        List<String> mPermissionList = new ArrayList<>();
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(str);
            }
        }
        String[] permis = new String[mPermissionList.size()];
        L.v(permis.toString());
        permissions = mPermissionList.toArray(permis);
        //权限发生了改变 true  //  false
        if (permissions.length > 0) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            callGallery();
        }
    }

    /**
     * @param requestCode
     * @param permissions  请求的权限
     * @param grantResults 请求权限返回的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // camear 权限回调
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 表示用户授权
                callGallery();
            } else {
                //用户拒绝权限
            }
        }
    }

    /**
     * 调用图库选择
     */
    private void callGallery() {

        MultiImageSelector.create()
                .showCamera(true) // 是否显示相机. 默认为显示
                .count(9) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                // 单选模式
                .multi() // 多选模式, 默认模式;
                .count(9)
                .setVideo(true)
                .selectCount(9)
                .start(this, 11);
    }


    @OnClick({R.id.tv_right,R.id.btn_left})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_left:
//                checkPermission();
                TingUtils.goToMarket(this);
//                EventBus.getDefault().post(new OneArticleEvent(OneArticleEvent.TYPE_BACK));
//                finish();
                break;
            case R.id.tv_right:
                TingUtils.isMarketInstalled(this);
                if(pageIndex == 0){
                    EventBus.getDefault().post(new OneArticleEvent(OneArticleEvent.TYPE_SAVE));
                }else {
                    EventBus.getDefault().post(new TwoArticleEvent());
                }
            break;
        }
    }

}
