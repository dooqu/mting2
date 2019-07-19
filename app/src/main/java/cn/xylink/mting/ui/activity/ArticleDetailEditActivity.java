package cn.xylink.mting.ui.activity;

import android.graphics.Color;
import android.widget.TextView;

import butterknife.BindView;
import cn.xylink.mting.R;

public class ArticleDetailEditActivity extends BasePresenterActivity {

    @BindView(R.id.tv_include_title)
    TextView tvInclude;
    @BindView(R.id.tv_right)
    TextView tvRight;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail_edit);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {
        tvInclude.setText("编辑文章");
        tvRight.setText("保存");
        tvRight.setTextColor(Color.parseColor("#488def"));
    }
}
