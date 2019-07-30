package cn.xylink.mting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.AddUnreadRequest;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.ArticleDetailRequest;
import cn.xylink.mting.bean.SearchRequeest;
import cn.xylink.mting.bean.SearchResultInfo;
import cn.xylink.mting.contract.AddUnreadContract;
import cn.xylink.mting.contract.ArticleDetailContract;
import cn.xylink.mting.contract.SearchContract;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.NotifyMainPlayEvent;
import cn.xylink.mting.presenter.AddUnreadPresenter;
import cn.xylink.mting.presenter.ArticleDetailPresenter;
import cn.xylink.mting.presenter.SearchPresenter;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.ui.adapter.SearchAdapter;
import cn.xylink.mting.ui.dialog.SearchArticleDetailDialog;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.widget.EditTextWidthClear;

public class SearchActivity extends BasePresenterActivity implements SearchContract.ISearchView, SearchAdapter.OnItemClickListener
        , ArticleDetailContract.IArticleDetailView, AddUnreadContract.IAddUnreadView, SearchArticleDetailDialog.OnBottomSelectDialogListener {

    @BindView(R.id.et_search)
    EditTextWidthClear mEditView;
    @BindView(R.id.ll_search_empty_layout)
    LinearLayout mEnptyLayout;
    @BindView(R.id.rv_search)
    RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private SearchPresenter mPresenter;
    private ArticleDetailPresenter mArticleDetailPresenter;
    private AddUnreadPresenter mAddUnreadPresenter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initView() {
//        mRecyclerView.addItemDecoration(null);
        mRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    protected void initData() {
        mPresenter = (SearchPresenter) createPresenter(SearchPresenter.class);
        mPresenter.attachView(this);
        mArticleDetailPresenter = (ArticleDetailPresenter) createPresenter(ArticleDetailPresenter.class);
        mArticleDetailPresenter.attachView(this);
        mAddUnreadPresenter = (AddUnreadPresenter) createPresenter(AddUnreadPresenter.class);
        mAddUnreadPresenter.attachView(this);
        mEditView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditView, 0);
            }
        }, 300);
    }

    @Override
    protected void initTitleBar() {

    }

    @OnEditorAction(R.id.et_search)
    boolean onEditorAction(KeyEvent key) {
        if (TextUtils.isEmpty(mEditView.getText().toString())) {
        } else {
            SearchRequeest request = new SearchRequeest();
            request.setQuery(mEditView.getText().toString());
            request.setPage(1);
            request.doSign();
            mPresenter.search(request);
            mAdapter.clearData();
            mTotalItemCount = 0;
            mCurrentPage = 1;
            showLoading();
        }
        return true;
    }

    private int mCurrentPage = 1;

    private void searchForPage(int page) {
        SearchRequeest request = new SearchRequeest();
        request.setQuery(mEditView.getText().toString());
        request.setPage(page);
        request.doSign();
        mPresenter.search(request);
    }

    @OnClick({R.id.tv_search_cancel, R.id.fl_contact,R.id.rv_search})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search_cancel:
                this.finish();
                break;
            case R.id.rv_search:
            case R.id.fl_contact:
                InputMethodManager imm = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditView.getWindowToken(), 0);
                break;
        }
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
            if (visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 20
                    && totalItemCount != mTotalItemCount) {
                mTotalItemCount = totalItemCount;
                ++mCurrentPage;
                searchForPage(mCurrentPage);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerView != null)
            mRecyclerView.removeOnScrollListener(scrollListener);
    }

    @Override
    public void onSuccessSearch(List<SearchResultInfo> unreadList) {
        hideLoading();
        if (unreadList != null && unreadList.size() > 0) {
            mAdapter.setData(unreadList);
            mEnptyLayout.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else if (mAdapter.getItemCount() == 0) {
            mEnptyLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onErrorSearch(int code, String errorMsg) {
        hideLoading();
//        mEnptyLayout.setVisibility(View.VISIBLE);
//        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(SearchResultInfo article) {
        ArticleDetailRequest request = new ArticleDetailRequest();
        request.setArticleId(article.getArticleId());
        request.doSign();
        mArticleDetailPresenter.createArticleDetail(request);
        showLoading();
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    public void onSuccessArticleDetail(ArticleDetailInfo info) {
        hideLoading();
        SearchArticleDetailDialog detailDialog = new SearchArticleDetailDialog(this);
        detailDialog.setListener(this);
        detailDialog.show(info);
    }

    @Override
    public void onErrorArticleDetail(int code, String errorMsg) {
        hideLoading();
    }

    @Override
    public void onSuccessAddUnread(String msg) {

    }

    @Override
    public void onErrorAddUnread(int code, String errorMsg) {

    }

    @Override
    public void onAddUnrad(ArticleDetailInfo info) {
        addUread(info.getArticleId());
        addSpeechList(info);
        EventBus.getDefault().post(new AddUnreadEvent());
    }

    private void addSpeechList(ArticleDetailInfo info) {
        Article article =new Article();
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
    }

    @Override
    public void onPlay(ArticleDetailInfo info) {
        addUread(info.getArticleId());
        addSpeechList(info);
        EventBus.getDefault().post(new NotifyMainPlayEvent(info.getArticleId()));
    }

    private void addUread(String id) {
        AddUnreadRequest request = new AddUnreadRequest();
        request.setArticleIds(id);
        request.doSign();
        mAddUnreadPresenter.addUnread(request);
    }
}
