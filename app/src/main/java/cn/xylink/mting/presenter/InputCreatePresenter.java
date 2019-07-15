package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.contract.BindCheckContact;
import cn.xylink.mting.contract.InputCreateContact;
import cn.xylink.mting.model.BindCheckRequest;
import cn.xylink.mting.model.InputCreateRequest;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.FileUtil;
import cn.xylink.mting.utils.L;

public class InputCreatePresenter extends BasePresenter<InputCreateContact.ICreateView> implements InputCreateContact.Presenter {
    @Override
    public void onCreateNote(InputCreateRequest request) {
        L.v("request",request);
        String json = new Gson().toJson(request);
        L.v("json",json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.inputCreateUrl(),json , new TypeToken<BaseResponse<Article>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<Article> baseResponse = (BaseResponse<Article>) data;
                int code = baseResponse.code;
                if(code == 200) {
                    mView.onCreateSuccess(baseResponse);
                }else
                {
                    mView.onCreateError(code,baseResponse.message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onCreateError(code, errorMsg);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
