package cn.xylink.mting.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;
import cn.xylink.mting.ui.adapter.MainFragmentAdapter;
import cn.xylink.mting.ui.dialog.MainAddMenuPop;
import cn.xylink.mting.ui.fragment.BaseMainTabFragment;
import cn.xylink.mting.utils.L;

public class MainActivity extends BaseActivity implements BaseMainTabFragment.OnControllerListener, MainAddMenuPop.OnMainAddMenuListener {

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
    private TAB_ENUM mCurrentTabIndex = TAB_ENUM.TAB_UNREAD;
    public SpeechServiceProxy proxy;
    private SpeechService service;
    private MainFragmentAdapter mTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void preView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setFocusableInTouchMode(false);
    }

    @Override
    protected void initData() {
        TAB_ENUM.TAB_LOVE.setView(mLoveTextView);
        TAB_ENUM.TAB_READED.setView(mReadedTextView);
        TAB_ENUM.TAB_UNREAD.setView(mUnreadTextView);
        mTabAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOffscreenPageLimit(TAB_ENUM.values().length);
        EventBus.getDefault().register(this);
        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service) {
                if (connected) {
                    MainActivity.this.service = service;
                }
            }
        };
        proxy.bind();

    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void onPlay(String id) {
        L.v();
        if (service!=null)
        service.play(id);
    }

    @Override
    public void onDelete(List<String> list) {
        L.v();
    }

    @Override
    public void onAdd() {

    }

    @Override
    public void onArrange() {

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
            , R.id.tv_main_tabar_readed, R.id.tv_main_tabar_unread, R.id.tv_main_tabar_love})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_title_my:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_main_title_search:
                break;
            case R.id.iv_main_title_add:
                MainAddMenuPop pop= new MainAddMenuPop(MainActivity.this, this);
                pop.showAsRight(mAddImageView);
                break;
            case R.id.tv_main_tabar_unread:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_UNREAD);
                break;
            case R.id.tv_main_tabar_readed:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_READED);
                break;
            case R.id.tv_main_tabar_love:
                doAnim(mCurrentTabIndex, TAB_ENUM.TAB_LOVE);
                break;
        }
    }

    private void doAnim(TAB_ENUM currentTab, TAB_ENUM goTab) {
        if (currentTab != goTab) {
            mViewPager.setCurrentItem(goTab.getIndex(),false);
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
    }


    /*
    播放进度回调， event.getFrameIndex 返回的是当前播报的片段索引,
    event.getTextFragments，返回所有的片段集合
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechProgress(SpeechProgressEvent event) {
        L.v(event.getArticle());
    }


    /*
    播放器无内容可播放后，会调用此事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        L.v(event);
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        proxy.unbind();
    }
}
