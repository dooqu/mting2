package cn.xylink.mting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {


    private OnScrollListener onScrollListener;

    private boolean manualScroll;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        boolean manualScrollCurrent = this.manualScroll;
        Log.d("scrollview", "onscrollChaned:" + manualScrollCurrent + ":" + t);
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null && manualScrollCurrent == false) {
            onScrollListener.onScroll(t);
        }
    }

    /**
     * 接口对外公开
     *
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * 滚动的回调接口
     *
     * @author xiaanming
     */
    public interface OnScrollListener {
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         *
         * @param scrollY 、
         */
        void onScroll(int scrollY);
    }

    @Override
    public void setScrollY(int value) {
        super.setScrollY(value);
    }

    public boolean isScrollingOrInDelay()
    {
        return manualScroll;
    }

    public void beginUpdateScroll() {
        this.manualScroll = true;
        Log.d("scrollview", "set menuscroll = true");
    }

    public void endUpdateScroll() {
        this.post(new Runnable() {
            @Override
            public void run() {
                Log.d("scrollview", "set menusroll = false");
                MyScrollView.this.manualScroll = false;
            }
        });
    }
}
