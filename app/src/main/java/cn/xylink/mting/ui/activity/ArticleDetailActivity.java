package cn.xylink.mting.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.ServiceState;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.AddLoveRequest;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.contract.DelMainContract;
import cn.xylink.mting.event.AddStoreSuccessEvent;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.presenter.DelMainPresenter;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.speech.data.ArticleDataProvider;
import cn.xylink.mting.speech.event.FavoriteEvent;
import cn.xylink.mting.speech.event.RecycleEvent;
import cn.xylink.mting.speech.event.SpeechEndEvent;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechPauseEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechReadyEvent;
import cn.xylink.mting.speech.event.SpeechResumeEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.dialog.ArticleDetailFont;
import cn.xylink.mting.ui.dialog.ArticleDetailSetting;
import cn.xylink.mting.ui.dialog.ArticleDetailShare;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.T;
import cn.xylink.mting.widget.ArcProgressBar;
import cn.xylink.mting.widget.MyScrollView;

import static cn.xylink.mting.speech.SpeechService.SpeechServiceState.Playing;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class ArticleDetailActivity extends BasePresenterActivity implements DelMainContract.IDelMainView {

    private int isPlaying = 0;
    private ArticleDetailSetting mArticleDetailSetting;
    private ArticleDetailFont mArticleDetailFont;
    private ArticleDetailShare mArticleDetailShare;
    private SpeechService service;
    private SpeechServiceProxy proxy;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.apb_main_play_progress)
    ArcProgressBar apbMain;
    @BindView(R.id.sk_progress)
    SeekBar skProgress;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_play_bar_btn)
    ImageView ivPlayBarBtn;
    @BindView(R.id.ll_article_edit)
    LinearLayout llArticleEdit;
    @BindView(R.id.ll_source_detail)
    LinearLayout llSourceDetail;
    @BindView(R.id.tv_ar_title)
    TextView tvArTitle;
    @BindView(R.id.tv_author)
    TextView tvAuthor;
    @BindView(R.id.tv_fav)
    TextView tvFav;
    @BindView(R.id.sv_content)
    MyScrollView svContent;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_fk)
    TextView tvFk;
    private String aid;
    private String articleUrl;
    private Article mCurrentArticle;
    private DelMainPresenter mPresenter;
    private float mTitleheight;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private int textReadedRuntimeHeight;
    private int textTotalRuntimeHeight;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail);
    }

    /*
    获取播放服务后，初始化播放状态， 因为页面进入后，可能onSpeechStart和onspeechReady未接收到，
    那么就要靠获取服务状态，进行状态的拉取
     */
    private void initServiceData() {
        getWindow().setStatusBarColor(Color.argb(0, 72, 141, 239));
        //
        mCurrentArticle = service.getSelected();
        Article prevArt = service.getSelected();
        Article currArt = null;

        synchronized (service) {
            //如果当前播放的不是选中的，或者当前没有选中的，那么开始播放选中的
            if (prevArt != null && prevArt.getArticleId().equals(aid) == false || prevArt == null) {
                service.play(aid);
            }
            //获取当前正在播放的ArticleInfo
            currArt = service.getSelected();
            llArticleEdit.setVisibility((currArt.getInType() == 1 || TextUtils.isEmpty(currArt.getUrl())) ? View.VISIBLE : View.GONE);
            llSourceDetail.setVisibility((currArt.getInType() == 1 || TextUtils.isEmpty(currArt.getUrl())) ? View.GONE : View.VISIBLE);

            //设定标题
            tvTitle.setText(currArt.getTitle());
            tvTitle.setVisibility(currArt.getTitle() != null ? View.VISIBLE : View.GONE);

            //设定作者来源
            tvAuthor.setText(currArt.getSourceName());
            tvAuthor.setVisibility(currArt.getSourceName() != null ? View.VISIBLE : View.GONE);
            tvFav.setText(currArt.getStore() == 1 ? "已收藏" : "收藏");

            articleUrl = currArt.getUrl();

            int frameIndex = service.getSpeechorFrameIndex();
            int framesTotal = service.getSpeechorTextFragments().size();
            List<String> textFragments = service.getSpeechorTextFragments();

            Handler handler = new Handler(Looper.getMainLooper());
            SpeechService.SpeechServiceState state = service.getState();
            switch (state) {
                case Playing:
                    //ivPlayBarBtn.setImageDrawable(mPauseDrawable);
                    showContent(textFragments, frameIndex);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setArticleProgress(frameIndex, framesTotal);
                        }
                    }, 100);
                    break;
                case Loadding:
                    //ivPlayBarBtn.setImageDrawable(mPauseDrawable);
                    break;
                case Paused:
                    //ivPlayBarBtn.setImageDrawable(mPlayDrawable);
                    showContent(textFragments, frameIndex);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setArticleProgress(frameIndex, framesTotal);
                        }
                    }, 100);
                    break;
            }
            mTitleheight = tvContent.getY();
            initPlayState();
        }


        /*
        if (service.getState() == Speechor.SpeechorState.SpeechorStatePlaying && aid.equals(mCurrentArticle.getArticleId())) {
            //判断是否是自己编写的文章
            if (mCurrentArticle != null && (mCurrentArticle.getInType() == 1 || TextUtils.isEmpty(mCurrentArticle.getUrl()))) {
                llArticleEdit.setVisibility(View.VISIBLE);
                llSourceDetail.setVisibility(View.GONE);
                mTitleheight = tvContent.getY();
                aid = mCurrentArticle.getId();
            }
            if (mCurrentArticle != null) {
                tvContent.setText(mCurrentArticle.getContent());
            }
            isPlaying = 1;
            if (ivPlayBarBtn.getDrawable() != mPauseDrawable) {
                ivPlayBarBtn.setImageDrawable(mPauseDrawable);
                ((Animatable) mPauseDrawable).start();
            }
        } else if (aid != null) {
            isPlaying = 1;
            service.play(aid);
        }
        if (mCurrentArticle != null) {
            articleUrl = mCurrentArticle.getUrl();
            tvContent.setText(mCurrentArticle.getContent());
            tvTitle.setText(mCurrentArticle.getTitle());
            if (TextUtils.isEmpty(mCurrentArticle.getTitle())) {
                tvArTitle.setVisibility(View.GONE);
            } else {
                tvArTitle.setVisibility(View.VISIBLE);
                tvArTitle.setText(mCurrentArticle.getTitle());
            }
            if (TextUtils.isEmpty(mCurrentArticle.getSourceName())) {
                tvAuthor.setVisibility(View.GONE);
            } else {
                tvAuthor.setVisibility(View.VISIBLE);
                tvAuthor.setText(mCurrentArticle.getSourceName());
            }
            if (mCurrentArticle.getInType() == 1 || TextUtils.isEmpty(mCurrentArticle.getUrl())) {
                llArticleEdit.setVisibility(View.VISIBLE);
                llSourceDetail.setVisibility(View.GONE);
            }
            if (mCurrentArticle.getStore() == 0) {
                tvFav.setText("收藏");
            } else {
                tvFav.setText("已收藏");
            }
        }
        mTitleheight = tvContent.getY();


        */
        svContent.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                float alpha = scrollY / mTitleheight;
                if (alpha > 1) {
                    alpha = 1;
                }
                if (alpha < 0) {
                    alpha = 0;
                }
                int a = (int) (255 * alpha);
                llTitle.setBackgroundColor(Color.argb(a, 72, 141, 239));
                getWindow().setStatusBarColor(Color.argb(a, 72, 141, 239));
                Drawable drawable = ivBack.getDrawable();
                int fk = (int) (153 + 102 * (alpha));
                drawable.setTint(Color.rgb(fk, fk, fk));
                ivBack.setImageDrawable(drawable);
                tvFk.setTextColor(Color.rgb(fk, fk, fk));
                tvTitle.setTextColor(Color.rgb(fk, fk, fk));
            }
        });
    }

    @Override
    protected void initView() {
        mPauseDrawable = this.getDrawable(R.drawable.nsvg_pause);
        mPlayDrawable = this.getDrawable(R.drawable.nsvg_play);
        mPresenter = (DelMainPresenter) createPresenter(DelMainPresenter.class);
        mPresenter.attachView(this);
        Bundle extras = getIntent().getExtras();
        aid = extras.getString("aid");
        int textSize = 16;
        if (ContentManager.getInstance().getTextSize() == 1) {
            textSize = 21;
        }
        else if (ContentManager.getInstance().getTextSize() == 2) {
            textSize = 26;
        }
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        skProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                service.seek(progress / 100f);
            }
        });
        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service) {
                if (connected) {
                    ArticleDetailActivity.this.service = service;
                    initServiceData();
                }
            }
        };
        proxy.bind();
    }


    //根据service的状态，设定play按钮的各种状态
    private void initPlayState() {
        if (service != null) {
            SpeechService.SpeechServiceState state = service.getState();
            switch (state) {
                case Ready:
                    ivPlayBarBtn.setImageDrawable(this.getDrawable(R.drawable.nsvg_play));
                    break;
                case Paused:
                    ivPlayBarBtn.setImageDrawable(this.getDrawable(R.drawable.nsvg_play));
                    break;
                case Playing:
                case Loadding:
                    ivPlayBarBtn.setImageDrawable(this.getDrawable(R.drawable.nsvg_play));
                    ((Animatable) ivPlayBarBtn.getDrawable()).start();
                    break;
            }
        }
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {
    }

    @OnClick(R.id.ll_source_detail)
    void onSourceDetail(View v) {
        Intent intent = new Intent();
        intent.setClass(this, HtmlActivity.class);
        intent.putExtra(HtmlActivity.EXTRA_HTML, articleUrl);
        startActivity(intent);
    }

    @OnClick(R.id.ll_article_edit)
    void onEditDetail(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("id", mCurrentArticle.getArticleId());
        bundle.putString("title", mCurrentArticle.getTitle());
        bundle.putString("content", mCurrentArticle.getContent());
        jumpActivity(ArticleDetailEditActivity.class, bundle);
    }

    @OnClick(R.id.iv_back)
    void onBackClick(View v) {
        finish();
    }

    @OnClick(R.id.tv_fk)
    void onTvfkClick(View v) {
        jumpActivity(FeedBackActivity.class);
    }

    @OnClick({R.id.ll_setting, R.id.iv_setting, R.id.tv_setting})
    void onSettingClick(View v) {
        if (mArticleDetailSetting == null) {
            mArticleDetailSetting = new ArticleDetailSetting(new ArticleDetailSetting.SettingListener() {
                @Override
                public void onSpeed(int speed) {
                    switch (speed) {
                        case 0:
                            service.setSpeed(Speechor.SpeechorSpeed.SPEECH_SPEED_NORMAL);
                            break;
                        case 1:
                            service.setSpeed(Speechor.SpeechorSpeed.SPEECH_SPEED_MULTIPLE_1_POINT_5);
                            break;
                        case 2:
                            service.setSpeed(Speechor.SpeechorSpeed.SPEECH_SPEED_MULTIPLE_2);
                            break;
                        case 3:
                            service.setSpeed(Speechor.SpeechorSpeed.SPEECH_SPEED_MULTIPLE_2_POINT_5);
                            break;
                    }
                }

                @Override
                public void onTime(int time) {
                    switch (time) {
                        case 0:
                            service.cancelCountDown();
                            break;
                        case 1:
                            service.setCountDown(SpeechService.CountDownMode.NumberCount, 1);
                            break;
                        case 2:
                            service.setCountDown(SpeechService.CountDownMode.MinuteCount, 10);
                            break;
                        case 3:
                            service.setCountDown(SpeechService.CountDownMode.MinuteCount, 20);
                            break;
                        case 4:
                            service.setCountDown(SpeechService.CountDownMode.MinuteCount, 30);
                            break;
                    }
                }

                @Override
                public void onVoiceType(int type) {
                    switch (type) {
                        case 0:
                            service.setRole(Speechor.SpeechorRole.XiaoYao);
                            break;
                        case 1:
                            service.setRole(Speechor.SpeechorRole.XiaoIce);
                            break;
                        case 2:
                            service.setRole(Speechor.SpeechorRole.XiaoMei);
                            break;
                        case 3:
                            service.setRole(Speechor.SpeechorRole.YaYa);
                            break;
                    }
                }
            });
        }
        mArticleDetailSetting.setRole(service.getRole());
        mArticleDetailSetting.setSpeed(service.getSpeed());
        mArticleDetailSetting.setCountDown(service.getCountDownMode(), service.getCountDownValue());
        mArticleDetailSetting.showDialog(this);
    }

    @OnClick({R.id.ll_font, R.id.iv_font, R.id.tv_font})
    void onFontClick(View v) {
        if (mArticleDetailFont == null) {
            mArticleDetailFont = new ArticleDetailFont(new ArticleDetailFont.FontClickListener() {
                @Override
                public void onFontChange(int change) {
                    ContentManager.getInstance().setTextSize(change);
                    switch (change) {
                        case 0:
                            tvArTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            break;
                        case 1:
                            tvArTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                            tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                            break;
                        case 2:
                            tvArTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                            tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                            break;
                    }
                    //因为文字大小已经发生变化，如果文章正在播放，直接更新文章滚动位置
                    synchronized (service) {
                        switch (service.getState()) {
                            case Ready:
                            case Playing:
                            case Paused:
                                showContent(service.getSpeechorTextFragments(), service.getSpeechorFrameIndex());
                                break;
                        }
                    }
                }
            });
        }
        mArticleDetailFont.getTextSize(ContentManager.getInstance().getTextSize());
        mArticleDetailFont.showDialog(this);
    }

    @OnClick({R.id.ll_share, R.id.iv_share, R.id.tv_share})
    void onShareClick(View v) {
        if (mArticleDetailShare == null) {
            mArticleDetailShare = new ArticleDetailShare(new ArticleDetailShare.ShareClickListener() {
                @Override
                public void onShareChange(int type) {
                    if (mCurrentArticle == null) {
                        return;
                    }
                    switch (type) {
                        case 0:
                            if (mCurrentArticle != null && !TextUtils.isEmpty(mCurrentArticle.getShareUrl()))
                                WXapi.shareWx(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                        null, mCurrentArticle.getTitle(),
                                        TextUtils.isEmpty(mCurrentArticle.getContent()) ? "" : mCurrentArticle.getContent().length() > 45 ?
                                                mCurrentArticle.getContent().substring(0, 40) : mCurrentArticle.getContent());
                            break;
                        case 1:
                            if (mCurrentArticle != null && !TextUtils.isEmpty(mCurrentArticle.getShareUrl()))
                                WXapi.sharePyq(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                        null, mCurrentArticle.getTitle(),
                                        TextUtils.isEmpty(mCurrentArticle.getContent()) ? "" : mCurrentArticle.getContent().length() > 45 ?
                                                mCurrentArticle.getContent().substring(0, 40) : mCurrentArticle.getContent());
                            break;
                        case 2:
                            if (mCurrentArticle != null && !TextUtils.isEmpty(mCurrentArticle.getShareUrl()))
                                QQApi.shareQQ(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                        null, mCurrentArticle.getTitle(),
                                        TextUtils.isEmpty(mCurrentArticle.getContent()) ? "" : mCurrentArticle.getContent().length() > 45 ?
                                                mCurrentArticle.getContent().substring(0, 40) : mCurrentArticle.getContent());
                            break;
                        case 3:
                            if (mCurrentArticle != null && !TextUtils.isEmpty(mCurrentArticle.getShareUrl()))
                                QQApi.shareSpace(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                        null, mCurrentArticle.getTitle(),
                                        TextUtils.isEmpty(mCurrentArticle.getContent()) ? "" : mCurrentArticle.getContent().length() > 45 ?
                                                mCurrentArticle.getContent().substring(0, 40) : mCurrentArticle.getContent());
                            break;
                        case 4:
                            if (mCurrentArticle != null) {
                                ClipboardManager cm =
                                        (ClipboardManager) ArticleDetailActivity.this.getSystemService(ArticleDetailActivity.this.CLIPBOARD_SERVICE);
                                ClipData mClipData = ClipData.newPlainText("Label", mCurrentArticle.getShareUrl());
                                cm.setPrimaryClip(mClipData);
                                ContentManager.getInstance().addCopyItem(mCurrentArticle.getShareUrl());
                                toastShort("分享链接复制成功");
                            }
                            break;
                    }
                }
            });
        }
        mArticleDetailShare.showDialog(this);
    }

    @OnClick(R.id.tv_fav)
    void onFav(View v) {
        ArticleDataProvider provider = new ArticleDataProvider(this);
        provider.favorite(mCurrentArticle, mCurrentArticle.getStore() == 1 ? false : true, new ArticleDataProvider.ArticleLoaderCallback() {
            @Override
            public void invoke(int errorCode, Article article) {
                if (errorCode == 0) {
                    tvFav.setText(article.getStore() == 1 ? "已收藏" : "收藏");
                    if (service != null && service.getSelected() != null
                            && mCurrentArticle != null
                            && service.getSelected().getArticleId().equals(mCurrentArticle.getArticleId())) {
                        service.getSelected().setStore(article.getStore());
                        service.updateNotification();
                    }
                    EventBus.getDefault().post(new AddStoreSuccessEvent());

                }
                else {
                    Toast.makeText(ArticleDetailActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.tv_next)
    void onNext(View v) {
        switch (service.getState()) {
            case Ready:
                /*
                当状态为ready时，一种情况是列表为空，另外一种情况是定时器被关闭，当定时器停止时，指针已经指向下一个
                 */
                if (service.getSelected() != null) {
                    service.play(service.getSelected().getArticleId());
                    return;
                }
                break;
            default:
                if (service.hasNext()) {
                    service.playNext();
                    return;
                }
                break;
        }
        T.s(this, "已经没有要播放的文章了");
    }

    @OnClick({R.id.rl_main_play_bar_play, R.id.iv_play_bar_btn})
    void onPlay(View v) {
        synchronized (service) {
            switch (service.getState()) {
                case Loadding:
                case Playing:
                    service.pause();
                    break;

                case Paused:
                    service.resume();
                    break;

                case Error:
                    service.seek((float) skProgress.getProgress() / (float) 100);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechEvent(RecycleEvent event) {
        if (event instanceof SpeechStartEvent) {
            tvContent.setText("正文正在加载...");
            tvTitle.setText(event.getArticle().getTitle());
            tvAuthor.setText(event.getArticle().getSourceName());
            tvAuthor.setVisibility(event.getArticle().getSourceName() != null && event.getArticle().getSourceName().trim() != "" ? View.VISIBLE : View.GONE);
        }
        else if (event instanceof SpeechReadyEvent) {
            //需要从网络加载的字段，需要在此事件中才能获取到
            mCurrentArticle = event.getArticle();
            tvContent.setText(mCurrentArticle.getContent());
            llArticleEdit.setVisibility((mCurrentArticle.getInType() == 1 || TextUtils.isEmpty(mCurrentArticle.getUrl())) ? View.VISIBLE : View.GONE);
            llSourceDetail.setVisibility((mCurrentArticle.getInType() == 1 || TextUtils.isEmpty(mCurrentArticle.getUrl())) ? View.GONE : View.VISIBLE);
            tvFav.setText(mCurrentArticle.getStore() == 1 ? "已收藏" : "收藏");
        }
        else if (event instanceof SpeechProgressEvent) {
            SpeechProgressEvent spe = (SpeechProgressEvent) event;
            showContent(((SpeechProgressEvent) event).getTextFragments(), ((SpeechProgressEvent) event).getFrameIndex());
            setArticleProgress(spe.getFrameIndex(), spe.getTextFragments().size());
            return;
        }
        else if (event instanceof SpeechEndEvent) {
            setArticleProgress(100, 100);
        }
        else if (event instanceof SpeechResumeEvent) {
            aid = event.getArticle().getId();
        }
        else if (event instanceof SpeechStopEvent) {
            if (ivPlayBarBtn.getDrawable() != mPlayDrawable) {
                ivPlayBarBtn.setImageDrawable(mPlayDrawable);
                ((Animatable) mPlayDrawable).start();
            }
            float progress = 0;
            setArticleProgress(100, 100);
        }
        else if (event instanceof FavoriteEvent) {
            Log.d("SPEECH_", "Favorite Event");
            if (event.getArticle().getArticleId() != null && event.getArticle().getArticleId().equals(mCurrentArticle.getArticleId())) {
                tvFav.setText(event.getArticle().getStore() == 1 ? "已收藏" : "收藏");
            }
            return;
        }
        setPlayerState(service.getState());
    }

    private void setPlayerState(SpeechService.SpeechServiceState state) {
        switch (state) {
            case Error:
                if (ivPlayBarBtn.getDrawable() != mPauseDrawable) {
                    ivPlayBarBtn.setImageDrawable(mPauseDrawable);
                    ((Animatable) mPauseDrawable).start();
                }
                break;
            case Playing:
            case Loadding:
                if (ivPlayBarBtn.getDrawable() != mPlayDrawable) {
                    ivPlayBarBtn.setImageDrawable(mPlayDrawable);
                    ((Animatable) mPlayDrawable).start();
                }

                break;
            case Paused:
                if (ivPlayBarBtn.getDrawable() != mPauseDrawable) {
                    ivPlayBarBtn.setImageDrawable(mPauseDrawable);
                    ((Animatable) mPauseDrawable).start();
                }
                break;
            case Ready:
                if (ivPlayBarBtn.getDrawable() != mPauseDrawable) {
                    ivPlayBarBtn.setImageDrawable(mPauseDrawable);
                    ((Animatable) mPauseDrawable).start();
                }
                break;
        }
    }


    private void setArticleProgress(int frameIndex, int framesTotal) {
        float percentage = 0f;
        int progress = 0;

        if (framesTotal != 0) {
            percentage = (float) frameIndex / (float) framesTotal;
            progress = (int) (percentage * 100);
        }
        apbMain.setProgress(progress);
        skProgress.setProgress(progress);
    }


    //设定文章正文，以及播放文字的高亮
    private void showContent(List<String> textFragments, int frameIndex) {
        StringBuilder textBuilder = new StringBuilder();
        final int fragmentsSize = textFragments.size();
        //generate the readed text's view.
        for(int index = 0; index < frameIndex; ++index) {
            textBuilder.append(textFragments.get(index).replace("\n", "<br/>"));
        }
        tvContent.setText(Html.fromHtml(textBuilder.toString()));
        //after invoke method setText, tvContent's getLineHeight not available.
        //post measure behavior at next frame
        tvContent.post(()->{
            //caculate the readed text's height.
            final int offsetHeight = tvContent.getLineHeight() * tvContent.getLineCount();
            //generate whole text's view.
            for(int index = frameIndex; index < fragmentsSize; ++index ) {
                String fragText = textFragments.get(index).replace("\n", "<br/>");
                fragText = ((index == frameIndex)? "<font color=\"#488def\">" +fragText + "</font>" : fragText);
                textBuilder.append(fragText);
            }
            tvContent.setText(Html.fromHtml(textBuilder.toString()));
            //caculute the whole textview's height by same method.
            tvContent.post(()->{
                int contentHeight = tvContent.getLineCount() * tvContent.getLineHeight();
                if(contentHeight > svContent.getMeasuredHeight()) {
                    svContent.setScrollY(offsetHeight);
                }
            });
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册
        EventBus.getDefault().unregister(this);
        //断开服务
        proxy.unbind();
    }

    @Override
    public void onSuccessDel(String str) {
    }

    @Override
    public void onErrorDel(int code, String errorMsg) {

    }


    @Override
    public void onSuccessAddLove(String str, Article article) {
        if ("收藏".equals(tvFav.getText().toString())) {
            tvFav.setText("已收藏");
        }
        else {
            tvFav.setText("收藏");
        }
    }


    @Override
    public void onErrorAddLove(int code, String errorMsg) {

    }

}
