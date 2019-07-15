package cn.xylink.mting.ui.activity;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;

public class ArrangeActivity extends BasePresenterActivity {

    @BindView(R.id.tv_arrange_title)
    TextView mTitleView;
    @BindView(R.id.tv_arrange_add_unread)
    TextView mAddUnreadView;
    @BindView(R.id.rv_arrange)
    RecyclerView mRecyclerView;
    @BindView(R.id.cb_arrange_all_check)
    CheckBox mAllCheckBox;
    @BindView(R.id.cb_arrange_unread_check)
    CheckBox mUnreadCheckBox;
    @BindView(R.id.tv_arrange_unread)
    TextView mUnreadTextView;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_arrange);
    }

    @Override
    protected void initView() {
        int type = getIntent().getIntExtra("", 0);
        switch (type) {
            case 0:
                mAddUnreadView.setVisibility(View.GONE);
                mUnreadCheckBox.setVisibility(View.GONE);
                mUnreadTextView.setVisibility(View.GONE);
                mTitleView.setText("待读");
                break;
            case 1:
                mTitleView.setText("已读");
                break;
            case 2:
                mTitleView.setText("收藏");
                break;
        }
    }

    @OnClick({R.id.iv_arrange_back, R.id.tv_arrange_del, R.id.tv_arrange_add_unread, R.id.cb_arrange_all_check, R.id.cb_arrange_unread_check})
    void onClick(View view){
        switch (view.getId()){
            case R.id.iv_arrange_back:
                break;
            case R.id.tv_arrange_del:
                break;
            case R.id.tv_arrange_add_unread:
                break;
            case R.id.cb_arrange_all_check:
                break;
            case R.id.cb_arrange_unread_check:
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }
}
