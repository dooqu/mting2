package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;

public class PhoneLoginActivity extends BaseActivity {

    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";
    @BindView(R.id.tv_phone_86)
    TextView tvPhone86;
    @BindView(R.id.et_phone)
    EditText etPhone;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_phone_login);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick(R.id.btn_next)
    public void onClick(View v){

        switch (v.getId())
        {
            case R.id.btn_next:
                String phone = etPhone.getText().toString();
                Intent mIntent = new Intent(PhoneLoginActivity.this,GetCodeActivity.class);
                mIntent.putExtra(EXTRA_PHONE,phone);
                mIntent.putExtra(EXTRA_SOURCE,"login");
                startActivity(mIntent);
                break;
        }
    }
}
