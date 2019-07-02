package cn.xylink.mting.http;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import cn.xylink.mting.model.HttpModel;
import cn.xylink.mting.model.IModel;
import cn.xylink.mting.utils.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class OkHttpUtils {
    private static OkHttpClient okHttpClient;

    public static void init() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
    }


    public static void post(String url, final HttpModel model, final JsonCallBack callBack) {
        if (callBack != null) {
            callBack.onPre();
        }
        String json = GsonUtil.GsonString(model.iModel);
        Log.d("okhttp-request:", json);
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), json))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                callBack.onError();
                callBack.onFinish();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Log.d("okhttp-response:", json);
                IModel iModel = GsonUtil.GsonToBean(json, model.clazz);
                callBack.onSuccess(iModel);
            }
        });
    }
}
