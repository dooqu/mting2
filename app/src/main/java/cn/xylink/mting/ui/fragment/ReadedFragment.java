package cn.xylink.mting.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cn.xylink.mting.event.DeleteArticleSuccessEvent;
import cn.xylink.mting.presenter.ReadedPresenter;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechEndEvent;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.activity.ArticleDetailActivity;
import cn.xylink.mting.ui.adapter.ReadedAdapter;
import cn.xylink.mting.ui.adapter.UnreadAdapter;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.widget.SpaceItemDecoration;

/*
 *已读
 *
 * -----------------------------------------------------------------
 * 2019/7/8 14:05 : Create ReadedFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ReadedFragment extends BaseMainTabFragment implements UnreadAdapter.OnItemClickListener, UnreadContract.IUnreadView {

    @BindView(R.id.rv_readed)
    RecyclerView mRecyclerView;
    private ReadedAdapter mAdapter;
    private ReadedPresenter mPresenter;

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_readed;
    }

    @Override
    protected void initView(View view) {
        mPresenter = (ReadedPresenter) createPresenter(ReadedPresenter.class);
        mPresenter.attachView(this);
        mAdapter = new ReadedAdapter(getActivity(), null, this);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
        mRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        getInitData();
    }

    private void getInitData(){
        UnreadRequest request = new UnreadRequest();
        request.setEvent(UnreadRequest.ENENT_TYPE.refresh.name());
        request.doSign();
        mPresenter.createUnread(request);
    }

    private void getReadedData() {
        List<Article> list = mAdapter.getArticleList();
        long at = list != null && list.size() > 0 ? list.get(list.size() - 1).getUpdateAt() : 0;
        UnreadRequest request = new UnreadRequest();
        request.setUpdateAt(at);
        request.setEvent(UnreadRequest.ENENT_TYPE.refresh.name());
        request.doSign();
        mPresenter.createUnread(request);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onItemClick(Article article) {
        List<Article> list = new ArrayList<>();
        article.setProgress(0);
        list.add(article.clone());

        SpeechList.getInstance().pushFront(list);
        mControllerListener.onPlay(article.getArticleId());

        Bundle bundle = new Bundle();
        bundle.putString("aid", article.getArticleId());
        ((BaseActivity) getActivity()).jumpActivity(ArticleDetailActivity.class, bundle);
    }

    @Override
    public void onItemMoreClick(Article article) {
        showBottonDialog(TAB_TYPE.READED,article);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(SpeechStartEvent event) {
        L.v(event.getArticle());
        mAdapter.refreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechProgress(SpeechProgressEvent event) {
        L.v(event.getArticle());
//        mAdapter.setProgress(event.getArticle());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        L.v(event);
        mAdapter.refreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechEnd(SpeechEndEvent event) {
        L.v(event);
        mAdapter.clearData();
        getReadedData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechError(SpeechErrorEvent event) {
        L.v(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelSuccess(DeleteArticleSuccessEvent event) {
        L.v(event);
        if (event.getTab_type()==TAB_TYPE.READED)
            getInitData();
    }

    @Override
    public void onSuccessUnread(List<Article> unreadList) {
        if (unreadList != null) {
            mAdapter.setData(unreadList);
        }
    }

    @Override
    public void onErrorUnread(int code, String errorMsg) {

    }

    private int mTotalItemCount = 0;
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            L.v();
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = manager.getChildCount();
            int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
            int totalItemCount = manager.getItemCount();
            L.v("visibleItemCount=" + visibleItemCount);
            L.v("lastVisibleItemPosition=" + lastVisibleItemPosition);
            L.v("totalItemCount=" + totalItemCount);
            L.v("mTotalItemCount=" + mTotalItemCount);
            if (visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 15
                    && totalItemCount != mTotalItemCount) {
                mTotalItemCount = totalItemCount;
                getReadedData();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
