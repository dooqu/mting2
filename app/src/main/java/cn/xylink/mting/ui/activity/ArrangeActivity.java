package cn.xylink.mting.ui.activity;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.AddUnreadRequest;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.DelReadedRequest;
import cn.xylink.mting.bean.DelUnreadRequest;
import cn.xylink.mting.bean.UnreadRequest;
import cn.xylink.mting.contract.AddUnreadContract;
import cn.xylink.mting.contract.DelMainContract;
import cn.xylink.mting.contract.UnreadContract;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.DeleteArticleSuccessEvent;
import cn.xylink.mting.presenter.AddUnreadPresenter;
import cn.xylink.mting.presenter.CollectPresenter;
import cn.xylink.mting.presenter.DelMainPresenter;
import cn.xylink.mting.presenter.ReadedPresenter;
import cn.xylink.mting.presenter.UnreadPresenter;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.ui.adapter.ArrangeAdapter;
import cn.xylink.mting.ui.fragment.BaseMainTabFragment;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.T;
import cn.xylink.mting.widget.SpaceItemDecoration;

public class ArrangeActivity extends BasePresenterActivity implements AddUnreadContract.IAddUnreadView, DelMainContract.IDelMainView
        , UnreadContract.IUnreadView, ArrangeAdapter.OnItemClickListener {

    public static String ACTION_ARRANGE_TYPE = "action_arrange_type";
    @BindView(R.id.tv_arrange_title)
    TextView mTitleView;
    @BindView(R.id.tv_arrange_add_unread)
    TextView mAddUnreadView;
    @BindView(R.id.tv_arrange_del)
    TextView mDelView;
    @BindView(R.id.rv_arrange)
    RecyclerView mRecyclerView;
    @BindView(R.id.cb_arrange_all_check)
    CheckBox mAllCheckBox;
    @BindView(R.id.cb_arrange_unread_check)
    CheckBox mUnreadCheckBox;
    @BindView(R.id.tv_arrange_unread)
    TextView mUnreadTextView;
    private int mTabType;
    private ArrangeAdapter mAdapter;
    private DelMainPresenter mDelMainPresenter;
    private AddUnreadPresenter mAddUnreadPresenter;
    private UnreadPresenter mUnreadPresenter;
    private ReadedPresenter mReadedPresenter;
    private CollectPresenter mCollectPresenter;
    private SpeechServiceProxy proxy;
    private SpeechService service;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_arrange);
    }

    @Override
    protected void initView() {
        mAddUnreadPresenter = (AddUnreadPresenter) createPresenter(AddUnreadPresenter.class);
        mAddUnreadPresenter.attachView(this);
        mDelMainPresenter = (DelMainPresenter) createPresenter(DelMainPresenter.class);
        mDelMainPresenter.attachView(this);
        mTabType = getIntent().getIntExtra(ACTION_ARRANGE_TYPE, 0);
        switch (mTabType) {
            case 0:
                mAddUnreadView.setVisibility(View.GONE);
                mUnreadCheckBox.setVisibility(View.GONE);
                mUnreadTextView.setVisibility(View.GONE);
                mTitleView.setText("待读");
                mUnreadPresenter = (UnreadPresenter) createPresenter(UnreadPresenter.class);
                mUnreadPresenter.attachView(this);
                break;
            case 1:
                mTitleView.setText("已读");
                mReadedPresenter = (ReadedPresenter) createPresenter(ReadedPresenter.class);
                mReadedPresenter.attachView(this);
                break;
            case 2:
                mUnreadCheckBox.setVisibility(View.GONE);
                mUnreadTextView.setVisibility(View.GONE);
                mTitleView.setText("收藏");
                mCollectPresenter = (CollectPresenter) createPresenter(CollectPresenter.class);
                mCollectPresenter.attachView(this);
                break;
        }
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
        mRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ArrangeAdapter(this, this);
        mAdapter.setTabType(mTabType);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);
        getInitData();

        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service) {
                if (connected) {
                    ArrangeActivity.this.service = service;
                }
            }
        };
        proxy.bind();
    }

    private void getInitData() {
        UnreadRequest request = new UnreadRequest();
        request.setEvent(UnreadRequest.ENENT_TYPE.more.name());
        request.doSign();
        switch (mTabType) {
            case 0:
                mUnreadPresenter.createUnread(request);
                break;
            case 1:
                mReadedPresenter.createUnread(request);
                break;
            case 2:
                mCollectPresenter.createUnread(request);
                break;
        }
    }

    private void getMoreData() {
        List<Article> list = mAdapter.getArticleList();
        long at = list != null && list.size() > 0 ? list.get(list.size() - 1).getUpdateAt() : 0;
        UnreadRequest request = new UnreadRequest();
        request.setUpdateAt(at);
        request.setEvent(UnreadRequest.ENENT_TYPE.more.name());
        request.doSign();
        switch (mTabType) {
            case 0:
                mUnreadPresenter.createUnread(request);
                break;
            case 1:
                mReadedPresenter.createUnread(request);
                break;
            case 2:
                mCollectPresenter.createUnread(request);
                break;
        }
    }

    private void delSelectData() {
        switch (mTabType) {
            case 0:
                DelUnreadRequest request = new DelUnreadRequest();
                request.setArticleIds(mAdapter.getSelectItemArticleID());
                request.doSign();
                mDelMainPresenter.delUnread(request);
                break;
            case 1:
                DelReadedRequest readedRequest = new DelReadedRequest();
                readedRequest.setIds(mAdapter.getSelectItemID());
                readedRequest.doSign();
                mDelMainPresenter.delReaded(readedRequest);
                break;
            case 2:
                DelReadedRequest Request = new DelReadedRequest();
                Request.setIds(mAdapter.getSelectItemID());
                Request.doSign();
                mDelMainPresenter.delConllect(Request);
                break;
        }
        showLoading();
    }

    private void addToUnread() {
        AddUnreadRequest request = new AddUnreadRequest();
        request.setArticleIds(mAdapter.getSelectItemArticleID());
        request.doSign();
        mAddUnreadPresenter.addUnread(request);
        showLoading();
    }

    @OnClick({R.id.iv_arrange_back, R.id.tv_arrange_del, R.id.tv_arrange_add_unread, R.id.cb_arrange_all_check, R.id.cb_arrange_unread_check})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_arrange_back:
                this.finish();
                break;
            case R.id.tv_arrange_del:
                delSelectData();
                break;
            case R.id.tv_arrange_add_unread:
                addToUnread();
                break;
            case R.id.cb_arrange_all_check:
                if (mAllCheckBox.isChecked())
                    mAdapter.selectAllItem(true);
                else
                    mAdapter.selectAllItem(false);
                break;
            case R.id.cb_arrange_unread_check:
                if (mUnreadCheckBox.isChecked())
                    mAdapter.selectAllUnreadItem(true);
                else
                    mAdapter.selectAllUnreadItem(false);
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onSuccessAddUnread(String msg) {
        hideLoading();
        T.s(this, "添加成功");
        service.addFirst(mAdapter.getSelectItemArticleArray());
        EventBus.getDefault().post(new AddUnreadEvent());
        this.finish();

    }

    @Override
    public void onErrorAddUnread(int code, String errorMsg) {
        hideLoading();
        T.s(this, "添加失败");
    }

    @Override
    public void onSuccessDel(String str) {
        hideLoading();
        T.s(this, "删除成功");
        service.removeFromSpeechList(mAdapter.getSelectItemArticleIDArray());
        if (mTabType == BaseMainTabFragment.TAB_TYPE.UNREAD.ordinal())
            EventBus.getDefault().post(new DeleteArticleSuccessEvent(BaseMainTabFragment.TAB_TYPE.UNREAD));
        if (mTabType == BaseMainTabFragment.TAB_TYPE.READED.ordinal())
            EventBus.getDefault().post(new DeleteArticleSuccessEvent(BaseMainTabFragment.TAB_TYPE.READED));
        if (mTabType == BaseMainTabFragment.TAB_TYPE.COLLECT.ordinal())
            EventBus.getDefault().post(new DeleteArticleSuccessEvent(BaseMainTabFragment.TAB_TYPE.COLLECT));
        this.finish();
    }

    @Override
    public void onErrorDel(int code, String errorMsg) {
        hideLoading();
        T.s(this, "删除失败");
    }

    @Override
    public void onSuccessAddLove(String str,Article article) {

    }

    @Override
    public void onErrorAddLove(int code, String errorMsg) {

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
            if (visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 30
                    && totalItemCount != mTotalItemCount) {
                mTotalItemCount = totalItemCount;
                getMoreData();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerView != null)
            mRecyclerView.removeOnScrollListener(scrollListener);
        proxy.unbind();
    }

    @Override
    public void onSuccessUnread(List<Article> unreadList) {
        if (unreadList != null & unreadList.size() > 0) {
            L.v(unreadList.size());
            mAdapter.setData(unreadList);
        }
    }

    @Override
    public void onErrorUnread(int code, String errorMsg) {

    }

    @Override
    public void checkChanged(int selectCount, boolean isSelectAllUnplay) {
        mUnreadCheckBox.setChecked(isSelectAllUnplay);
        if (selectCount > 0) {
            mTitleView.setText(mTitleView.getText().toString().substring(0, 2) + "(已选" + selectCount + ")");
            if (selectCount == mAdapter.getItemCount()) {
                mAllCheckBox.setChecked(true);
                mUnreadCheckBox.setChecked(true);
            } else {
                mAllCheckBox.setChecked(false);
            }
            mAddUnreadView.setTextColor(getResources().getColor(R.color.c488def));
            mDelView.setTextColor(getResources().getColor(R.color.c488def));
        } else {
            mAllCheckBox.setChecked(false);
            mUnreadCheckBox.setChecked(false);
            mTitleView.setText(mTitleView.getText().toString().substring(0, 2));
            mAddUnreadView.setTextColor(getResources().getColor(R.color.cbbbbbb));
            mDelView.setTextColor(getResources().getColor(R.color.cbbbbbb));
        }
    }
}
