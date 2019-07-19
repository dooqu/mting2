package cn.xylink.mting.ui.activity;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;

public class FeedBackActivity extends BasePresenterActivity {

    @BindView(R.id.tv_include_title)
    TextView tvTitle;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_fadeback);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {
        tvTitle.setText("建议与反馈");

    }

    @OnClick(R.id.btn_left)
    void onBack(View v) {
        finish();
    }

    @OnClick(R.id.bt_submit)
    void onSubmit(View v) {
        finish();
    }
}
