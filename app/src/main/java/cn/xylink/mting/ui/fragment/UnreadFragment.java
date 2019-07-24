package cn.xylink.mting.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UnreadRequest;
import cn.xylink.mting.contract.UnreadContract;
import cn.xylink.mting.event.AddStoreSuccessEvent;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.DeleteArticleSuccessEvent;
import cn.xylink.mting.presenter.UnreadPresenter;
import cn.xylink.mting.speech.data.ArticleDataProvider;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.activity.ArticleDetailActivity;
import cn.xylink.mting.ui.adapter.UnreadAdapter;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.widget.SpaceItemDecoration;

/*
 *未读
 *
 * -----------------------------------------------------------------
 * 2019/7/8 14:03 : Create UnreadFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class UnreadFragment extends BaseMainTabFragment implements UnreadAdapter.OnItemClickListener, UnreadContract.IUnreadView {
    private UnreadAdapter mAdapter;
    private UnreadPresenter mPresenter;

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_unread;
    }

    @Override
    protected void initView(View view) {
        mPresenter = (UnreadPresenter) createPresenter(UnreadPresenter.class);
        mPresenter.attachView(this);
        mAdapter = new UnreadAdapter(getActivity(), SpeechList.getInstance().getArticleList(), this);
        mRecyclerView = view.findViewById(R.id.rv_unread);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
//        mRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        if (SpeechList.getInstance().getArticleList() == null || SpeechList.getInstance().getArticleList().size() < 1) {
            UnreadRequest request = new UnreadRequest();
//        request.setUpdateAt(0l);
            request.setEvent(UnreadRequest.ENENT_TYPE.refresh.name());
            request.doSign();
            mPresenter.createUnread(request);
        }
    }


    @Override
    public void onItemClick(Article article) {
//        mControllerListener.onPlay(article.getArticleId());
        Bundle bundle = new Bundle();
        bundle.putString("aid", article.getArticleId());
        ((BaseActivity) getActivity()).jumpActivity(ArticleDetailActivity.class, bundle);
        L.v();
    }

    @Override
    public void onItemMoreClick(Article article) {
        L.v();
        showBottonDialog(TAB_TYPE.UNREAD, article);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(SpeechStartEvent event) {
        L.v(event.getArticle());
        mAdapter.refreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechProgress(SpeechProgressEvent event) {
        L.v(event.getArticle());
        mAdapter.setProgress(event.getArticle());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        L.v(event);
        mAdapter.refreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechError(SpeechErrorEvent event) {
        L.v(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelSuccess(DeleteArticleSuccessEvent event) {
        L.v(event);
        if (event.getTab_type() == TAB_TYPE.UNREAD)
            mAdapter.refreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddUnread(AddUnreadEvent event) {
        L.v(event);
        mAdapter.refreshData();
        if (!TextUtils.isEmpty(event.getArticleID())) {
            Article article = new Article();
            article.setArticleId(event.getArticleID());
            ArticleDataProvider provider = new ArticleDataProvider(getActivity());
            provider.loadArticleContent(article, false, (errorCode, article1) -> {
                if (errorCode == 0) {
                    List<Article> list = new ArrayList<>();
                    list.add(article1);
                    SpeechList.getInstance().pushFront(list);
                    mAdapter.refreshData();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddStoreSuccess(AddStoreSuccessEvent event) {
        L.v(event);
        if (mAdapter != null && mAdapter.getArticleList() != null && mAdapter.getArticleList().size() > 0 && event.getArticle() != null) {
            for (int i = 0; i < mAdapter.getArticleList().size(); i++) {
                if (event.getArticle().getArticleId().equals(mAdapter.getArticleList().get(i).getArticleId())){
                    mAdapter.getArticleList().get(i).setStore(event.getArticle().getStore());
                    mAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public void onSuccessUnread(List<Article> unreadList) {
        if (unreadList != null) {
            SpeechList.getInstance().appendArticles(unreadList);
            mAdapter.refreshData();
            mControllerListener.onDataSuccess();
        }
    }

    @Override
    public void onErrorUnread(int code, String errorMsg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
