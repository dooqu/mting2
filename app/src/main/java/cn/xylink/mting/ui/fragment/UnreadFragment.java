package cn.xylink.mting.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xylink.mting.R;
import cn.xylink.mting.model.Article;
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
public class UnreadFragment extends Fragment implements UnreadAdapter.OnItemClickListener{
    @BindView(R.id.rv_unread)
    RecyclerView mRecyclerView;
    private UnreadAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unread, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new UnreadAdapter(getActivity(), SpeechList.getInstance().getArticleList(),this);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(Article article) {
        L.v();
    }
    @Override
    public void onItemMoreClick(Article article) {
        L.v();
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
