package cn.xylink.mting.ui.fragment;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.speech.data.SpeechList;
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
public class UnreadFragment extends BaseMainTabFragment implements UnreadAdapter.OnItemClickListener{
    @BindView(R.id.rv_unread)
    RecyclerView mRecyclerView;
    private UnreadAdapter mAdapter;

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_unread;
    }

    @Override
    protected void initView(View view) {
        mAdapter = new UnreadAdapter(getActivity(), SpeechList.getInstance().getArticleList(),this);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {

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
}
