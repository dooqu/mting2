package cn.xylink.mting.ui.fragment;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UnreadRequest;
import cn.xylink.mting.contract.UnreadContract;
import cn.xylink.mting.presenter.UnreadPresenter;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.adapter.UnreadAdapter;
import cn.xylink.mting.utils.DensityUtil;
import cn.xylink.mting.utils.L;

/*
 *未读
 *
 * -----------------------------------------------------------------
 * 2019/7/8 14:03 : Create UnreadFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class UnreadFragment extends BaseMainTabFragment implements UnreadAdapter.OnItemClickListener, UnreadContract.IUnreadView {
    @BindView(R.id.rv_unread)
    RecyclerView mRecyclerView;
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
        mAdapter = new UnreadAdapter(getActivity(), SpeechList.getInstance().getArticleList(),this);
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
        UnreadRequest request = new UnreadRequest();
        request.setUpdateAt(0);
        request.setEvent(UnreadRequest.ENENT_TYPE.refresh.name());
        request.doSign();
        mPresenter.createUnread(request);
    }


    @Override
    public void onItemClick(Article article) {
        mControllerListener.onPlay(article.getArticleId());
        L.v();
    }
    @Override
    public void onItemMoreClick(Article article) {
        L.v();
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

    @Override
    public void onSuccessUnread(List<Article> unreadList) {
        if (unreadList!=null){
            SpeechList.getInstance().appendArticles(unreadList);
            mAdapter.refreshData();
        }
    }

    @Override
    public void onErrorUnread(int code, String errorMsg) {

    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int space = DensityUtil.dip2pxComm(getActivity(), 32);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = space;
            } else {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = space;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
