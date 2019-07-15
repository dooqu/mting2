package cn.xylink.mting.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.contract.LinkCreateContact;
import cn.xylink.mting.event.TwoArticleEvent;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.presenter.CheckLinkPresenter;
import cn.xylink.mting.presenter.LinkCreatePresenter;
import cn.xylink.mting.utils.L;

public class AddTwoNoteFragment extends BasePresenterFragment implements LinkCreateContact.IPushView, CheckLinkContact.ICheckLinkView {


    @BindView(R.id.et_article_title)
    EditText etLink;
    @BindView(R.id.tv_content)
    TextView tv_content;

    public int inLink = 2;
    private LinkCreatePresenter linkCreatePresenter;
    private CheckLinkPresenter checkLinkPresenter;

    public static AddTwoNoteFragment newInstance(Bundle args) {
        AddTwoNoteFragment fragment = new AddTwoNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutViewId() {
        return  R.layout.fragment_add_aricle_2;
    }

    @Override
    protected void initView(View view) {
        EventBus.getDefault().register(this);

        linkCreatePresenter = (LinkCreatePresenter) createPresenter(LinkCreatePresenter.class);
        linkCreatePresenter.attachView(this);

        checkLinkPresenter = (CheckLinkPresenter) createPresenter(CheckLinkPresenter.class);
        checkLinkPresenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.tv_preview)
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_preview:
                String link = etLink.getText().toString();
                if(TextUtils.isEmpty(link)){
                    Toast.makeText(this.getContext(),"地址不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                checkLinkUrl(link);
                break;
        }
    }

    public void checkLinkUrl(String link){
        CheckLinkUrlRequset  request = new CheckLinkUrlRequset();
        request.setUrl(link);
        request.doSign();
        checkLinkPresenter.onCheckLink(request);
    }

    //添加文章
    public void linkPushRequset(String link)
    {
        LinkCreateRequest request = new LinkCreateRequest();
        request.setUrl(link);
        request.setInType(inLink);
        request.doSign();
        linkCreatePresenter.onPush(request);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TwoArticleEvent event) {
        L.v("eventType", 2);
        String link = etLink.getText().toString();
        if(TextUtils.isEmpty(link)){
            Toast.makeText(this.getContext(),"地址不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        link = link.trim().replaceAll(" ","");
        linkPushRequset(link);
    }

    @Override
    public void onPushSuccess(BaseResponse<LinkArticle> response) {
        L.v(response.data);
//        String title = response.data.getTitle();
//        String describe = response.data.getDescribe();
//        L.v("title",title);
//        L.v("describle",describe);
//        tv_content.setText(title +"\n" + describe);
        getActivity().finish();
    }

    @Override
    public void onPushError(int code, String errorMsg) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onCheckLinkSuccess(BaseResponse<LinkArticle> response) {
        L.v(response.data);
        String title = response.data.getTitle();
        String describe = response.data.getDescribe();
        L.v("title",title);
        L.v("describle",describe);
        tv_content.setText(title +"\n" + describe);
    }

    @Override
    public void onCheckLinkError(int code, String errorMsg) {

    }
}
