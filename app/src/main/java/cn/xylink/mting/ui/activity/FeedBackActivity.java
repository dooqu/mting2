package cn.xylink.mting.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.contract.AddFeedbackContact;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.presenter.AddFeedbackPresenter;

public class FeedBackActivity extends BasePresenterActivity implements AddFeedbackContact.IAddFeedBackView {

    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.sn_type)
    Spinner snType;
    @BindView(R.id.et_content)
    EditText etContent;

    private AddFeedbackPresenter addFeedbackPresenter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_fadeback);
    }

    @Override
    protected void initView() {
        addFeedbackPresenter = (AddFeedbackPresenter) createPresenter(AddFeedbackPresenter.class);
        addFeedbackPresenter.attachView(this);
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
