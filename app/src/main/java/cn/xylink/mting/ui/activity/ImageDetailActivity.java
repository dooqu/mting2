package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.utils.ImageUtils;

public class ImageDetailActivity extends BaseActivity {

    @BindView(R.id.iv_source)
    ImageView ivSource;
    private int position;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_image_detail);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String path = extras.getString("path");
        position = extras.getInt("position", -1);
        ImageUtils.get().load(ivSource, path);
    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick(R.id.iv_back)
    void idaClose(View v) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        setResult(0, intent);
        finish();
    }


    @OnClick(R.id.tv_del)
    void idaDel(View v) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        setResult(100, intent);
        finish();
    }
}
