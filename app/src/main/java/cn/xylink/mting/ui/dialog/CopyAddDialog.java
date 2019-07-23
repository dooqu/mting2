package cn.xylink.mting.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

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
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.utils.L;

/*
 *检测黏贴板
 *
 * -----------------------------------------------------------------
 * 2019/7/22 18:09 : Create CopyAddDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class CopyAddDialog extends BaseDimDialog implements CheckLinkContact.ICheckLinkView,
        ArticleDetailContract.IArticleDetailView , LinkCreateContact.IPushView {

    @BindView(R.id.ll_copy_add_layout)
    LinearLayout mRoot;
    @BindView(R.id.ll_copy_add_write)
    LinearLayout mWriteLayout;
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
    private CheckLinkPresenter mCheckLinkPresenter;
    private ArticleDetailPresenter mArticleDetailPresenter;
    private LinkCreatePresenter mLinkCreatePresenter;
    private String mUrl;

    public CopyAddDialog(Context context, String url) {
        super(context);
        mCheckLinkPresenter = new CheckLinkPresenter();
        mCheckLinkPresenter.attachView(this);
        mLinkCreatePresenter = new LinkCreatePresenter();
        mLinkCreatePresenter.attachView(this);
        mArticleDetailPresenter = new ArticleDetailPresenter();
        mArticleDetailPresenter.attachView(this);
        mUrl = url;
        CheckLinkUrlRequset request = new CheckLinkUrlRequset();
        request.setUrl(url);
        request.doSign();
        mCheckLinkPresenter.onCheckLink(request);
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_copy_add, null);
    }

    @OnClick({R.id.ll_copy_add_write, R.id.ll_copy_add_layout, R.id.iv_copy_add_close, R.id.tv_copy_add_play, R.id.tv_copy_add_add_unread})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_add_add_unread:
                L.v();
                addUnread();
                mPlayView.setEnabled(false);
                break;
            case R.id.tv_copy_add_play:
                isPlay = true;
                addUnread();
                mAddUnreadView.setEnabled(false);
                L.v();
                break;
            case R.id.iv_copy_add_close:
                this.dismiss();
                break;
        }
    }

    private boolean isPlay;

    private void addUnread() {
        LinkCreateRequest request = new LinkCreateRequest();
        request.setUrl(mUrl);
        request.setInType(1);
        request.doSign();
        mLinkCreatePresenter.onPush(request);
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
        mCheckLinkPresenter.deatchView();
        mArticleDetailPresenter.deatchView();
        super.dismiss();
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
        }
        this.dismiss();
    }

    @Override
    public void onErrorArticleDetail(int code, String errorMsg) {

    }

    @Override
    public void onCheckLinkSuccess(BaseResponse<LinkArticle> response) {
        mLoadingLayout.setVisibility(View.GONE);
        mTitleView.setText(response.getData().getTitle());
        mContactView.setText(response.getData().getDescribe());
        mAddUnreadView.setEnabled(true);
        mPlayView.setEnabled(true);
    }

    //200:成功
//-1: 签名错误
//-2:url链接无效
//-3:解析正文失败
//-4:文章正文超限,max limit 20w character
//
//-999:token失效或登录超时
//-100000:异常,请核实后重试!
    @Override
    public void onCheckLinkError(int code, String errorMsg) {
        mLoadingLayout.setVisibility(View.GONE);
        mTitleView.setText(errorMsg);
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
        mTitleView.setText(errorMsg);
    }
}
