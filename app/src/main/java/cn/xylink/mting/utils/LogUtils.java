package cn.xylink.mting.utils;

import android.util.Log;


public class LogUtils {

    public static boolean isLog = true;

    public static void i(String tag, String msg) {
        if (isLog && msg != null) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isLog && msg != null) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isLog && msg != null) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isLog && msg != null) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isLog && msg != null) {
            Log.e("Mting", msg);
        }
    }
}
