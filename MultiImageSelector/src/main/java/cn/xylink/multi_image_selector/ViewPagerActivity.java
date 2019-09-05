package cn.xylink.multi_image_selector;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.multi_image_selector.adapter.ImagePageAdapter;
import cn.xylink.multi_image_selector.adapter.RecyclerAdapter;
import cn.xylink.multi_image_selector.adapter.SectionsPagerAdapter;
import cn.xylink.multi_image_selector.bean.Image;
import cn.xylink.multi_image_selector.event.EventConstant;
import cn.xylink.multi_image_selector.event.EventImageMsg;
import cn.xylink.multi_image_selector.event.EventMsg;
import cn.xylink.multi_image_selector.utils.SharedPreHelper;
import cn.xylink.multi_image_selector.view.CustomViewPager;

public class ViewPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ViewPagerActivity.class.getName();
    private ImageView btn_left;
    private TextView tv_title;
    private Button iv_select;
    private RecyclerView rv_bottom;

    private Button mSubmitButton;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

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
        mImages = (List<Image>) getIntent().getSerializableExtra(IMAGES_DATA);
        mSelectedImages = (List<Image>) getIntent().getSerializableExtra(SELECTED_IMAGES);
        imageSize = mImages.size();
        currentIndex = mImages.indexOf(curItem);
        String title = String.format("%s/%s", currentIndex + 1, imageSize);
        tv_title.setText(title);
        if (mSelectedImages.contains(mImages.get(currentIndex))) {
            iv_select.setText("删除");
        }else
        {
            iv_select.setText("选择");
        }
        pageAdapter = new ImagePageAdapter(this, mSelectedImages);
        rv_bottom.setAdapter(pageAdapter);


        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mImages,mViewPager);
        // Set up the ViewPager with the sections adapter.
//        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(currentIndex);

        int scrollPosition = mSelectedImages.indexOf(curItem);
        rv_bottom.scrollToPosition(scrollPosition);
        pageAdapter.setCurentIndex(scrollPosition);
        updateDoneText(mSelectedImages.size());

    }


    @Override
    protected void onStop() {
        super.onStop();
        ArrayList<String> data = new ArrayList<>();
        for (Image image : mSelectedImages)
            data.add(image.path);
        EventBus.getDefault().post(new EventImageMsg(new Object[]{EventConstant.RESUME, data}));
    }

    public void initListener() {
        btn_left.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                curItem = mImages.get(i);
                if (mSelectedImages.contains(mImages.get(i))) {
                    int imagePos = mSelectedImages.indexOf(curItem);
                    rv_bottom.scrollToPosition(imagePos);
                    pageAdapter.setCurentIndex(imagePos);
                } else {
                }
                String title = String.format("%s/%s", currentIndex + 1, imageSize);
                tv_title.setText(title);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //預覽位置索引
                currentIndex = mSelectedImages.indexOf(curItem);

                    mSelectedImages.add(curItem);
                    pageAdapter.notifyDataSetChanged();
                    int index = mSelectedImages.indexOf(curItem);
                    rv_bottom.scrollToPosition(index);
                    pageAdapter.setCurentIndex(index);
                updateDoneText(mSelectedImages.size());
            }
        });

        pageAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, Object item, int pos) {
                curItem = (Image) item;
                ArrayList<String> resultList = new ArrayList<>();
                    resultList.add(curItem.path);
                EventBus.getDefault().post(new EventMsg(new Object[]{EventConstant.ACTIVITY_FINISH, resultList}));
                finish();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void updateDoneText(int selectCount) {
        if (selectCount <= 0) {
            mSubmitButton.setText(R.string.mis_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setEnabled(true);
        }
        if (selectCount > 0)
            mSubmitButton.setBackgroundColor(getColor(R.color.mis_button_selected_bg));
        else
            mSubmitButton.setBackgroundColor(getColor(R.color.mis_button_default_bg));

        int COUNT = (int) SharedPreHelper.getInstance(ViewPagerActivity.this).getSharedPreference(SharedPreHelper.SharedAttribute.SELECT_COUNT,0);

        mSubmitButton.setText(getString(R.string.mis_action_button_string,
                getString(R.string.mis_action_done), selectCount, COUNT));
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
