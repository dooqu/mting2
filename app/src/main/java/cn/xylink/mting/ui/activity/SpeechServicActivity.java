package cn.xylink.mting.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.model.Article;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.speech.event.SpeechErrorEvent;
import cn.xylink.mting.speech.event.SpeechProgressEvent;
import cn.xylink.mting.speech.SpeechServiceProxy;
import cn.xylink.mting.speech.event.SpeechStartEvent;
import cn.xylink.mting.speech.event.SpeechStopEvent;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class SpeechServicActivity extends Activity {

    /*
    start SpeechListAdapter
    ################################################################################################
     */
    public static class SpeechListAdapter extends RecyclerView.Adapter<SpeechListAdapter.SpeechListViewHolder> {

        protected SpeechService service;

        public SpeechListAdapter(SpeechService service) {
            this.service = service;
        }

        /*
        全部更新
         */
        public void refresh() {
            this.notifyDataSetChanged();
        }

        /*
        更新其中一条，可以作为进度更新来使用
         */
        public void update(Article ar) {
            List<Article> list = service.getSpeechList();

            for (int i = 0, size = list.size(); i < size; i++) {
                if (ar == list.get(i)) {
                    this.notifyItemChanged(i);
                    break;
                }
            }
        }

        @NonNull
        @Override
        public SpeechListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_speech_example, viewGroup, false);
            return new SpeechListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SpeechListViewHolder speechListViewHolder, int i) {

            speechListViewHolder.artTitle.setText(SpeechList.getInstance().getArticleList().get(i).getTitle());
            Article currentArticle = service.getSpeechList().get(i);

            //绑定条目的时候，判定条目的状态
            if (currentArticle != null && service.getSelected() == currentArticle) {
                //设定某个条目的播放状态和进度
                speechListViewHolder.artStatus.setText("正在播放:" + service.getProgress());
                //高亮条目背景颜色
                speechListViewHolder.row.setBackgroundColor(Color.GRAY);
            }
            else {
                speechListViewHolder.artStatus.setText("");
                speechListViewHolder.row.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return service.getSpeechList().size();
        }

        public class SpeechListViewHolder extends RecyclerView.ViewHolder {
            //标题
            TextView artTitle;
            //状态
            TextView artStatus;
            //容器，用于设定bgcolor
            LinearLayout row;

            public SpeechListViewHolder(View view) {
                super(view);
                artTitle = (TextView) view.findViewById(R.id.artTitle);

                artStatus = (TextView) view.findViewById(R.id.artStatus);
                row = (LinearLayout) view.findViewById(R.id.row);

                //给条目添加一个点击事件，用于播放对应的条目
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //SpeechService
                        final SpeechService service = SpeechListAdapter.this.service;
                        // 点击的位置
                        int pos = getAdapterPosition();
                        //点击位置转换成对应的条目对象
                        Article article = service.getSpeechList().get(pos);
                        //通过articleid，来查找播放
                        service.play(article.getArticleId());
                    }
                });
            }
        }
    }
    // end internal class SpeechListAdapter
    //#############################################################################################

    //条目列表
    RecyclerView listArticles;

    /*
    服务代理，使用proxy连接服务，获得SpeechService对象
     */
    SpeechServiceProxy proxy;

    /*
    获得的SpeechService对象，缓冲
     */
    SpeechService service;

    /*
    数据列表的Adapter
     */
    SpeechListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_speech_example);
        listArticles = (RecyclerView) findViewById(R.id.listArticles);

        /*
        一个匿名实现类， 用于连接SpeechService，定义onConnect成功后，获取到service对象，之后可以可以调用service的各种控制方法；
        service方法没有做多线程同步处理，尽量都在主线程调用
         */
        proxy = new SpeechServiceProxy(this) {
            @Override
            protected void onConnected(boolean connected, SpeechService service) {
                if (connected) {
                    SpeechServicActivity.this.service = service;

                    //服务连接成功后，再初始化Adapter，SpeechService要作为adapter的参数，因为Adapter内部的数据列表来自SpeechService的getSpeechList
                    adapter = new SpeechListAdapter(service);

                    //最后设定setAdapter
                    listArticles.setAdapter(adapter);
                }
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        listArticles.setLayoutManager(linearLayoutManager);
        listArticles.setItemAnimator(null);
        listArticles.addItemDecoration(new SpaceItemDecoration());

        //注册订阅者，用于接收开始播放事件(onSpeechStart)、播放结束事件(onSpeechStop)、播放进度事件(onSpeechProgress)、播放错误事件(onSpeechError)
        EventBus.getDefault().register(this);

        //绑定服务，开始连接
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


    /*
    某一个文章准备开始播报的时候被调用, event.getArticle返回的是文章对象
    注意，该事件被调用并不意味着真正的tts开始播放，在此时间调用后，还有获取Article正文缓冲、以及tts转换等缓冲时间，
    真正的播放开始要在progress的0进度开始
    该事件调用后，可以切换播放器的标题等操作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStart(SpeechStartEvent event) {
        adapter.refresh();
        Log.d("xylink", "onSpeechStart:" + event.getArticle().getTitle());
    }


    /*
    播放进度回调， event.getFrameIndex 返回的是当前播报的片段索引,
    event.getTextFragments，返回所有的片段集合
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechProgress(SpeechProgressEvent event) {
        adapter.update(event.getArticle());
        Log.d("xylink", "onSpeechProgress: " + event.getArticle().getTitle() + "," + event.getTextFragments().get(event.getFrameIndex()));
    }


    /*
    播放器无内容可播放后，会调用此事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechStop(SpeechStopEvent event) {
        Log.d("xylink", "onSpeechStop");
        adapter.refresh();
    }


    /*
    播放遇到错误，会调用此事件，比如网络错误等
    event.getArticle()指示当前错误的文章
    event.getErrorCode()指示错误码
    event.getMessage()指示错误提示
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeechError(SpeechErrorEvent event) {
        Log.d("xylink", "onSpeechError:" + event.getArticle().getTitle());

    }


    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int space = 10;
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = space;
                outRect.top = 0;
            }
            else {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = space;
                outRect.top = space * 2 / 3;
            }
        }
    }
}
