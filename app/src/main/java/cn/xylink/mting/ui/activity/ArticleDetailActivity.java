package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.graphics.Color;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class ArticleDetailActivity extends BaseActivity {

    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {



    }

    @Override
    protected void initTitleBar() {
//        titleBar.setBackgroundColor(Color.parseColor("#488def"));
//        titleBar.getLeftImageButton().setImageResource(R.mipmap.back);
//        titleBar.setStatusBarColor(R.color.color_main_bllue);
//        titleBar.getRightTextView().setTextColor(Color.parseColor("#ffffff"));
//        titleBar.getRightTextView().setText("反馈");
    }
}
