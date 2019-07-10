package cn.xylink.mting.ui.fragment;

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
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.UnreadRequest;
import cn.xylink.mting.contract.UnreadContract;
import cn.xylink.mting.presenter.ReadedPresenter;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
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
public class ReadedFragment extends BaseMainTabFragment implements UnreadAdapter.OnItemClickListener, UnreadContract.IUnreadView{

    @BindView(R.id.rv_readed)
    RecyclerView mRecyclerView;
    private UnreadAdapter mAdapter;
    private ReadedPresenter mPresenter;

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_readed;
    }

    @Override
    protected void initView(View view) {
        mPresenter = (ReadedPresenter) createPresenter(ReadedPresenter.class);
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
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onItemClick(Article article) {
        List<Article> list = new ArrayList<>();
        list.add(article);
        SpeechList.getInstance().pushFront(list);
        mControllerListener.onPlay(article.getArticleId());
    }

    @Override
    public void onItemMoreClick(Article article) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
