package cn.xylink.mting.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
//        switch (arg0.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                isCanScroll = true;
//                break;
//            default:
                if (isCanScroll) {
                    return super.onTouchEvent(arg0);
                } else {
                    return false;
                }
//        }
//      return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanScroll) {
            try {
                return super.onInterceptTouchEvent(arg0);
            } catch (IllegalArgumentException e) {}
        } else {
            return false;
        }
        return false;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {

        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {

        super.setCurrentItem(item, false);
    }

}
