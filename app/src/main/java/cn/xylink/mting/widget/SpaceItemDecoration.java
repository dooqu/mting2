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

    private int dipValue = 32;
    private boolean isBottom = false;

    public SpaceItemDecoration() {
    }

    public SpaceItemDecoration(int dipValue ,boolean isBottom) {
        this.dipValue = dipValue;
        this.isBottom = isBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int space = DensityUtil.dip2pxComm(MTing.getInstance().getApplicationContext(), dipValue);
        if (isBottom) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = space;
            outRect.top = 0;
        } else {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.top = space;
        }
    }
}