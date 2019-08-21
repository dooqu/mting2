package cn.xylink.mting.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.tendcloud.tenddata.TCAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UnreadRequest;
import cn.xylink.mting.contract.UnreadContract;
import cn.xylink.mting.event.AddStoreSuccessEvent;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.DeleteArticleSuccessEvent;
import cn.xylink.mting.event.PlayBarVisibilityEvent;
import cn.xylink.mting.presenter.UnreadPresenter;
import cn.xylink.mting.speech.data.ArticleDataProvider;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.activity.ArticleDetailActivity;
import cn.xylink.mting.ui.activity.PlayerlActivity;
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
    public static boolean ISINIT = false;
    private UnreadAdapter mAdapter;
    private UnreadPresenter mPresenter;
    @BindView(R.id.ll_empty)
    LinearLayout mEnptyLayout;
    @BindView(R.id.ll_empty_first)
    LinearLayout mEnptyFirstLayout;
    @BindView(R.id.ll_network_error)
    LinearLayout mNetworkErrorLayout;

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
        L.v();
        if (SpeechList.getInstance().getArticleList() == null || SpeechList.getInstance().getArticleList().size() < 1) {
            UnreadRequest request = new UnreadRequest();
//        request.setUpdateAt(0l);
            request.setEvent(UnreadRequest.ENENT_TYPE.refresh.name());
            request.doSign();
            mPresenter.createUnread(request);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new PlayBarVisibilityEvent(mRecyclerView.getVisibility()));
            if (mControllerListener != null)
                mControllerListener.onDataSuccess();
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
        TCAgent.onEvent(getActivity(),"article_more");
        showBottonDialog(TAB_TYPE.UNREAD, article);
    }

    @OnClick({R.id.tv_unread_empty_first,R.id.ll_network_error})
    void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_unread_empty_first:
                startActivity(new Intent(getActivity(), PlayerlActivity.class));
                break;
            case R.id.ll_network_error:
                initData();
                break;
        }
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
        if (event.getStopReason() == SpeechStopEvent.StopReason.ListIsNull) {
            mEnptyLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mEnptyFirstLayout.setVisibility(View.GONE);
            mNetworkErrorLayout.setVisibility(View.GONE);
            EventBus.getDefault().post(new PlayBarVisibilityEvent(mRecyclerView.getVisibility()));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechError(SpeechErrorEvent event) {
        L.v(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelSuccess(DeleteArticleSuccessEvent event) {
        L.v(event);
        if (event.getTab_type() == TAB_TYPE.UNREAD) {
            mAdapter.refreshData();
            if (mAdapter.getItemCount() < 1) {
                mEnptyLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mEnptyFirstLayout.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.GONE);
                EventBus.getDefault().post(new PlayBarVisibilityEvent(mRecyclerView.getVisibility()));
            }
        } else if (event.getTab_type() == TAB_TYPE.COLLECT) {
            List<String> ids = event.getIds();
            if (ids != null && ids.size() > 0 && mAdapter != null && mAdapter.getArticleList() != null && mAdapter.getArticleList().size() > 0) {
                for (String id:ids){
                    for (int i = 0; i < mAdapter.getArticleList().size(); i++) {
                        if (id.equals(mAdapter.getArticleList().get(i).getArticleId())) {
                            mAdapter.getArticleList().get(i).setStore(0);
                            mAdapter.notifyItemChanged(i);
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddUnread(AddUnreadEvent event) {
        L.v(event);
        if (SpeechList.getInstance().getCurrent() == null && mControllerListener != null) {
            mControllerListener.onDataSuccess();
        }
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
        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new PlayBarVisibilityEvent(mRecyclerView.getVisibility()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddStoreSuccess(AddStoreSuccessEvent event) {
        L.v(event);
        if (mAdapter != null && mAdapter.getArticleList() != null && mAdapter.getArticleList().size() > 0 && event.getArticle() != null) {
            for (int i = 0; i < mAdapter.getArticleList().size(); i++) {
                if (event.getArticle().getArticleId().equals(mAdapter.getArticleList().get(i).getArticleId())) {
                    mAdapter.getArticleList().get(i).setStore(event.getArticle().getStore());
                    mAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public void onSuccessUnread(List<Article> unreadList, int used) {
        ISINIT = true;
        if (unreadList != null) {
            SpeechList.getInstance().appendArticles(unreadList);
            mAdapter.refreshData();
            mControllerListener.onDataSuccess();
            if (unreadList.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new PlayBarVisibilityEvent(mRecyclerView.getVisibility()));
            } else {
                if (used > 0)
                    mEnptyLayout.setVisibility(View.VISIBLE);
                else
                    mEnptyFirstLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onErrorUnread(int code, String errorMsg) {
//        if (code > 9999) {
        mNetworkErrorLayout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
