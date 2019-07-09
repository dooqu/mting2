package cn.xylink.mting.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.WindowManager;

public class DensityUtil {

    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getKeyBordHeight(Activity context) {
        Rect rect = new Rect();
        // 获取当前页面窗口的显示范围
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int screenHeight = getScreenHeight(context);
        int keyboardHeight = screenHeight - rect.bottom; // 输入法的高度
        return keyboardHeight;
    }

    public static int getScreenHeight(Activity context) {
        int mScreenHeight = 0;
        if (mScreenHeight > 0) {
            return mScreenHeight;
        }
        mScreenHeight = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
        return mScreenHeight;
    }

    //官方内部转换  dip转变为px尺寸
    public static int dip2pxComm(Context context, float dipValue) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
        return size;
    }

    //官方内部转换 sp转变为px尺寸
    public static int sp2pxComm(Context context, float spValue) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
        return size;
    }

    //2015年11月13日15:20:37
    public static int dip2sp(Context context, float dipValue) {
        int pxValue = dip2px(context, dipValue);
        return px2sp(context, pxValue);
    }

    public static int sp2dip(Context context, float spValue) {
        int pxValue = sp2px(context, spValue);
        return px2dip(context, pxValue);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变  【代码里最终设置的单位基本都是px】
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变  【代码里最终设置的单位基本都是px，所以最常用使用这个】
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
