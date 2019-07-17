package cn.xylink.mting.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.speech.event.RecycleEvent;
import cn.xylink.mting.speech.event.SpeechEndEvent;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechReadyEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.dialog.ArticleDetailFont;
import cn.xylink.mting.ui.dialog.ArticleDetailSetting;
import cn.xylink.mting.ui.dialog.ArticleDetailShare;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.widget.ArcProgressBar;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class ArticleDetailActivity extends BaseActivity {

    private boolean isPlaying = false;
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
    private String aid;


    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail);
    }

    private void initServiceData() {
        if (aid != null) {
            isPlaying = true;
            service.play(aid);
        }
        Article selected = service.getSelected();
        if (selected != null) {
            tvContent.setText(selected.getContent());
            tvTitle.setText(selected.getTitle());
        }

    }

    @Override
    protected void initView() {
        Bundle extras = getIntent().getExtras();
        aid = extras.getString("aid");
        int textSize = 16;
        if (ContentManager.getInstance().getTextSize() == 1) {
            textSize = 21;
        } else if (ContentManager.getInstance().getTextSize() == 2) {
            textSize = 26;
        }
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {
    }

    @OnClick(R.id.iv_back)
    void onBackClick(View v) {
        finish();
    }

    @OnClick(R.id.tv_fk)
    void onTvfkClick(View v) {

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
                            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            break;
                        case 1:
                            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                            break;
                        case 2:
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
            mArticleDetailShare = new ArticleDetailShare();
        }
        mArticleDetailShare.showDialog(this);
    }

    @OnClick(R.id.tv_fav)
    void onFav(View v) {
    }

    @OnClick(R.id.tv_next)
    void onNext(View v) {
        service.playNext();
    }

    @OnClick({R.id.rl_main_play_bar_play, R.id.iv_play_bar_btn})
    void onPlay(View v) {
        if (isPlaying) {
            service.pause();
        } else {
            service.play(aid);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(RecycleEvent event) {
        if (event instanceof SpeechStartEvent) {
            tvContent.setText("");
        } else if (event instanceof SpeechReadyEvent) {
            isPlaying = true;
            aid = event.getArticle().getId();
            ivPlayBarBtn.setImageResource(R.mipmap.ico_pause);
            tvContent.setText(event.getArticle().getContent());
        } else if (event instanceof SpeechProgressEvent) {
            SpeechProgressEvent spe = (SpeechProgressEvent) event;
            showContent(spe);
            float progress = (float) spe.getFrameIndex() / (float) spe.getTextFragments().size();
            apbMain.setProgress((int) (progress * 100));
            skProgress.setProgress((int) (progress * 100));
        } else if (event instanceof SpeechEndEvent) {
            isPlaying = false;
            float progress = 1;
            apbMain.setProgress((int) (progress * 100));
            skProgress.setProgress((int) (progress * 100));
        } else if (event instanceof SpeechErrorEvent) {
            isPlaying = false;
            ivPlayBarBtn.setImageResource(R.mipmap.ico_playing);
            float progress = 0;
            apbMain.setProgress((int) (progress * 100));
            skProgress.setProgress((int) (progress * 100));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        isPlaying = false;
        ivPlayBarBtn.setImageResource(R.mipmap.ico_playing);
        float progress = 0;
        apbMain.setProgress((int) (progress * 100));
        skProgress.setProgress((int) (progress * 100));
    }

    private void showContent(SpeechProgressEvent spe) {
        int frameIndex = spe.getFrameIndex();
        List<String> textFragments = spe.getTextFragments();
        tvContent.setText("");
        for (int i = 0; i < textFragments.size(); i++) {
            String s = textFragments.get(i);
            SpannableString spannableString = new SpannableString(s);
            ClickableSpan clickableSpan;
            if (i == frameIndex) {
                clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#488def"));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                };
            } else {
                clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        //Do something.
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#333333"));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                };
            }
            spannableString.setSpan(clickableSpan, 0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvContent.append(spannableString);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
    protected void onDestroy() {
        super.onDestroy();
        //反注册
        EventBus.getDefault().unregister(this);
        //断开服务
        proxy.unbind();
    }
}
