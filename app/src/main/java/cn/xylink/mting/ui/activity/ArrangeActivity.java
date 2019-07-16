package cn.xylink.mting.ui.activity;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.contract.AddUnreadContract;
import cn.xylink.mting.contract.DelMainContract;
import cn.xylink.mting.presenter.AddUnreadPresenter;
import cn.xylink.mting.presenter.CollectPresenter;
import cn.xylink.mting.presenter.DelMainPresenter;
import cn.xylink.mting.presenter.ReadedPresenter;
import cn.xylink.mting.presenter.UnreadPresenter;
import cn.xylink.mting.ui.adapter.ArrangeAdapter;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.widget.SpaceItemDecoration;

public class ArrangeActivity extends BasePresenterActivity implements AddUnreadContract.IAddUnreadView , DelMainContract.IDelMainView {

    @BindView(R.id.tv_arrange_title)
    TextView mTitleView;
    @BindView(R.id.tv_arrange_add_unread)
    TextView mAddUnreadView;
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
        mTabType = getIntent().getIntExtra("", 0);
        switch (mTabType) {
            case 0:
                mAddUnreadView.setVisibility(View.GONE);
                mUnreadCheckBox.setVisibility(View.GONE);
                mUnreadTextView.setVisibility(View.GONE);
                mTitleView.setText("待读");
                break;
            case 1:
                mTitleView.setText("已读");
                break;
            case 2:
                mTitleView.setText("收藏");
                break;
        }
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
        mRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ArrangeAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    @OnClick({R.id.iv_arrange_back, R.id.tv_arrange_del, R.id.tv_arrange_add_unread, R.id.cb_arrange_all_check, R.id.cb_arrange_unread_check})
    void onClick(View view){
        switch (view.getId()){
            case R.id.iv_arrange_back:
                this.finish();
                break;
            case R.id.tv_arrange_del:
                break;
            case R.id.tv_arrange_add_unread:
                break;
            case R.id.cb_arrange_all_check:
                break;
            case R.id.cb_arrange_unread_check:
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

    }

    @Override
    public void onErrorAddUnread(int code, String errorMsg) {

    }

    @Override
    public void onSuccessDel(String str) {

    }

    @Override
    public void onErrorDel(int code, String errorMsg) {

    }

    @Override
    public void onSuccessAddLove(String str) {

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
//                getReadedData();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerView != null)
            mRecyclerView.removeOnScrollListener(scrollListener);
    }
}
