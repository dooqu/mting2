package cn.xylink.mting.model.data;

import android.os.Build;

import com.lzy.okgo.OkGo;

import org.bouncycastle.util.Pack;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import cn.xylink.mting.MTing;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.utils.PackageUtils;

public class OkGoUtils<T> {

    private OkGoUtils() {
    }

    public static OkGoUtils getInstance() {
        return OkGoUtilHolder.singleTonInstance;
    }


    public void postData(final IBaseView view, String url, String postData, Type type, final ICallback<T> callback) {
        OkGo.<T>post(url)
                .upJson(postData)
                .headers("timestamp", String.valueOf(System.currentTimeMillis()))
                .headers("version", PackageUtils.getAppVersionCode(MTing.getInstance()))
                .headers("versionName", PackageUtils.getAppVersionName(MTing.getInstance()))
                .headers("deviceId", PackageUtils.getWifiMac(MTing.getInstance()))
                .headers("deviceName", Build.MODEL)
                .headers("sysVersion", "Android " + Build.VERSION.RELEASE)
                .tag(view)
                .execute(new JsonBeanCallback<T>(type) {
                             @Override
                             protected void onStart() {
                                 super.onStart();
                                 if (view != null) {
                                     callback.onStart();
                                 }
                             }

                             @Override
                             protected void onSuccess(T data) {
                                 if (view != null) {
                                     callback.onSuccess(data);
                                 }
                             }

                             @Override
                             protected void onFailure(int errorCode, String errorMsg) {
                                 if (view != null) {
                                     callback.onFailure(errorCode + 10000, errorMsg);
                                     callback.onComplete();

                                     if (errorCode == -1)
                                         cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                     else if (errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                 }
                             }

                             @Override
                             protected void onComplete() {
                                 super.onComplete();
                                 if (view != null) {
                                     callback.onComplete();
                                 }
                             }
                         }
                );

    }

    public void postParamsData(String url, Map<String, String> postData, Type type, final ICallback<T> callback) {
        OkGo.<T>post(url)
                .headers("timestamp", String.valueOf(System.currentTimeMillis()))
                .headers("version", PackageUtils.getAppVersionCode(MTing.getInstance()))
                .headers("versionName", PackageUtils.getAppVersionName(MTing.getInstance()))
                .headers("deviceId", PackageUtils.getWifiMac(MTing.getInstance()))
                .headers("deviceName", Build.MODEL)
                .headers("sysVersion", "Android " + Build.VERSION.RELEASE)
                .params(postData)
                .execute(new JsonBeanCallback<T>(type) {

                             @Override
                             protected void onStart() {
                                 super.onStart();
                                 callback.onStart();
                             }

                             @Override
                             protected void onSuccess(T data) {
                                 callback.onSuccess(data);
                             }

                             @Override
                             protected void onFailure(int errorCode, String errorMsg) {
                                 callback.onFailure(errorCode + 10000, errorMsg);
                                 callback.onComplete();
                                 if (errorCode == -1)
                                     cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                 else if (errorCode == 9999)
                                     cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                             }

                             @Override
                             protected void onComplete() {
                                 super.onComplete();
                                 callback.onComplete();
                             }
                         }
                );

    }

    public void postData(final IBaseView view, String url, Map<String, String> data, File file, Type type, final ICallback<T> callback) {
        OkGo.<T>post(url)
                .tag(view)
                .headers("timestamp", String.valueOf(System.currentTimeMillis()))
                .headers("version", PackageUtils.getAppVersionCode(MTing.getInstance()))
                .headers("versionName", PackageUtils.getAppVersionName(MTing.getInstance()))
                .headers("deviceId", PackageUtils.getWifiMac(MTing.getInstance()))
                .headers("deviceName", Build.MODEL)
                .headers("sysVersion", "Android " + Build.VERSION.RELEASE)
                .params(data, true)
                .params("file", file)
                .execute(new JsonBeanCallback<T>(type) {

                             @Override
                             protected void onStart() {
                                 super.onStart();
                                 if (view != null) {
                                     callback.onStart();
                                 }
                             }

                             @Override
                             protected void onSuccess(T data) {
                                 if (view != null) {
                                     callback.onSuccess(data);
                                 }
                             }

                             @Override
                             protected void onFailure(int errorCode, String errorMsg) {
                                 if (view != null) {
                                     callback.onFailure(errorCode + 10000, errorMsg);
                                     callback.onComplete();

                                     if (errorCode == -1)
                                         cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                     else if (errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                 }
                             }

                             @Override
                             protected void onComplete() {
                                 super.onComplete();
                                 if (view != null) {
                                     callback.onComplete();
                                 }
                             }
                         }
                );
    }

    public void getData(final IBaseView view, String url, Type type, final ICallback<T> callback) {
        OkGo.<T>get(url)
                .headers("timestamp", String.valueOf(System.currentTimeMillis()))
                .headers("version", PackageUtils.getAppVersionCode(MTing.getInstance()))
                .headers("versionName", PackageUtils.getAppVersionName(MTing.getInstance()))
                .headers("deviceId", PackageUtils.getWifiMac(MTing.getInstance()))
                .headers("deviceName", Build.MODEL)
                .headers("sysVersion", "Android " + Build.VERSION.RELEASE)
                .tag(view)
                .execute(new JsonBeanCallback<T>(type) {

                             @Override
                             protected void onStart() {
                                 super.onStart();
                                 if (view != null) {
                                     callback.onStart();
                                 }
                             }

                             @Override
                             protected void onSuccess(T data) {
                                 if (view != null) {
                                     callback.onSuccess(data);
                                 }
                             }

                             @Override
                             protected void onFailure(int errorCode, String errorMsg) {
                                 if (view != null) {
                                     callback.onFailure(errorCode + 10000, errorMsg);
                                     callback.onComplete();

                                     if (errorCode == -1)
                                         cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                     else if (errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast(HttpConst.NO_NETWORK);
                                 }
                             }

                             @Override
                             protected void onComplete() {
                                 super.onComplete();
                                 if (view != null) {
                                     callback.onComplete();
                                 }
                             }
                         }
                );

    }

    public void cancel(IBaseView view) {
        OkGo.getInstance().cancelTag(view);
    }

    public interface ICallback<T> {

        void onStart();

        void onSuccess(T data);

        void onFailure(int code, String errorMsg);

        void onComplete();
    }

    public static class OkGoUtilHolder {
        private static final OkGoUtils singleTonInstance = new OkGoUtils();
    }

}