package cn.xylink.mting.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.ArticleDetailRequest;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.ArticleDetailContract;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.contract.LinkCreateContact;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.NotifyMainPlayEvent;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.presenter.ArticleDetailPresenter;
import cn.xylink.mting.presenter.CheckLinkPresenter;
import cn.xylink.mting.presenter.LinkCreatePresenter;
import cn.xylink.mting.service.AddUnreadService;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.ui.activity.PlayerlActivity;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.T;

/*
 *检测黏贴板
 *
 * -----------------------------------------------------------------
 * 2019/7/22 18:09 : Create CopyAddDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class CopyAddDialog extends BaseDimDialog implements
        ArticleDetailContract.IArticleDetailView, LinkCreateContact.IPushView {

    @BindView(R.id.ll_copy_add_content)
    LinearLayout mContentLayout;
    @BindView(R.id.ll_copy_add_loading)
    LinearLayout mLoadingLayout;
    @BindView(R.id.tv_copy_add_title)
    TextView mTitleView;
    @BindView(R.id.tv_copy_add_contact)
    TextView mContactView;
    @BindView(R.id.tv_copy_add_add_unread)
    TextView mAddUnreadView;
    @BindView(R.id.tv_copy_add_play)
    TextView mPlayView;
    @BindView(R.id.iv_copy_add_close)
    ImageView mCloseView;
    @BindView(R.id.btn_copy_add_fastadd)
    Button mFastAddBtn;
    private ArticleDetailPresenter mArticleDetailPresenter;
    private LinkCreatePresenter mLinkCreatePresenter;
    private String mUrl;

    public CopyAddDialog(Context context, String url) {
        super(context);
        mLinkCreatePresenter = new LinkCreatePresenter();
        mLinkCreatePresenter.attachView(this);
        mArticleDetailPresenter = new ArticleDetailPresenter();
        mArticleDetailPresenter.attachView(this);
        mUrl = url;
        mContactView.setText(url);
        EventBus.getDefault().register(this);
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_copy_add, null);
    }

    @OnClick({R.id.ll_copy_add_write, R.id.ll_copy_add_layout, R.id.iv_copy_add_close, R.id.tv_copy_add_play, R.id.tv_copy_add_add_unread,
            R.id.btn_copy_add_fastadd})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_add_add_unread:
                addUnread();
                break;
            case R.id.tv_copy_add_play:
                isPlay = true;
                addUnread();
                break;
            case R.id.iv_copy_add_close:
            case R.id.v_copy_add_nc:
            case R.id.ll_copy_add_layout:
                this.dismiss();
                break;
            case R.id.btn_copy_add_fastadd:
                Intent intent = new Intent(mContext, PlayerlActivity.class);
                intent.putExtra(PlayerlActivity.EXTRA_HTML, PlayerlActivity.PROTOCOL_URL);
                intent.putExtra(PlayerlActivity.EXTRA_TITLE, mContext.getResources().getString(R.string.player_mting));
                mContext.startActivity(intent);
                break;
        }
    }

    private boolean isPlay;

    private void addUnread() {
        mContentLayout.setVisibility(View.INVISIBLE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(mContext, AddUnreadService.class);
        intent.putExtra(AddUnreadService.EXTRA_URL, mUrl);
        intent.putExtra(AddUnreadService.EXTRA_ISPLAY, isPlay);
        mContext.startService(intent);

//        LinkCreateRequest request = new LinkCreateRequest();
//        request.setUrl(mUrl);
//        request.setInType(1);
//        request.doSign();
//        mLinkCreatePresenter.onPush(request);
    }

    private LinkArticle mLinkArticle;

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void dismiss() {
        mLinkCreatePresenter.deatchView();
        mArticleDetailPresenter.deatchView();
        EventBus.getDefault().unregister(this);
        super.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechResume(AddUnreadEvent event) {
        this.dismiss();
    }

    @Override
    public void onSuccessArticleDetail(ArticleDetailInfo info) {
        Article article = new Article();
        article.setProgress(0);
        article.setTitle(info.getTitle());
        article.setArticleId(info.getArticleId());
        article.setSourceName(info.getSourceName());
        article.setShareUrl(info.getShareUrl());
        article.setStore(info.getStore());
        article.setRead(info.getRead());
        article.setUpdateAt(info.getUpdateAt());
        List<Article> list = new ArrayList<>();
        list.add(article);
        SpeechList.getInstance().pushFront(list);
        EventBus.getDefault().post(new AddUnreadEvent());
        if (isPlay) {
            EventBus.getDefault().post(new NotifyMainPlayEvent(mLinkArticle.getArticleId()));
            Toast.makeText(mContext, "开始朗读文章", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "已添加到待读", Toast.LENGTH_SHORT).show();
        }
        this.dismiss();
    }

    @Override
    public void onErrorArticleDetail(int code, String errorMsg) {
        mPlayView.setEnabled(true);
        mAddUnreadView.setEnabled(true);
        this.dismiss();
    }

    @Override
    public void onPushSuccess(BaseResponse<LinkArticle> loginInfoBaseResponse) {
        mLinkArticle = loginInfoBaseResponse.data;
        ArticleDetailRequest request1 = new ArticleDetailRequest();
        request1.setArticleId(loginInfoBaseResponse.data.getArticleId());
        request1.doSign();
        mArticleDetailPresenter.createArticleDetail(request1);
    }

    @Override
    public void onPushError(int code, String errorMsg) {
        mLoadingLayout.setVisibility(View.GONE);
        Toast.makeText(mContext, "文章加载失败，请稍后再试", Toast.LENGTH_SHORT).show();
//        if (!TextUtils.isEmpty(errorMsg))
//            mTitleView.setText(errorMsg);
        mPlayView.setEnabled(true);
        mAddUnreadView.setEnabled(true);
        this.dismiss();
    }
}
