package cn.xylink.mting.ui.fragment;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.ui.activity.BasePresenterActivity;
import cn.xylink.mting.ui.dialog.MainListMenuDialog;
import cn.xylink.mting.ui.dialog.TipDialog;


/*
 *主页tab页基类
 *
 * -----------------------------------------------------------------
 * 2019/7/9 14:50 : Create BaseMainTabFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public abstract class BaseMainTabFragment extends BasePresenterFragment implements MainListMenuDialog.OnBottomSelectDialogListener {
    protected OnControllerListener mControllerListener;
    protected MainListMenuDialog mBottomDialog;
    RecyclerView mRecyclerView;

    public enum TAB_TYPE {
        UNREAD,
        READED,
        COLLECT,
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mControllerListener = (OnControllerListener) context;
    }

    public interface OnControllerListener {
        void onPlay(String id);

        void onDataSuccess();

        void onLove(Article article);

        void onDel(TAB_TYPE tabType, String id);
    }

    protected void showBottonDialog(TAB_TYPE tabType, Article article) {
        mBottomDialog = new MainListMenuDialog(getActivity());
        mBottomDialog.setListener(this);
        mBottomDialog.show(tabType, article);
    }

    @Override
    public void onItemDel(TAB_TYPE tabType, String id) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//        dialog.setMessage("确定删除这篇文章吗？").setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
//                .setPositiveButton("确定", (dialog12, which) -> {
//                    if (mControllerListener != null)
//                        mControllerListener.onDel(tabType, id);
//                    dialog12.dismiss();
//                }).create();
//        dialog.show();

        TipDialog alertDialog = new TipDialog(getActivity());
        alertDialog.setMsg("确定删除这篇文章吗？", "取消", "确定", new TipDialog.OnTipListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                if (mControllerListener != null)
                    mControllerListener.onDel(tabType, id);
            }
        });
        alertDialog.show();
    }

    @Override
    public void onItemLove(Article article) {
        if (mControllerListener != null)
            mControllerListener.onLove(article);
    }

    public void backTop() {
        if (mRecyclerView != null && mRecyclerView.getChildAt(0) != null) {
            int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0)) + 1;
            int heiht = mRecyclerView.getChildAt(0).getHeight();
            int sy = -firstItem * heiht * 2;
            mRecyclerView.smoothScrollBy(0, sy, new DecelerateInterpolator(1.8f));
        }
    }
}
