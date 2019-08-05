package cn.xylink.mting.ui.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.AddFeedbackContact;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.contract.LinkCreateContact;
import cn.xylink.mting.event.AddArticleHomeEvent;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.TwoArticleEvent;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.presenter.AddFeedbackPresenter;
import cn.xylink.mting.presenter.CheckLinkPresenter;
import cn.xylink.mting.presenter.LinkCreatePresenter;
import cn.xylink.mting.ui.activity.HtmlActivity;
import cn.xylink.mting.ui.dialog.CheckArticleDialog;
import cn.xylink.mting.ui.dialog.LoadingDialog;
import cn.xylink.mting.utils.DateUtils;
import cn.xylink.mting.utils.L;

public class AddTwoNoteFragment extends BasePresenterFragment implements LinkCreateContact.IPushView, CheckLinkContact.ICheckLinkView, AddFeedbackContact.IAddFeedBackView {

    @BindView(R.id.et_article_title)
    EditText etLink;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.tv_preview)
    TextView tvPreview;
    @BindView(R.id.tv_feedback)
    TextView tvFeedback;
    @BindView(R.id.ll_error)
    LinearLayout llError;
    @BindView(R.id.tv_loading_error)
    TextView tvLoadingError;
    @BindView(R.id.v_1)
    View v_1;
    @BindView(R.id.pb_speech_bar)
    ImageView pb_speech_bar;

    //文章类型 1 手动添加， 2 链接添加
    public int inLink = 2;
    private LinkCreatePresenter linkCreatePresenter;
    private CheckLinkPresenter checkLinkPresenter;
    private AddFeedbackPresenter addFeedbackPresenter;

    private LinkArticle linkArticle;
    private String responseUrl;

    private volatile boolean isStop;

    AnimationDrawable animationDrawable;

    public static AddTwoNoteFragment newInstance(Bundle args) {
        AddTwoNoteFragment fragment = new AddTwoNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        L.v("isVisibleToUser", isVisibleToUser);
        if (!isVisibleToUser) {
            EventBus.getDefault().post(new AddArticleHomeEvent(0));
        } else {
            if (!TextUtils.isEmpty(tv_content.getText())) {
                EventBus.getDefault().post(new AddArticleHomeEvent(1));
            }
        }
    }

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_add_aricle_2;
    }

    @Override
    protected void initView(View view) {
        EventBus.getDefault().register(this);

        etLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                L.v("s.length", s.length());
                if (s.length() > 0) {
                    tvPreview.setTextColor(getResources().getColor(R.color.color_blue));
                } else {
                    tvPreview.setText(R.string.load_on);
                    tvPreview.setVisibility(View.VISIBLE);
                    tvPreview.setTextColor(getResources().getColor(R.color.color_login_text_gray));
                }
            }
        });
    }

    public void startAnim() {
        pb_speech_bar.setImageResource(R.drawable.gif_loading);
        animationDrawable = (AnimationDrawable) pb_speech_bar.getDrawable();
        animationDrawable.start();
    }

    public void stopAnim() {
        if (animationDrawable != null)
            animationDrawable.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {

        linkCreatePresenter = (LinkCreatePresenter) createPresenter(LinkCreatePresenter.class);
        linkCreatePresenter.attachView(this);

        checkLinkPresenter = (CheckLinkPresenter) createPresenter(CheckLinkPresenter.class);
        checkLinkPresenter.attachView(this);

        addFeedbackPresenter = (AddFeedbackPresenter) createPresenter(AddFeedbackPresenter.class);
        addFeedbackPresenter.attachView(this);

    }

    @OnClick({R.id.tv_preview, R.id.tv_feedback})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_preview:
                isStop = !isStop;
                if (isStop) {
                    pb_speech_bar.setVisibility(View.GONE);
                    v_1.setVisibility(View.VISIBLE);
                    tvPreview.setText(R.string.load_stop);
                } else {
                    stopAnim();
                    if (tvPreview.getText().toString().equals(getResources().getString(R.string.load_refresh))) {
                        tvPreview.setText(R.string.load_refresh);
                    } else {
                        tvPreview.setText(R.string.load_on);
                    }
                }
                if (!isStop)
                    return;
                String link = etLink.getText().toString();
                if (TextUtils.isEmpty(link)) {
                    Toast.makeText(this.getContext(), "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                startAnim();
                link = link.trim().replaceAll(" ", "");
                pb_speech_bar.setVisibility(View.VISIBLE);
                v_1.setVisibility(View.GONE);
                checkLinkUrl(link);
                break;
            case R.id.tv_feedback:
                LinkCreateRequest request = new LinkCreateRequest();
                request.setUrl(etLink.getText().toString());
                request.doSign();
                addFeedbackPresenter.onFeedBack(request);
                break;
        }
    }

    //检查文章
    public void checkLinkUrl(String link) {
        CheckLinkUrlRequset request = new CheckLinkUrlRequset();
        request.setUrl(link);
        request.doSign();
        checkLinkPresenter.onCheckLink(request);
    }

    //添加文章
    public void linkPushRequset(String link) {
        LinkCreateRequest request = new LinkCreateRequest();
        request.setUrl(link);
        request.setInType(inLink);
        request.doSign();
        linkCreatePresenter.onPush(request);
    }


    @Override
    public void onResume() {
        super.onResume();
        ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb.getPrimaryClip() != null) {
            String fristText = cmb.getPrimaryClip().getItemAt(0).getText().toString();
            L.v("fristText");
            if (fristText.startsWith("http://") || fristText.startsWith("https://")) {
                etLink.setText(fristText);
                tvPreview.setTextColor(getResources().getColorStateList(R.color.color_blue));
            }
        }
    }

    /**
     * 保存，更新到待读
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TwoArticleEvent event) {
        L.v("eventType", 2);
        String link = responseUrl;
        if (!TextUtils.isEmpty(etLink.getText()) && TextUtils.isEmpty(link)) {
            Toast.makeText(this.getContext(), "请先点击预览", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(link)) {
            Toast.makeText(this.getContext(), "不能解析空地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (linkArticle.getExistUnread() == 1 || linkArticle.getExistUnread() == 2 ) {
            final CheckArticleDialog checkArticleDialog = new CheckArticleDialog(getContext());
            checkArticleDialog.setCanceledOnTouchOutside(true);
            checkArticleDialog.setData(linkArticle.getTitle(), new CheckArticleDialog.MessageListener() {
                @Override
                public void onUpdate() {
                    linkPushRequset(responseUrl.trim().replaceAll(" ", ""));
//                    checkArticleDialog.dismiss();
                }

                @Override
                public void onLook() {
                    Intent mIntent = new Intent(getActivity(), HtmlActivity.class);
                    mIntent.putExtra(HtmlActivity.EXTRA_HTML, responseUrl);
                    startActivity(mIntent);
                }
            });

            String day = DateUtils.getDateText(new Date(), "yyyy年MM月dd日");
            String hint = getResources().getString(R.string.update_msg_hint);
            String msg = String.format(hint, day);
            checkArticleDialog.setUpdateMsg(msg);
            checkArticleDialog.show();
        }else{
            linkPushRequset(responseUrl.trim().replaceAll(" ", ""));
        }
    }

    @Override
    public void onPushSuccess(BaseResponse<LinkArticle> response) {
        L.v(response.data);

        Toast.makeText(getContext(), "已加入待读", Toast.LENGTH_SHORT).show();
        AddUnreadEvent event = new AddUnreadEvent();
        event.setArticleID(response.data.getArticleId());
        EventBus.getDefault().post(event);
        getActivity().finish();
    }

    @Override
    public void onPushError(int code, String errorMsg) {
        L.v("code", code);
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void onCheckLinkSuccess(BaseResponse<LinkArticle> response) {
        L.v(response.data);
        tvPreview.setText(R.string.load_refresh);
        stopAnim();
        pb_speech_bar.setVisibility(View.GONE);
        v_1.setVisibility(View.VISIBLE);
        if (!isStop)
            return;
        linkArticle = response.data;
        String title = response.data.getTitle();
        String describe = response.data.getContent();
        responseUrl = response.data.getUrl();
        L.v("title", title);
        L.v("describle", describe);
        String sourcename = linkArticle.getSourceName();
        if(sourcename.equals("null"))
        {
            sourcename = "";
            tv_content.setText(title + "\n\n" + describe);
        }else{

            tv_content.setText(title + "\n\n"+ linkArticle.getSourceName() + "\n\n" + describe);
        }

        tvFeedback.setVisibility(View.VISIBLE);
        if (llError.getVisibility() == View.VISIBLE) {
            llError.setVisibility(View.GONE);
        }
        if (describe.length() > 0) {
            EventBus.getDefault().post(new AddArticleHomeEvent(1));
        } else {
            EventBus.getDefault().post(new AddArticleHomeEvent(0));
        }
    }

    @Override
    public void onCheckLinkError(int code, String errorMsg) {
        L.v(code);
        switch (code) {
            case -3:
                tvFeedback.setVisibility(View.VISIBLE);
                llError.setVisibility(View.VISIBLE);
                tvLoadingError.setVisibility(View.VISIBLE);
                stopAnim();
                pb_speech_bar.setVisibility(View.GONE);
                v_1.setVisibility(View.VISIBLE);
                break;
        }
        tvPreview.setText("重新加载");
    }

    @Override
    public void onAddFeedBackSuccess(BaseResponse<String> response) {
        Toast.makeText(this.getContext(), "反馈成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBindCheckError(int code, String errorMsg) {
        Toast.makeText(this.getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}