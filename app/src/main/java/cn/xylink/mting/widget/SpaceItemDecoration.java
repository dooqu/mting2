package cn.xylink.mting.widget;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.xylink.mting.MTing;
import cn.xylink.mting.utils.DensityUtil;

/*
 *decoration
 *
 * -----------------------------------------------------------------
 * 2019/7/10 14:02 : Create SpaceItemDecoration.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int space = DensityUtil.dip2pxComm(MTing.getInstance().getApplicationContext(), 32);
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