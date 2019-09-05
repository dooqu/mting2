package cn.xylink.mting.speech.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.ArticleRecordRequest;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.ArticleInfoRequest;
import cn.xylink.mting.model.ArticleInfoResponse;
import cn.xylink.mting.model.FavoriteArticleRequest;
import cn.xylink.mting.model.UpdateSpeechSettingRequest;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.ReadArticleRequest;
import cn.xylink.mting.speech.SoundEffector;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.GsonUtil;


/*
文章正文内容的网络加载类
 */
public class ArticleDataProvider {

    static String TAG = ArticleDataProvider.class.getSimpleName();
    static String serverURL = "http://test.xylink.cn";
    /*
    文章正文加载的回调，采用函数接口形式
    invoke(errorCode, Article article)
    errorCode: 0时无错误
    article:是返回的正文已经加载完成的Article
     */
    @FunctionalInterface
    public static interface ArticleLoaderCallback {
        void invoke(int errorCode, Article article);
    }

    SoundEffector soundEffector;
    Article currentArticle;
    long tickcount = 0;
    Handler handler;

    public ArticleDataProvider(Context context) {
        soundEffector = new SoundEffector(context);
        currentArticle = null;
        handler = new Handler(Looper.getMainLooper());
    }

    /*
    article:需要更新的 文章对象，其中正文字段是需要更新的
    needSoundPlay:在加载前播放新文章的启动音效
    callback;文章加载成功后的回调，回调在主线程
     */
    public void updateArticle(Article article, boolean needSoundPlay, ArticleLoaderCallback callback) {
        final long tickCountAtTime = ++tickcount;
        currentArticle = article;
        if (needSoundPlay) {
            soundEffector.playSwitch(null);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //模拟网络加载
                    Thread.sleep(1500);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tickCountAtTime == tickcount && callback != null) {
                            callback.invoke(0, article);
                        }
                        else {
                            Log.d("xylink", "取消");
                        }
                    }
                });
            }
        }).start();
    }


    public void release() {
        this.soundEffector.release();
        this.soundEffector = null;
    }

    public void loadArticle(String articleId, ArticleLoaderCallback callback) {
        ArticleInfoRequest request = new ArticleInfoRequest();
        request.setArticleId(articleId);
        request.setToken(ContentManager.getInstance().getLoginToken());
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                },
                serverURL + "/api/sct/v2/article/detail",
                GsonUtil.GsonString(request), ArticleInfoResponse.class,
                new OkGoUtils.ICallback<ArticleInfoResponse>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        if (callback != null) {
                            //article.setContent(errorMsg);
                            callback.invoke(code, null);
                        }
                    }

                    @Override
                    public void onSuccess(ArticleInfoResponse response) {
                        if (callback != null) {
                            Article article = new Article();
                            article.setArticleId(response.data.getArticleId());
                            article.setContent(response.data.getContent());
                            article.setStore(response.data.getStore());
                            article.setInType(response.data.getInType());
                            article.setUrl(response.data.getUrl());
                            article.setTitle(response.data.getTitle());
                            article.setShareUrl(response.data.getShareUrl());
                            article.setSourceName(response.data.getSourceName());
                            article.setId(response.data.getId());
                            article.setSourceLogo(response.data.getSourceLogo());
                            article.setRead(response.data.getRead());
                            article.setProgress(response.data.getProgress());
                            callback.invoke(0, article);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("xylink", "onComplete");
                    }
                });
    }


    public void loadArticleContent(Article article, boolean needSoundPlay, ArticleLoaderCallback callback) {
        final long tickCountAtTime = ++tickcount;
        currentArticle = article;
        if (needSoundPlay) {
            soundEffector.playSwitch(null);
        }

        ArticleInfoRequest request = new ArticleInfoRequest();
        request.setArticleId(article.getArticleId());
        request.setToken(ContentManager.getInstance().getLoginToken());
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                },
                serverURL + "/api/sct/v2/article/detail",
                GsonUtil.GsonString(request), ArticleInfoResponse.class,
                new OkGoUtils.ICallback<ArticleInfoResponse>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        if (callback != null && tickCountAtTime == tickcount) {
                            article.setContent(errorMsg);
                            callback.invoke(code, article);
                        }
                    }

                    @Override
                    public void onSuccess(ArticleInfoResponse response) {
                        if(response.getCode() == 200) {
                            if (callback != null && tickCountAtTime == tickcount) {
                                article.setContent(response.data.getContent());
                                article.setStore(response.data.getStore());
                                article.setInType(response.data.getInType());
                                article.setUrl(response.data.getUrl());
                                article.setTitle(response.data.getTitle());
                                article.setShareUrl(response.data.getShareUrl());
                                article.setSourceName(response.data.getSourceName());
                                article.setId(response.data.getId());
                                article.setSourceLogo(response.data.getSourceLogo());
                                article.setRead(response.data.getRead());
                                article.setProgress(response.data.getProgress());
                                callback.invoke(0, article);
                            }
                        }
                        else {
                            onFailure(-500, "服务端响应错误");
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("xylink", "onComplete");
                    }
                });
    }


    public void readArticle(Article article, float progress, boolean deleteFromReadList, ArticleLoaderCallback callback) {
        ReadArticleRequest request = new ReadArticleRequest();
        request.setArticleId(article.getArticleId());
        request.setProgress(progress);
        request.setRead(deleteFromReadList ? 1 : 0);
        request.setToken(ContentManager.getInstance().getLoginToken());
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                }
                , serverURL + "/api/sct/v2/article/read",
                GsonUtil.GsonString(request),
                BaseResponse.class,
                new OkGoUtils.ICallback<BaseResponse<Object>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(BaseResponse data) {
                        Log.d("xylink", data.toString());
                        callback.invoke(0, article);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        Log.d("xylink", "ReadArticle.onFailure: code=" + code + ",msg=" + errorMsg);
                        if (callback != null) {
                            callback.invoke(-200, article);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void favorite(Article article, boolean isStore, ArticleLoaderCallback callback) {
        FavoriteArticleRequest request = new FavoriteArticleRequest(article.getArticleId(), isStore);
        request.setToken(ContentManager.getInstance().getLoginToken());
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                }
                , serverURL + "/api/sct/v2/article/store",
                GsonUtil.GsonString(request),
                BaseResponse.class,
                new OkGoUtils.ICallback<BaseResponse<Object>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(BaseResponse data) {
                        if (callback != null) {
                            article.setStore(isStore ? 1 : 0);
                            callback.invoke(0, article);
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        if (callback != null) {
                            callback.invoke(-200, article);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void appendArticleRecord(String articleId, long duration) {
        ArticleRecordRequest request = new ArticleRecordRequest();
        List<ArticleRecordRequest.ArticleRecord> readData = new ArrayList<>();
        ArticleRecordRequest.ArticleRecord record = new ArticleRecordRequest.ArticleRecord();
        record.setArticleId(articleId);
        record.setDate(new java.util.Date());
        record.setTime(duration);
        readData.add(record);
        request.setToken(ContentManager.getInstance().getLoginToken());
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                }
                , serverURL + "/api/analyse/v1/article/reader_time",
                GsonUtil.GsonString(request),
                BaseResponse.class,
                new OkGoUtils.ICallback<BaseResponse<Object>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(BaseResponse data) {
                        Log.d("xy", data.toString());
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
//                        Log.d("xy", errorMsg);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void updateSpeechSetting(Speechor.SpeechorRole role, Speechor.SpeechorSpeed speed, Integer fontType) {
        UpdateSpeechSettingRequest request = new UpdateSpeechSettingRequest();
        int sound = 1;
        switch (role) {
            case XiaoIce:
                sound = 1;
                break;

            case XiaoMei:
                sound = 2;
                break;

            case XiaoYu:
                sound = 4;
                break;

            case XiaoYao:
                sound = 3;
                break;
        }

        request.setSound(sound);

        float speedFloat = 1f;

        switch (speed) {
            case SPEECH_SPEED_HALF:
                speedFloat = 0.5f;
                break;
            case  SPEECH_SPEED_NORMAL:
                speedFloat = 1f;
                break;

            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                speedFloat = 1.5f;
                break;

            case SPEECH_SPEED_MULTIPLE_2:
                speedFloat = 2f;
                break;

            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                speedFloat = 2.5f;
                break;
        }

        request.setSpeed(speedFloat);
        request.setFont(fontType);
        request.setToken(ContentManager.getInstance().getLoginToken());
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                }
                , serverURL + "/api/user/v1/user_setting/update",
                GsonUtil.GsonString(request),
                BaseResponse.class,
                new OkGoUtils.ICallback<BaseResponse<Object>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(BaseResponse data) {
                        Log.d("xy", data.toString());
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        Log.d("xy", errorMsg);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

}
