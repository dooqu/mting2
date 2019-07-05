package cn.xylink.mting.ui.activity;

import cn.xylink.mting.R;
import cn.xylink.mting.bean.BaseResponse;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.bean.GetCodeRequest;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.TingUtils;

public class GetCodeActivity extends BasePresenterActivity implements GetCodeContact.IGetCodeView {

    private GetCodePresenter codePresenter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_get_code);
    }

    @Override
    protected void initView() {
        codePresenter = (GetCodePresenter) createPresenter(GetCodePresenter.class);
        codePresenter.attachView(this);
    }

    @Override
    protected void initData() {

        GetCodeRequest requset = new GetCodeRequest();
        requset.deviceId = TingUtils.getDeviceId(getApplicationContext());
        requset.phone = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_PHONE);
        requset.source = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_SOURCE);
        requset.doSign();
        codePresenter.onGetCode(requset);

    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> loginInfoBaseResponse) {
        L.v(loginInfoBaseResponse.message);
    }

    @Override
    public void onCodeError(int code, String errorMsg) {

    }
}
