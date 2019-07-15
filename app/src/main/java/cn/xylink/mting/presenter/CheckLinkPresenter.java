package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.contract.LinkCreateContact;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.L;

public class CheckLinkPresenter extends BasePresenter<CheckLinkContact.ICheckLinkView> implements CheckLinkContact.Presenter {
    @Override
    public void onCheckLink(CheckLinkUrlRequset request) {
        L.v("request",request);
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        String json = gs.toJson(request);
        L.v("json",json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.checkLinkUrl(),json , new TypeToken<BaseResponse<LinkArticle>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<LinkArticle> baseResponse = (BaseResponse<LinkArticle>) data;
                int code = baseResponse.code;
                if(code == 200) {
                    mView.onCheckLinkSuccess(baseResponse);
                }else
                {
                    mView.onCheckLinkError(code,baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onCheckLinkError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
