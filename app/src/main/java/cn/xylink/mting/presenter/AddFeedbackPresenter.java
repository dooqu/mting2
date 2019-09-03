package cn.xylink.mting.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.AddFeedbackContact;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.GsonUtil;
import cn.xylink.mting.utils.L;
import okhttp3.OkHttpClient;

public class AddFeedbackPresenter extends BasePresenter<AddFeedbackContact.IAddFeedBackView> implements AddFeedbackContact.Presenter {
    @Override
    public void onFeedBack(LinkCreateRequest request) {
        L.v("request", request);
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        String json = gs.toJson(request);
        L.v("json", json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.feedbackUrl(), json, new TypeToken<BaseResponse<String>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
                mView.showLoading();
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    mView.onAddFeedBackSuccess(baseResponse);
                } else {
                    mView.onBindCheckError(code, baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onBindCheckError(code, errorMsg);
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
            }
        });
    }

    public void onFeedBackForm(List<File> files, HttpParams param) {
        param.put("timestamp", System.currentTimeMillis());
        param.put("token", ContentManager.getInstance().getLoginToken());
        String url = RemoteUrl.feedbackUrlv2();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的读取超时时间  基于前面的通道建立完成后，客户端终于可以向服务端发送数据了
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间  服务器发回消息，可是客户端出问题接受不到了
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间  http建立通道的时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        OkGo.getInstance().setOkHttpClient(builder.build());
        OkGo.<String>post(url)
                .tag(this)
                .isMultipart(true)
                .params(param)
                .addFileParams("files", files)
                .execute(new Callback<String>() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        mView.showLoading();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        BaseResponse<String> baseResponse = GsonUtil.GsonToBean(body, BaseResponse.class);
                        int code = baseResponse.code;
                        if (code == 200) {
                            mView.onAddFeedBackSuccess(baseResponse);
                        } else {
                            mView.onBindCheckError(code, baseResponse.message);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {

                    }

                    @Override
                    public void onError(Response<String> response) {
                        mView.hideLoading();
                    }

                    @Override
                    public void onFinish() {
                        mView.hideLoading();
                    }

                    @Override
                    public void uploadProgress(Progress progress) {

                    }

                    @Override
                    public void downloadProgress(Progress progress) {

                    }

                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        String string = response.body().string();
                        return string;
                    }
                });
    }
}
