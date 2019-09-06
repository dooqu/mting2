package cn.xylink.multi_image_selector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import cn.xylink.multi_image_selector.event.EventConstant;
import cn.xylink.multi_image_selector.event.EventMsg;
import cn.xylink.multi_image_selector.utils.SharedPreHelper;

/**
 * Multi image selector
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 * Updated by nereo on 2016/5/18.
 */
public class MultiImageSelectorActivity extends AppCompatActivity
        implements MultiImageSelectorFragment.Callback {

    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;

    public static final int REQUSET_DETAIL = 101;
    public static final int REQUSET_CROP = 102;

    /**
     * Max image size，int，{@link #DEFAULT_IMAGE_SIZE} by default
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * Select mode，{@link #MODE_MULTI} by default
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * Whether show camera，true by default
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * Result data set，ArrayList&lt;String&gt;
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * Original data set
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    // Default image size
    private int DEFAULT_IMAGE_SIZE = 9;
    public static final String EXTRA_SELECT_COUNT_IMG = "select_count";

    public static final String EXTRA_SHOW_VIDEO = "show_video";

    private ArrayList<String> resultList = new ArrayList<>();
    private TextView mSubmitButton;
    private ImageView mBtnBack;
    private TextView mTvTitle;
    public int mDefaultCount = 0;
    private int selectCount;

    /**
     *  video Control Options
     */
    public static boolean isVideo = false;

    private MultiImageSelectorFragment multiImageSelectorFragment;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.mis_activity_default);

        EventBus.getDefault().register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        final Intent intent = getIntent();
        isVideo = intent.getBooleanExtra(EXTRA_SHOW_VIDEO,false);
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
        SharedPreHelper.getInstance(this).put(SharedPreHelper.SharedAttribute.SHARED_VIDEO,isVideo);
        SharedPreHelper.getInstance(this).put(SharedPreHelper.SharedAttribute.SELECT_COUNT,mDefaultCount);
        final int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        final boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        selectCount = intent.getIntExtra(EXTRA_SELECT_COUNT_IMG, 0);

        mSubmitButton = findViewById(R.id.commit);
        mTvTitle = findViewById(R.id.tv_title);

        mSubmitButton.setText("确定");

        mBtnBack = findViewById(R.id.btn_left);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        if (mode == MODE_MULTI) {
            updateDoneText(resultList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (resultList != null && resultList.size() > 0) {
                        // Notify success
                        Intent data = new Intent();
                        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                        setResult(RESULT_OK, data);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_SELECT_COUNT_IMG, selectCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
//            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_VIDEO,isVideo);
            bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

            multiImageSelectorFragment = (MultiImageSelectorFragment) Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, multiImageSelectorFragment)
                    .commit();
            multiImageSelectorFragment.setTitle(mTvTitle);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUSET_DETAIL)
        {
            if (resultList != null && resultList.size() > 0) {
                // Notify success
                Intent mIntent = new Intent();
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, mIntent);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }else if (requestCode == REQUSET_CROP)
        {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update done button by select image data
     *
     * @param resultList selected image data
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void updateDoneText(ArrayList<String> resultList) {
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(R.string.mis_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setEnabled(true);
        }
        mTvTitle.setText(getString(R.string.mis_action_button_string,
                "请选择图片", resultList.size(), mDefaultCount));

    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            // notify system the image has change
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onImageListSelected(ArrayList<String> images) {
        resultList = images;
        updateDoneText(images);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        resultList.clear();

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getEventBus(EventMsg msg) {
        final int type = (int) msg.getObj()[0];
        Log.e(MultiImageSelectorActivity.class.getName(),"activity getEventBus TYPE:" + type);
        switch (type) {
            case EventConstant.ACTIVITY_FINISH:
                resultList = (ArrayList<String>) msg.getObj()[1];
//                if (resultList != null && resultList.size() > 0) {
                    // Notify success
                    Intent mIntent = new Intent();
                    mIntent.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, mIntent);
//                } else {
//                    setResult(RESULT_CANCELED);
//                }
                finish();
                break;
        }
    }
}
