package cn.xylink.mting.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.utils.ImageUtils;

public class BindingPhoneQQWxActivity extends BasePresenterActivity {

    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.iv_qq_wx)
    ImageView ivQQWx;
    @BindView(R.id.iv_xyl_icon)
    ImageView ivXyl;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_binding_phone_wx_qq);
    }

    @Override
    protected void initView() {
        tvTitle.setText("账号绑定");
        ImageUtils.get().load(ivQQWx,30,"");
        ImageUtils.get().load(ivXyl,30,"");

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }
}
