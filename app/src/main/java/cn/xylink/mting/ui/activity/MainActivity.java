package cn.xylink.mting.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.AddLoveRequest;
import cn.xylink.mting.bean.AddUnreadRequest;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.bean.DelReadedRequest;
import cn.xylink.mting.bean.DelUnreadRequest;
import cn.xylink.mting.contract.AddUnreadContract;
import cn.xylink.mting.contract.DelMainContract;
import cn.xylink.mting.event.AddStoreSuccessEvent;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.CloseLeftMenuEvent;
import cn.xylink.mting.event.DeleteArticleSuccessEvent;
import cn.xylink.mting.event.NotifyMainPlayEvent;
import cn.xylink.mting.event.PlayBarVisibilityEvent;
import cn.xylink.mting.presenter.AddUnreadPresenter;
import cn.xylink.mting.presenter.DelMainPresenter;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.FavoriteEvent;
import cn.xylink.mting.speech.event.SpeechBufferingEvent;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechPauseEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechResumeEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.adapter.MainFragmentAdapter;
import cn.xylink.mting.ui.dialog.MainAddMenuPop;
import cn.xylink.mting.ui.dialog.ShareAppDialog;
import cn.xylink.mting.ui.fragment.BaseMainTabFragment;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.T;
import cn.xylink.mting.widget.ArcProgressBar;

/*主页面*/
public class MainActivity extends BasePresenterActivity implements BaseMainTabFragment.OnControllerListener, MainAddMenuPop.OnMainAddMenuListener
        , DelMainContract.IDelMainView, AddUnreadContract.IAddUnreadView, ViewPager.OnPageChangeListener {

    public static String SHARE_URL = "share_url";
    public static String SHARE_SUCCESS = "share_success";
    public static String ARTICLE_ID = "article_id";
    @BindView(R.id.tv_main_tabar_unread)
    TextView mUnreadTextView;
    @BindView(R.id.tv_main_tabar_readed)
    TextView mReadedTextView;
    @BindView(R.id.tv_main_tabar_love)
    TextView mLoveTextView;
    @BindView(R.id.dl_main)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.vp_main)
    ViewPager mViewPager;
    @BindView(R.id.iv_main_title_add)
    ImageView mAddImageView;
    @BindView(R.id.tv_play_bar_title)
    TextView mPlayBarTitle;
    @BindView(R.id.apb_main_play_progress)
    ArcProgressBar mProgress;
    @BindView(R.id.rl_main_play_bar_play)
    RelativeLayout mPlayBtn;
    @BindView(R.id.iv_play_bar_btn)
    ImageView mPlayBtnSRC;
    @BindView(R.id.pb_main_play_progress)
    ProgressBar mLoadingProgress;
    @BindView(R.id.ll_main_play_bar_layout)
    LinearLayout mPlayBarLayout;
    private TAB_ENUM mCurrentTabIndex = TAB_ENUM.TAB_UNREAD;
    public SpeechServiceProxy proxy;
    private SpeechService service;
    private MainFragmentAdapter mTabAdapter;
    private DelMainPresenter mPresenter;
    private AddUnreadPresenter mAddUreadPresenter;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private String mOpenArticleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.v();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        showShareResultDialog(getIntent().getIntExtra(SHARE_SUCCESS, -1), getIntent().getStringExtra(SHARE_URL));
        mOpenArticleID = getIntent().getStringExtra(ARTICLE_ID);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.v("*^*^*^*^*" + intent.getIntExtra(SHARE_SUCCESS, -1));
        showShareResultDialog(intent.getIntExtra(SHARE_SUCCESS, -1), getIntent().getStringExtra(SHARE_URL));
        String articleID = getIntent().getStringExtra(ARTICLE_ID);
        if (!TextUtils.isEmpty(articleID)) {
            Bundle bundle = new Bundle();
            bundle.putString("aid", articleID);
            this.jumpActivity(ArticleDetailActivity.class, bundle);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void preView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        mDrawerLayout.setFocusableInTouchMode(false);
        mPresenter = (DelMainPresenter) createPresenter(DelMainPresenter.class);
        mPresenter.attachView(this);
        mAddUreadPresenter = (AddUnreadPresenter) createPresenter(AddUnreadPresenter.class);
        mAddUreadPresenter.attachView(this);
        mPauseDrawable = this.getDrawable(R.drawable.nsvg_pause);
        mPlayDrawable = this.getDrawable(R.drawable.nsvg_play);
    }

    @Override
    protected void initData() {
        TAB_ENUM.TAB_LOVE.setView(mLoveTextView);
        TAB_ENUM.TAB_READED.setView(mReadedTextView);
        TAB_ENUM.TAB_UNREAD.setView(mUnreadTextView);
        mTabAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOffscreenPageLimit(TAB_ENUM.values().length);
        mViewPager.addOnPageChangeListener(this);
        EventBus.getDefault().register(this);
        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service) {
                if (connected) {
                    MainActivity.this.service = service;
                    initPlayState();
                }
            }
        };
        proxy.bind();

    }

    private void setPlayBarState() {
        String playTitle = null;
        List<Article> list = SpeechList.getInstance().getArticleList();
        Article art = SpeechList.getInstance().getCurrent();
        if (art == null)
            art = list != null && list.size() > 0 ? list.get(0) : null;
        if (art != null) {
            playTitle = art.getTitle();
            float progress = art.getProgress();
            mProgress.setProgress((int) (progress * 100));
        }
        mPlayBarTitle.setText(TextUtils.isEmpty(playTitle) ? "还没有文章，快去添加吧~" : playTitle);

    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onPlay(String id) {
        L.v();
//        if (service != null)
//            service.play(id);
        AddUnreadRequest request = new AddUnreadRequest();
        request.setArticleIds(id);
        request.doSign();
        mAddUreadPresenter.addUnread(request);
        EventBus.getDefault().post(new AddUnreadEvent());
    }

    @Override
    public void onDataSuccess() {
        L.v();
        setPlayBarState();
        if (!TextUtils.isEmpty(mOpenArticleID)) {
            L.v();
            Bundle bundle = new Bundle();
            bundle.putString("aid", mOpenArticleID);
            this.jumpActivity(ArticleDetailActivity.class, bundle);
            mOpenArticleID = null;
        }
    }

    @Override
    public void onLove(Article article) {
        AddLoveRequest request = new AddLoveRequest();
        request.setArticleId(article.getArticleId());
        request.setType(article.getStore() == 0 ? AddLoveRequest.TYPE.STORE.name().toLowerCase() : AddLoveRequest.TYPE.CANCEL.name().toLowerCase());
        request.doSign();
        mPresenter.addLove(request, article);
    }

    private Queue<BaseMainTabFragment.TAB_TYPE> mMessageQueue = new LinkedList<>();

    @Override
    public void onDel(BaseMainTabFragment.TAB_TYPE tabType, String id) {
        mMessageQueue.add(tabType);
        switch (tabType) {
            case UNREAD:
                List<String> list = new ArrayList<>();
                list.add(id);
//                SpeechList.getInstance().removeSome(list);
                if (service != null)
                    service.removeFromSpeechList(list);
                DelUnreadRequest request = new DelUnreadRequest();
                request.setArticleIds(id);
                request.doSign();
                mPresenter.delUnread(request);
                break;
            case READED:
                DelReadedRequest readedRequest = new DelReadedRequest();
                readedRequest.setIds(id);
                readedRequest.doSign();
                mPresenter.delReaded(readedRequest);
                break;
            case COLLECT:
                DelReadedRequest stroeRequest = new DelReadedRequest();
                stroeRequest.setIds(id);
                stroeRequest.doSign();
                mPresenter.delConllect(stroeRequest);
                break;
        }
    }

    @Override
    public void onAdd() {
        Intent mIntent = new Intent(this, AddArticleActivity.class);
        startActivity(mIntent);
    }

    @Override
    public void onArrange() {
        Intent mIntent = new Intent(this, ArrangeActivity.class);
        mIntent.putExtra(ArrangeActivity.ACTION_ARRANGE_TYPE, mCurrentTabIndex.getIndex());
        startActivity(mIntent);
    }

    @Override
    public void onSuccessDel(String str) {
        T.showCustomToast("删除成功");
        EventBus.getDefault().post(new DeleteArticleSuccessEvent(mMessageQueue.poll()));
        setPlayBarState();
    }

    @Override
    public void onErrorDel(int code, String errorMsg) {
        T.s(this, "删除失败");
    }

    @Override
    public void onSuccessAddLove(String str, Article article) {
        if (article != null) {
            article.setStore(article.getStore() ^ 1);
            if (service != null && service.getSelected() != null
                    && service.getSelected().getArticleId().equals(article.getArticleId())) {
                service.updateNotification();
            }
            if (article.getStore() == 1) {
                T.s(this, "收藏成功");
            } else {
                T.s(this, "取消收藏成功");
            }
        }
        EventBus.getDefault().post(new AddStoreSuccessEvent(article));
    }

    @Override
    public void onErrorAddLove(int code, String errorMsg) {
        T.s(this, "收藏失败");
    }

    @Override
    public void onSuccessAddUnread(Article msg) {

    }

    @Override
    public void onErrorAddUnread(int code, String errorMsg) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_UNREAD, false);
                break;
            case 1:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_READED, false);
                break;
            case 2:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_LOVE, false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public enum TAB_ENUM {
        TAB_UNREAD(0, null),
        TAB_READED(1, null),
        TAB_LOVE(2, null);

        TAB_ENUM(int index, TextView view) {
            this.index = index;
        }

        private int index;
        private TextView view;

        public TextView getView() {
            return view;
        }

        public void setView(TextView view) {
            this.view = view;
        }

        public int getIndex() {
            return index;
        }

    }


    @OnClick({R.id.iv_main_title_my, R.id.iv_main_title_search, R.id.iv_main_title_add
            , R.id.tv_main_tabar_readed, R.id.tv_main_tabar_unread, R.id.tv_main_tabar_love, R.id.rl_main_play_bar_play, R.id.rl_main_title_layout
            , R.id.tv_play_bar_title})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_title_my:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_main_title_search:
                TCAgent.onEvent(this, "article_search");
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.iv_main_title_add:
                TCAgent.onEvent(this, "add");
                MainAddMenuPop pop = new MainAddMenuPop(MainActivity.this, this);
                pop.showAsRight(mAddImageView);
                break;
            case R.id.tv_main_tabar_unread:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_UNREAD, true);
                break;
            case R.id.tv_main_tabar_readed:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_READED, true);
                break;
            case R.id.tv_main_tabar_love:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_LOVE, true);
                break;
            case R.id.rl_main_play_bar_play:
                L.v("============================");
                if (SpeechList.getInstance().getArticleList() != null && SpeechList.getInstance().getArticleList().size() > 0)
                    playCtrl();
                break;
            case R.id.rl_main_title_layout:
                L.v("============================");
                mTabAdapter.getItem(mCurrentTabIndex.index).backTop();
                break;
            case R.id.tv_play_bar_title:
                String articleId = "";
                List<Article> list = SpeechList.getInstance().getArticleList();
                Article art = SpeechList.getInstance().getCurrent();
                if (art == null)
                    art = list != null && list.size() > 0 ? list.get(0) : null;
                if (art != null) {
                    articleId = art.getArticleId();
                }
                if (!TextUtils.isEmpty(articleId)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("aid", articleId);
                    this.jumpActivity(ArticleDetailActivity.class, bundle);
                }
                break;
        }
    }

    private void initPlayState() {
        if (service != null) {
            SpeechService.SpeechServiceState state = service.getState();
            switch (state) {
                case Ready:
                    mPlayBtnSRC.setImageDrawable(this.getDrawable(R.drawable.nsvg_play));
                    break;
                case Paused:
                    mPlayBtnSRC.setImageDrawable(this.getDrawable(R.drawable.nsvg_play));
                    break;
                case Playing:
                case Loadding:
                    mPlayBtnSRC.setImageDrawable(this.getDrawable(R.drawable.nsvg_play));
                    ((Animatable) mPlayBtnSRC.getDrawable()).start();
                    break;
            }
        }
    }

    //播放按钮逻辑
    private void playCtrl() {
        if (service != null) {
            SpeechService.SpeechServiceState state = service.getState();
            switch (state) {
                case Ready:
                    String aid = null;
                    Article art = SpeechList.getInstance().getCurrent();
                    if (art == null)
                        art = SpeechList.getInstance().getArticleList() != null && SpeechList.getInstance().getArticleList().size() > 0 ?
                                SpeechList.getInstance().getArticleList().get(0) : null;
                    if (art != null) {
                        aid = art.getArticleId();
                    }
                    if (!TextUtils.isEmpty(aid))
                        service.play(aid);
//                    mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_pause));
                    if (mPlayBtnSRC.getDrawable() != mPlayDrawable) {
                        mPlayBtnSRC.setImageDrawable(mPlayDrawable);
                        ((Animatable) mPlayDrawable).start();
                    }
                    break;
                case Paused:
                    if (service.resume())
//                        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_pause));
                        if (mPlayBtnSRC.getDrawable() != mPlayDrawable) {
                            mPlayBtnSRC.setImageDrawable(mPlayDrawable);
                            ((Animatable) mPlayDrawable).start();
                        }
                    break;
                case Playing:
                case Loadding:
                    if (service.pause())
//                        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_playing));
                        if (mPlayBtnSRC.getDrawable() != mPauseDrawable) {
                            mPlayBtnSRC.setImageDrawable(mPauseDrawable);
                            ((Animatable) mPauseDrawable).start();
                        }
                    break;
            }
        }
    }

    private void doAnim(TAB_ENUM currentTab, TAB_ENUM goTab, boolean isGoPage) {
        if (currentTab != goTab) {
            if (isGoPage)
                mViewPager.setCurrentItem(goTab.getIndex(), false);
            mCurrentTabIndex = goTab;
            ObjectAnimator ccAnimator = ObjectAnimator.ofInt(currentTab.getView(), "textColor", 0xff333333, 0xff999999);
            ccAnimator.setEvaluator(new ArgbEvaluator());
            ccAnimator.setDuration(180);
            ObjectAnimator gcAnimator = ObjectAnimator.ofInt(goTab.getView(), "textColor", 0xff999999, 0xff333333);
            gcAnimator.setEvaluator(new ArgbEvaluator());
            gcAnimator.setDuration(180);
            ObjectAnimator csAnimator = ObjectAnimator.ofFloat(currentTab.getView(), "textSize", 24f, 15f);
            csAnimator.setDuration(180);
            ObjectAnimator gsAnimator = ObjectAnimator.ofFloat(goTab.getView(), "textSize", 15f, 24f);
            gsAnimator.setDuration(180);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ccAnimator, gcAnimator, csAnimator, gsAnimator);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.start();
            TextPaint paint = goTab.getView().getPaint();
            paint.setFakeBoldText(true);
            TextPaint paint1 = currentTab.getView().getPaint();
            paint1.setFakeBoldText(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }


    /*
某一个文章准备开始播报的时候被调用, event.getArticle返回的是文章对象
注意，该事件被调用并不意味着真正的tts开始播放，在此时间调用后，还有获取Article正文缓冲、以及tts转换等缓冲时间，
真正的播放开始要在progress的0进度开始
该事件调用后，可以切换播放器的标题等操作
 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(SpeechStartEvent event) {
        L.v(event.getArticle());
        setPlayBarState();
//        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_pause));
        if (mPlayBtnSRC.getDrawable() != mPlayDrawable) {
            mPlayBtnSRC.setImageDrawable(mPlayDrawable);
            ((Animatable) mPlayDrawable).start();
        }
        if (mLoadingProgress.getVisibility() != View.VISIBLE) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechBufferingEvent(SpeechBufferingEvent event) {
        L.v();
        if (mLoadingProgress.getVisibility() != View.VISIBLE) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }
    }


    /*
    播放进度回调， event.getFrameIndex 返回的是当前播报的片段索引,
    event.getTextFragments，返回所有的片段集合
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechProgress(SpeechProgressEvent event) {
        L.v(event.getArticle());
        float progress = (float) event.getFrameIndex() / (float) event.getTextFragments().size();
        mProgress.setProgress((int) (progress * 100));

//        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_pause));
        if (mPlayBtnSRC.getDrawable() != mPlayDrawable) {
            mPlayBtnSRC.setImageDrawable(mPlayDrawable);
            ((Animatable) mPlayDrawable).start();
        }
        mLoadingProgress.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelSuccess(DeleteArticleSuccessEvent event) {
        L.v(event);
        if (event.getTab_type() == BaseMainTabFragment.TAB_TYPE.UNREAD) {
            if (SpeechList.getInstance().getArticleList() == null || SpeechList.getInstance().getArticleList().size() < 1) {
                mPlayBarTitle.setText("还没有文章，快去添加吧~");
            }
        }

    }


    /*
    播放器无内容可播放后，会调用此事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        L.v(event);
//        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_playing));
        if (mPlayBtnSRC.getDrawable() != mPauseDrawable) {
            mPlayBtnSRC.setImageDrawable(mPauseDrawable);
            ((Animatable) mPauseDrawable).start();
        }
        if (event.getStopReason() == SpeechStopEvent.StopReason.ListIsNull) {
            mPlayBarTitle.setText("还没有文章，快去添加吧~");
        }
        mLoadingProgress.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechPause(SpeechPauseEvent event) {
        L.v(event);
//        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_playing));
        if (mPlayBtnSRC.getDrawable() != mPauseDrawable) {
            mPlayBtnSRC.setImageDrawable(mPauseDrawable);
            ((Animatable) mPauseDrawable).start();
        }
        mLoadingProgress.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechResume(SpeechResumeEvent event) {
        L.v(event);
//        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_pause));
        if (mPlayBtnSRC.getDrawable() != mPlayDrawable) {
            mPlayBtnSRC.setImageDrawable(mPlayDrawable);
            ((Animatable) mPlayDrawable).start();
        }
    }


    /*
    播放遇到错误，会调用此事件，比如网络错误等
    event.getArticle()指示当前错误的文章
    event.getErrorCode()指示错误码
    event.getMessage()指示错误提示
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechError(SpeechErrorEvent event) {
        L.v(event);
//        mPlayBtnSRC.setImageDrawable(getResources().getDrawable(R.mipmap.ico_playing));
        if (mPlayBtnSRC.getDrawable() != mPauseDrawable) {
            mPlayBtnSRC.setImageDrawable(mPauseDrawable);
            ((Animatable) mPauseDrawable).start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyPlay(NotifyMainPlayEvent event) {
        L.v(event);
        if (service != null)
            service.play(event.getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseLeftMenu(CloseLeftMenuEvent event) {
        L.v(event);
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawers();
        if (event.isShare()) {
            ShareAppDialog dialog = new ShareAppDialog(this);
            dialog.show();
        }
        if (event.isStopSer() && service != null) {
            service.clearSpeechList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifFavorite(FavoriteEvent event) {
        L.v(event);
        if (event.getArticle() != null)
            EventBus.getDefault().post(new AddStoreSuccessEvent(event.getArticle()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayBarVis(PlayBarVisibilityEvent event) {
        L.v(event);
        mPlayBarLayout.setVisibility(event.getVisibility());
    }


    @Override
    protected boolean enableVersionUpgrade() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.v();
        EventBus.getDefault().unregister(this);
        proxy.unbind();
    }
}
