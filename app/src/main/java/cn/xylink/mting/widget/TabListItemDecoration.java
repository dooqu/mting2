package cn.xylink.mting.widget;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.xylink.mting.MTing;
import cn.xylink.mting.utils.DensityUtil;
import cn.xylink.mting.utils.L;

/*
 *主页tabList通用decoration
 *
 * -----------------------------------------------------------------
 * 2019/9/3 14:11 : Create TabListItemDecoration.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class TabListItemDecoration extends RecyclerView.ItemDecoration {

    private int dipValue = 32;

    public TabListItemDecoration() {
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int space = DensityUtil.dip2pxComm(MTing.getInstance().getApplicationContext(), dipValue);
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.top = space;
        }
    }
}