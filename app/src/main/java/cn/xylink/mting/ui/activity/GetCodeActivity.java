package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.CheckInfo;
import cn.xylink.mting.bean.CodeInfo;
import cn.xylink.mting.contract.CheckPhoneContact;
import cn.xylink.mting.contract.GetCodeContact;
import cn.xylink.mting.model.CheckPhoneRequest;
import cn.xylink.mting.model.GetCodeRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.presenter.CheckPhonePresenter;
import cn.xylink.mting.presenter.GetCodePresenter;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.SafeUtils;
import cn.xylink.mting.utils.TingUtils;
import cn.xylink.mting.widget.PhoneCode;
import cn.xylink.mting.widget.ZpPhoneEditText;

public class GetCodeActivity extends BasePresenterActivity implements GetCodeContact.IGetCodeView, CheckPhoneContact.ICheckPhoneView {

    private GetCodePresenter codePresenter;
    private CheckPhonePresenter checkPhonePresenter;
    private CodeInfo codeInfo;
    public static final String EXTRA_TICKET = "extra_ticket";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_SOURCE = "extra_source";

    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    @BindView(R.id.et_phone)
    ZpPhoneEditText etPhone;
    @BindView(R.id.pc_code)
    PhoneCode pCcode;
    @BindView(R.id.tv_include_title)
    TextView tvTitle;


    private boolean isFinished;
    private String phone;
    private String codeID;
    private String ticket;
    private String source;

    CountDownTimer timer;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_get_code);
    }


    public void resetDownTimer() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isFinished = false;
                tvCountDown.setText(millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                isFinished = true;
                tvCountDown.setText("重新获取");
            }
        }.start();
    }

    @Override
    protected void initView() {
        if ("register".equals(source))
            resetDownTimer();
        pCcode.setOnCompleteListener(new PhoneCode.Listener() {
            @Override
            public void onComplete(String content) {
                L.v("content", content);
                CheckPhoneRequest requset = new CheckPhoneRequest();
                requset.source = "register";
                requset.codeId = codeID;
                requset.phone = phone.replaceAll(" ", "");
                requset.setDeviceId(TingUtils.getDeviceId(getApplicationContext()));
                try {
                    requset.code = SafeUtils.getRsaString(content, Const.publicKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requset.doSign();
                checkPhonePresenter.onCheckPhone(requset);


            }
        });

        etPhone.setText(phone);
        tvTitle.setText("手机号验证");
    }

    @Override
    protected void initData() {

        phone = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_PHONE);
        codeID = getIntent().getStringExtra(PhoneLoginActivity.EXTRA_CODE);
        source = getIntent().getStringExtra(EXTRA_SOURCE);

        codePresenter = (GetCodePresenter) createPresenter(GetCodePresenter.class);
        codePresenter.attachView(this);

        checkPhonePresenter = (CheckPhonePresenter) createPresenter(CheckPhonePresenter.class);
        checkPhonePresenter.attachView(this);

        requsetCode();
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onCodeSuccess(BaseResponse<CodeInfo> response) {
        if (response.data != null) {
            codeID = response.data.getCodeId();
        }
        if (response.code == 200) {
            resetDownTimer();
            return;
        }
    }

    @Override
    public void onCodeError(int code, String errorMsg) {

    }


    public void requsetCode() {
        GetCodeRequest requset = new GetCodeRequest();
        requset.phone = phone.replaceAll(" ", "");
        requset.source = source;
        requset.doSign();
        codePresenter.onGetCode(requset);
    }

    @OnClick(R.id.tv_count_down)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_count_down:
                if (isFinished) {
                    pCcode.clearText();
                    requsetCode();

                }
                break;
        }
    }

    @Override
    public void onCheckPhoneSuccess(BaseResponse<CheckInfo> response) {
        L.v("code", response.code);

        if (response.data != null) {
            ticket = response.data.getTicket();
            Intent mIntent = new Intent(this, SetPhonePwdActivity.class);
            mIntent.putExtra(EXTRA_TICKET, ticket);
            mIntent.putExtra(EXTRA_PHONE, phone.replaceAll(" ", ""));
            if(source.equals("register")){
                mIntent.putExtra(SetPhonePwdActivity.EXTRA_TYPE,1);
            }else if(source.equals("forgot"))
            {
                mIntent.putExtra(SetPhonePwdActivity.EXTRA_TYPE,2);
            }
            startActivity(mIntent);

        }
    }

    @Override
    public void onCheckPhoneError(int code, String errorMsg) {

    }
}
