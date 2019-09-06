package cn.xylink.mting.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.bean.ArticleDetailRequest;
import cn.xylink.mting.bean.LinkArticle;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.NotifyMainPlayEvent;
import cn.xylink.mting.model.LinkCreateRequest;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.ui.fragment.UnreadFragment;
import cn.xylink.mting.utils.L;

/*
 *加入待读服务
 *
 * -----------------------------------------------------------------
 * 2019/9/5 17:48 : Create AddUnreadService.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class AddUnreadService extends IntentService {
    public static String EXTRA_URL = "extra_url";
    public static String EXTRA_ISPLAY = "extra_isplay";
    private boolean isPlay;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AddUnreadService() {
        super("AddUnreadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String url = intent.getStringExtra(EXTRA_URL);
            isPlay = intent.getBooleanExtra(EXTRA_ISPLAY, false);
            L.v(url);
            if (!TextUtils.isEmpty(url)) {
                addUnread(url);
            }
        }
    }

    private void addUnread(String str) {
        LinkCreateRequest request = new LinkCreateRequest();
        request.setUrl(str);
        request.setInType(1);
        request.doSign();
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        String json = gs.toJson(request);
        L.v("json", json);
        OkGoUtils.getInstance().postData(new IBaseView() {
            @Override
            public void showLoading() {

            }

            @Override
            public void hideLoading() {

            }
        }, RemoteUrl.linkCreateUrl(), json, new TypeToken<BaseResponse<LinkArticle>>() {

        }.getType(), new OkGoUtils.ICallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object data) {
                BaseResponse<LinkArticle> baseResponse = (BaseResponse<LinkArticle>) data;
                int code = baseResponse.code;
                if (code == 200) {
                    addLocalUread(baseResponse.data.getArticleId());
                } else {
                    Toast.makeText(AddUnreadService.this, "文章加载失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Toast.makeText(AddUnreadService.this, "文章加载失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void addLocalUread(String id) {
        if (!TextUtils.isEmpty(id)) {
            ArticleDetailRequest request = new ArticleDetailRequest();
            request.setArticleId(id);
            request.doSign();
            OkGoUtils.getInstance().postData(new IBaseView() {
                @Override
                public void showLoading() {

                }

                @Override
                public void hideLoading() {

                }
            }, RemoteUrl.getArticDetailUrl(), new Gson().toJson(request), new TypeToken<BaseResponse<ArticleDetailInfo>>() {
            }.getType(), new OkGoUtils.ICallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(Object data) {
                    BaseResponse<ArticleDetailInfo> baseResponse = (BaseResponse<ArticleDetailInfo>) data;
                    int code = baseResponse.code;
                    if (code == 200 && UnreadFragment.ISINIT) {
                        ArticleDetailInfo info = baseResponse.data;
                        Article article = new Article();
                        article.setProgress(0);
                        article.setTitle(info.getTitle());
                        article.setArticleId(info.getArticleId());
                        article.setSourceName(info.getSourceName());
                        article.setShareUrl(info.getShareUrl());
                        article.setStore(info.getStore());
                        article.setRead(info.getRead());
                        article.setUpdateAt(info.getUpdateAt());
                        List<Article> list = new ArrayList<>();
                        list.add(article);
                        SpeechList.getInstance().pushFront(list);
                        EventBus.getDefault().post(new AddUnreadEvent());
                        if (isPlay) {
                            EventBus.getDefault().post(new NotifyMainPlayEvent(id));
                            Toast.makeText(AddUnreadService.this, "开始朗读文章", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddUnreadService.this, "已添加到待读", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (code != 200) {
                        Toast.makeText(AddUnreadService.this, "文章加载失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    Toast.makeText(AddUnreadService.this, "文章加载失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }
}
