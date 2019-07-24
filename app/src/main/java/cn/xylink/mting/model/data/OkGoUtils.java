package cn.xylink.mting.model.data;

import com.lzy.okgo.OkGo;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import cn.xylink.mting.contract.IBaseView;

public class OkGoUtils<T> {

    private OkGoUtils() {
    }

    public static OkGoUtils getInstance() {
        return OkGoUtilHolder.singleTonInstance;
    }


    public void postData(final IBaseView view, String url, String postData, Type type, final ICallback<T> callback) {
        OkGo.<T>post(url)
                .upJson(postData)
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
                                     if (errorCode==-1)
                                        cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
                                     else if(errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");

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
                                     if (errorCode==-1)
                                        cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
                                     else if(errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
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
                                   if (errorCode==-1)
                                        cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
                                     else if(errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
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
                                     if (errorCode==-1)
                                         cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
                                     else if(errorCode == 9999)
                                         cn.xylink.mting.utils.T.showCustomToast("网络连接失败！");
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