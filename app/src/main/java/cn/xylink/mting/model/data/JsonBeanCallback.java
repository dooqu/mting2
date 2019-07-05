package cn.xylink.mting.model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.Type;

import cn.xylink.mting.annotation.FooAnnotationExclusionStrategy;
import okhttp3.ResponseBody;

public class JsonBeanCallback<T> extends AbsCallback<T> {
    private Type type;

    public JsonBeanCallback(Type type) {
        this.type = type;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        onStart();
    }

    @Override
    public void onSuccess(Response<T> response) {
        T data = response.body();
        onSuccess(data);
    }

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null)
            return null;
        T data = null;
        Gson gson = new GsonBuilder().setExclusionStrategies(new FooAnnotationExclusionStrategy()).create();
        JsonReader jsonReader = new JsonReader(body.charStream());
        if (type != null)
            data = gson.fromJson(jsonReader, type);
        return data;
    }

    @Override
    public void onError(Response<T> response) {
        super.onError(response);
        onFailure(response.code(), response.message());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        onComplete();
    }

    protected void onStart() {
    }

    protected void onSuccess(T data) {
    }

    protected void onFailure(int errorCode, String errorMsg) {
    }

    protected void onComplete() {
    }

}
