package cn.xylink.multi_image_selector;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.multi_image_selector.adapter.ImagePageAdapter;
import cn.xylink.multi_image_selector.adapter.SectionsPagerAdapter;
import cn.xylink.multi_image_selector.bean.Image;
import cn.xylink.multi_image_selector.event.EventConstant;
import cn.xylink.multi_image_selector.event.EventImageMsg;
import cn.xylink.multi_image_selector.event.EventMsg;
import cn.xylink.multi_image_selector.view.CustomViewPager;

public class ViewPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ViewPagerActivity.class.getName();
    private ImageView btn_left;
    private TextView tv_title;
    private TextView iv_select;
    private RecyclerView rv_bottom;

    private Button mSubmitButton;

    private List<Image> mSelectedImages = new ArrayList<>();
    private List<Image> listData = new ArrayList<>();

    private Image curItem;

    protected volatile int currentIndex;
    public static final String SELECT_INDEX = "select_index";
    public static final String IMAGES_DATA = "images_data";
    public static final String SELECTED_IMAGES = "selected_images";
    private ImagePageAdapter pageAdapter;

    private CustomViewPager mViewPager;
    private int imageSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_view_pager);

        initView();
        initData();
        initListener();
    }

    private void initView() {

        btn_left = findViewById(R.id.btn_left);
        tv_title = findViewById(R.id.tv_title);
        iv_select = findViewById(R.id.iv_select);
        mViewPager = findViewById(R.id.container);
        rv_bottom = findViewById(R.id.rv_bottom);

        mSubmitButton = findViewById(R.id.commit);

        LinearLayoutManager lm2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_bottom.setLayoutManager(lm2);
    }

    private void initData() {
        curItem = (Image) getIntent().getSerializableExtra(SELECT_INDEX);
        mSelectedImages = (List<Image>) getIntent().getSerializableExtra(SELECTED_IMAGES);
        if(mSelectedImages.contains(curItem))
        {
            curItem.isCheck = true;
        }
        if (curItem.isCheck) {
            iv_select.setText("删除");
        }else
        {
            iv_select.setText("选择");
        }
        listData.add(curItem);
        pageAdapter = new ImagePageAdapter(this, listData);
        rv_bottom.setAdapter(pageAdapter);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), listData,mViewPager);
        // Set up the ViewPager with the sections adapter.
//        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCanScroll(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ArrayList<String> data = new ArrayList<>();
        for (Image image : mSelectedImages)
            data.add(image.path);
        EventBus.getDefault().post(new EventImageMsg(new Object[]{EventConstant.RESUME, data}));
        mSelectedImages.clear();
        listData.clear();
    }

    public void initListener() {
        btn_left.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);

        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv_select.getText().equals("删除")){
                    mSelectedImages.remove(curItem);
                    ArrayList<String> resultList = new ArrayList<>();
                    for (Image image : mSelectedImages)
                        resultList.add(image.path);
                    EventBus.getDefault().post(new EventMsg(new Object[]{EventConstant.ACTIVITY_FINISH, resultList}));
                }else {
                    //預覽位置索引
                    mSelectedImages.add(curItem);
                    ArrayList<String> resultList = new ArrayList<>();
                    for (Image image : mSelectedImages)
                        resultList.add(image.path);
                    EventBus.getDefault().post(new EventMsg(new Object[]{EventConstant.ACTIVITY_FINISH, resultList}));

                }
                finish();
            }
        });

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAfterTransition();

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_left) {
            finishAfterTransition();
            return;
        } else if (id == R.id.commit) {
            ArrayList<String> resultList = new ArrayList<>();
            for (Image image : mSelectedImages)
                resultList.add(image.path);
            EventBus.getDefault().post(new EventMsg(new Object[]{EventConstant.ACTIVITY_FINISH, resultList}));
            finish();
        }
    }
}
