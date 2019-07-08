package cn.xylink.mting.speech.data;

import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import cn.xylink.mting.base.BaseRequest;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.ArticleInfoRequest;
import cn.xylink.mting.model.ArticleInfoResponse;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.ReadArticleRequest;
import cn.xylink.mting.speech.SoundEffector;
import cn.xylink.mting.utils.GsonUtil;


/*
文章正文内容的网络加载类
 */
public class ArticleDataProvider {

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


    public void updateArticleTest(Article article, ArticleLoaderCallback callback) {
        final long tickCountAtTime = ++tickcount;
        currentArticle = article;

        ArticleInfoRequest request = new ArticleInfoRequest();
        request.setArticleId(article.getArticleId());
        request.setToken("appe11069626a464dcab7eb2f69901f501b");
        request.doSign();

        OkGoUtils.getInstance().postData(new IBaseView() {
            @Override
            public void showLoading() {

            }

            @Override
            public void hideLoading() {

            }
        }, "http://test.xylink.cn/api/sct/v2/article/detail", GsonUtil.GsonString(request), ArticleInfoResponse.class, new OkGoUtils.ICallback<ArticleInfoResponse>() {

            @Override
            public void onStart() {
                Log.d("xylink", "onStart");
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.d("xylink", "onFailure");
                callback.invoke(-code, null);
            }

            @Override
            public void onSuccess(ArticleInfoResponse response) {
                if (callback != null) {
                    callback.invoke(0, response.data);
                }
            }

            @Override
            public void onComplete() {
                Log.d("xylink", "onComplete");
            }
        });
    }

    public void readArticle(String article, float progress) {
        ReadArticleRequest request = new ReadArticleRequest();
        request.setArticleId(article);
        request.setProgress(progress);
        request.setRead(progress == 1f ? 1 : 0);
        request.setToken("appe11069626a464dcab7eb2f69901f501b");
        request.doSign();

        OkGoUtils.getInstance().postData(new IBaseView() {
                                             @Override
                                             public void showLoading() {
                                             }

                                             @Override
                                             public void hideLoading() {
                                             }
                                         }
                , "http://test.xylink.cn//api/sct/v2/article/read", GsonUtil.GsonString(request), BaseRequest.class, new OkGoUtils.ICallback<BaseResponse>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(BaseResponse data) {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void saveSpeechListToLocal()
    {
    }

    public List<Article> getSpeechListFromLocal()
    {
        return null;
    }
}
