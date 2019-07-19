package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.AddFeedbackContact;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.L;

public class AddFeedbackPresenter extends BasePresenter<AddFeedbackContact.IAddFeedBackView> implements AddFeedbackContact.Presenter {
    @Override
    public void onFeedBack(LinkCreateRequest request) {
        L.v("request",request);
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        String json = gs.toJson(request);
        L.v("json",json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.feedbackUrl(),json , new TypeToken<BaseResponse<String>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
                mView.showLoading();
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<String> baseResponse = (BaseResponse<String>) data;
                int code = baseResponse.code;
                if(code == 200) {
                    mView.onAddFeedBackSuccess(baseResponse);
                }else
                {
                    mView.onBindCheckError(code,baseResponse.message);
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
}
