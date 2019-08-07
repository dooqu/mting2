package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.CheckLinkContact;
import cn.xylink.mting.contract.ShareAddContract;
import cn.xylink.mting.model.ArticleInfoRequest;
import cn.xylink.mting.model.CheckLinkUrlRequset;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.L;

public class ShareAddPresenter extends BasePresenter<ShareAddContract.IShareAddView> implements ShareAddContract.Presenter {
    @Override
    public void shareAdd(ArticleInfoRequest request) {
        L.v("request",request);
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        String json = gs.toJson(request);
        L.v("json",json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.shareAddUrl(),json , new TypeToken<BaseResponse<ArticleDetailInfo>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {
//                mView.showLoading();
            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<ArticleDetailInfo> baseResponse = (BaseResponse<ArticleDetailInfo>) data;
                int code = baseResponse.code;
                if(code == 200) {
                    mView.onShareAddSuccess(baseResponse);
                }else
                {
                    mView.onShareAddError(code,baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onShareAddError(code, errorMsg);
            }

            @Override
            public void onComplete() {
//                mView.hideLoading();
            }
        });
    }
}
