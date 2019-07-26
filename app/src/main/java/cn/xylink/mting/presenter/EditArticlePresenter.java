package cn.xylink.mting.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.contract.EditArticleContact;
import cn.xylink.mting.model.EditArticleRequest;
import cn.xylink.mting.model.InputCreateRequest;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.utils.GsonUtil;
import cn.xylink.mting.utils.LogUtils;

public class EditArticlePresenter extends BasePresenter<EditArticleContact.ICreateView> implements EditArticleContact.Presenter {

    @Override
    public void onEditNote(String id, String title, String content) {
        EditArticleRequest request = new EditArticleRequest();
        request.setArticleId(id);
        request.setTitle(title);
        request.setContent(content);
        request.doSign();
        String json = GsonUtil.GsonString(request);
        LogUtils.e(RemoteUrl.getEditArticle());
        LogUtils.e(json);
        OkGoUtils.getInstance().postData(mView, RemoteUrl.getEditArticle(), json, new TypeToken<BaseResponse>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse res= (BaseResponse) data;
                if (res.code==200){
                    mView.onSaveSuccess();
                }else{
                    mView.onSaveError();
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mView.onSaveError();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
