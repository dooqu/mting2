package cn.xylink.mting.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import cn.xylink.mting.widget.ArcProgressBar;
import cn.xylink.mting.widget.MyScrollView;

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


    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail);
    }

    private void initServiceData() {
        getWindow().setStatusBarColor(Color.argb(0, 72, 141, 239));
        mCurrentArticle = service.getSelected();
        if (service.getState() == Speechor.SpeechorState.SpeechorStatePlaying && aid.equals(mCurrentArticle.getArticleId())) {
            if (mCurrentArticle != null && (mCurrentArticle.getInType() == 1 || TextUtils.isEmpty(mCurrentArticle.getUrl()))) {
                llArticleEdit.setVisibility(View.VISIBLE);
                llSourceDetail.setVisibility(View.GONE);
                mTitleheight = tvContent.getY();
                aid = mCurrentArticle.getId();
                tvContent.setText(mCurrentArticle.getContent());

            }
            isPlaying = 1;
            ivPlayBarBtn.setImageResource(R.mipmap.ico_pause);
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
            }
        });
    }

    @Override
    protected void initView() {
        mPresenter = (DelMainPresenter) createPresenter(DelMainPresenter.class);
        mPresenter.attachView(this);
        Bundle extras = getIntent().getExtras();
        aid = extras.getString("aid");
        int textSize = 16;
        if (ContentManager.getInstance().getTextSize() == 1) {
            textSize = 21;
        } else if (ContentManager.getInstance().getTextSize() == 2) {
            textSize = 26;
        }
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
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
                            service.setCountDown(SpeechService.CountDownMode.None, 0);
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
                            service.setRole(Speechor.SpeechorRole.XiaoMei);
                            break;
                        case 1:
                            service.setRole(Speechor.SpeechorRole.XiaoIce);
                            break;
                        case 2:
                            service.setRole(Speechor.SpeechorRole.XiaoYao);
                            break;
                        case 3:
                            service.setRole(Speechor.SpeechorRole.YaYa);
                            break;
                    }
                }
            });
        }
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
                            WXapi.shareWx(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                    mCurrentArticle.getPicture(), mCurrentArticle.getTitle(),
                                    mCurrentArticle.getContent().substring(0, 20));
                            break;
                        case 1:
                            WXapi.sharePyq(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                    mCurrentArticle.getPicture(), mCurrentArticle.getTitle(),
                                    mCurrentArticle.getContent().substring(0, 20));
                            break;
                        case 2:
                            QQApi.shareQQ(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                    mCurrentArticle.getPicture(), mCurrentArticle.getTitle(),
                                    mCurrentArticle.getContent().substring(0, 20));
                            break;
                        case 3:
                            QQApi.shareSpace(ArticleDetailActivity.this, mCurrentArticle.getShareUrl(),
                                    mCurrentArticle.getPicture(), mCurrentArticle.getTitle(),
                                    mCurrentArticle.getContent().substring(0, 20));
                            break;
                        case 4:
                            if (mCurrentArticle != null) {
                                ClipboardManager cm = (ClipboardManager) ArticleDetailActivity.this.getSystemService(ArticleDetailActivity.this.CLIPBOARD_SERVICE);
                                ClipData mClipData = ClipData.newPlainText("Label", mCurrentArticle.getShareUrl());
                                cm.setPrimaryClip(mClipData);
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
        String txt = tvFav.getText().toString();
        boolean isFav = "收藏".equals(txt);
        AddLoveRequest request = new AddLoveRequest();
        request.setArticleId(mCurrentArticle.getArticleId());
        request.setType(!isFav ? AddLoveRequest.TYPE.STORE.name() : AddLoveRequest.TYPE.CANCEL.name());
        request.doSign();
        mPresenter.addLove(request);
    }

    @OnClick(R.id.tv_next)
    void onNext(View v) {
        if (service.hasNext()) {
            service.playNext();
        }
    }

    @OnClick({R.id.rl_main_play_bar_play, R.id.iv_play_bar_btn})
    void onPlay(View v) {
        if (isPlaying == 1) {
            service.pause();
        } else if (isPlaying == 0) {
            service.play(aid);
        } else if (isPlaying == -1) {
            service.resume();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(RecycleEvent event) {
        if (event instanceof SpeechStartEvent) {
            tvContent.setText("");
        } else if (event instanceof SpeechReadyEvent) {
            mCurrentArticle = event.getArticle();
            if (mCurrentArticle.getInType() == 1 || TextUtils.isEmpty(mCurrentArticle.getUrl())) {
                llArticleEdit.setVisibility(View.VISIBLE);
                llSourceDetail.setVisibility(View.GONE);
                mTitleheight = tvContent.getY();
            }
            isPlaying = 1;
            aid = event.getArticle().getId();
            ivPlayBarBtn.setImageResource(R.mipmap.ico_pause);
            tvContent.setText(event.getArticle().getContent());
        } else if (event instanceof SpeechProgressEvent) {
            SpeechProgressEvent spe = (SpeechProgressEvent) event;
            showContent(spe);
            float progress = (float) spe.getFrameIndex() / (float) spe.getTextFragments().size();
            setArticleProgress(progress, 100, spe);
        } else if (event instanceof SpeechEndEvent) {
            isPlaying = 0;
            float progress = 1;
            setArticleProgress(progress, 100, null);
        } else if (event instanceof SpeechErrorEvent) {
            isPlaying = 0;
            ivPlayBarBtn.setImageResource(R.mipmap.ico_playing);
            float progress = 0;
            setArticleProgress(progress, 100, null);
        } else if (event instanceof SpeechPauseEvent) {
            isPlaying = -1;
            ivPlayBarBtn.setImageResource(R.mipmap.ico_playing);
        } else if (event instanceof SpeechResumeEvent) {
            isPlaying = 1;
            aid = event.getArticle().getId();
            ivPlayBarBtn.setImageResource(R.mipmap.ico_pause);
        }
    }

    private void setArticleProgress(float progress, int base, SpeechProgressEvent spe) {
        apbMain.setProgress((int) (progress * base));
        skProgress.setProgress((int) (progress * base));
        if (spe != null) {
            List<String> textFragments = spe.getTextFragments();
            String read = "";
            String unread = "";
            for (int i = 0; i < textFragments.size(); i++) {
                if (i <= spe.getFrameIndex()) {
                    read += textFragments.get(i);
                } else {
                    unread += textFragments.get(i);
                }
            }
            float v = read.length() * 1f / (read.length() + unread.length());
            svContent.scrollTo(0, (int) (svContent.getHeight() * v));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        isPlaying = 0;
        ivPlayBarBtn.setImageResource(R.mipmap.ico_playing);
        float progress = 0;
        setArticleProgress(progress, 100, null);
    }

    private void showContent(SpeechProgressEvent spe) {
        int frameIndex = spe.getFrameIndex();
        List<String> textFragments = spe.getTextFragments();
        tvContent.setText("");
        String txt = "";
        for (int i = 0; i < textFragments.size(); i++) {
            String s = textFragments.get(i);
            if (i == frameIndex) {
                s = "<font color=\"#488def\">" + s + "</font>";
            }
            txt += s;
        }
        tvContent.setText(Html.fromHtml(txt));
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
        } else {
            tvFav.setText("收藏");
        }
        EventBus.getDefault().post(new AddStoreSuccessEvent());
    }

    @Override
    public void onErrorAddLove(int code, String errorMsg) {

    }

}
