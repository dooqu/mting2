package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.contract.AddFeedbackContact;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.presenter.AddFeedbackPresenter;
import cn.xylink.mting.utils.adapter.BaseAdapterHelper;
import cn.xylink.mting.utils.adapter.QuickAdapter;

public class FeedBackActivity extends BasePresenterActivity implements AddFeedbackContact.IAddFeedBackView {

    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.sn_type)
    Spinner snType;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.gv_content)
    GridView gvContent;

    private AddFeedbackPresenter addFeedbackPresenter;
    private QuickAdapter<String> mAdapter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_fadeback);
    }

    @Override
    protected void initView() {
        addFeedbackPresenter = (AddFeedbackPresenter) createPresenter(AddFeedbackPresenter.class);
        addFeedbackPresenter.attachView(this);
        mAdapter = new QuickAdapter<String>(this, R.layout.item_fadeback) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                ImageView ivItem = helper.getView(R.id.iv_item);
                if ("del".equals(item)) {
                    ivItem.setImageResource(R.mipmap.ico_add);
                }
            }
        };
        gvContent.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String type = intent.getExtras().getString("type");
            if ("detail".equals(type)) {
                String[] fadeType2 = getResources().getStringArray(R.array.fade_type2);
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fadeType2);
                snType.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        }
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
        TCAgent.onEvent(this, "sys_feedback");
        LinkCreateRequest linkCreateRequest = new LinkCreateRequest();
        linkCreateRequest.setType((String) snType.getSelectedItem());
        linkCreateRequest.setContent(etContent.getText().toString());
        linkCreateRequest.doSign();
        addFeedbackPresenter.onFeedBack(linkCreateRequest);
    }

    @Override
    public void onAddFeedBackSuccess(BaseResponse<String> response) {
        toastShort("反馈成功");
        finish();
    }

    @Override
    public void onBindCheckError(int code, String errorMsg) {
        toastShort("反馈失败");
    }
}
