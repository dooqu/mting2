package cn.xylink.mting.utils;

import android.text.TextUtils;
import android.util.Log;

public class L {

    public static final String TAG = "LOG_INFO";
    public static final boolean DEBUG = true;
    private static final String TAG_CONTENT_PRINT = "[%s][%s] [%s]";

    private static String getContent(String tag, String method, Object msg) {
        final String formatContent = String.format(TAG_CONTENT_PRINT, TAG, tag, method, msg);
        return formatContent;
    }

    public static void v(String tag, String method, Object msg) {
        if (DEBUG) {
            Log.v(TAG, getContent(tag, method, msg));
        }
    }

    public static void recordInfo(Exception e)
    {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Exception:").append(e.getClass()).append("\t").append(e.getMessage()).append("\t");
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                stringBuilder.append(i).append(":").append(e.getStackTrace()[i].toString()).append("\n");
            }
            Log.e(TAG, getContent() + ">>" + stringBuilder.toString());
        }
    }

    public static void v(Object... msg) {

        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : msg) {
                sb.append(obj + " ");
            }
            Log.e(TAG, getContent() + ">>" + sb.toString());
        }
    }
    public static void e(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.e(TAG, getContent() + ">>" + msg, throwable);
        }
    }

    public static void e(String tag, String method, String msg) {
        if (DEBUG) {
            Log.e(TAG, method + ">>" + msg);
        }
    }

    public static void e(Object... msg) {
        v(msg);
    }

    public static void e(Exception e) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Exception:").append(e.getClass()).append("\t").append(e.getMessage()).append("\t");
            for (int i = 0; i < e.getStackTrace().length; ++i) {
                stringBuilder.append(i).append(":").append(e.getStackTrace()[i].toString()).append("\n");
            }
            Log.e(TAG, stringBuilder.toString());
        }
    }


    public static void e(String log, Exception e) {
        if (DEBUG) {
            if (!TextUtils.isEmpty(log))
                Log.e(TAG, log);
            e.printStackTrace();
        }
    }


    private static String getContent() {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        if (stes == null) {
            return null;
        }
        for (StackTraceElement s : stes) {
            if (s.isNativeMethod()) {
                continue;
            }
            if (s.getClassName().equals(Thread.class.getName()))
                continue;
            if (s.getClassName().equals(L.class.getName()))
                continue;
            final String str = String.format(TAG_CONTENT_PRINT, s.getClassName(), s.getMethodName(), s.getLineNumber());
            return str;
        }
        return null;
    }
}
