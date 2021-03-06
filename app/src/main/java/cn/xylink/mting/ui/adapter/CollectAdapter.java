package cn.xylink.mting.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.speech.data.SpeechList;
import cn.xylink.mting.utils.L;

/*
 *收藏
 *
 * -----------------------------------------------------------------
 * 2019/7/15 10:50 : Create CollectAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class CollectAdapter extends BaseMainTabAdapter {
    private Context mContext;
    private Article mCurrent = SpeechList.getInstance().getCurrent();
    private int mCurrentPosition = 0;

    public CollectAdapter(Context context, List<Article> list, UnreadAdapter.OnItemClickListener listener) {
        this.mContext = context;
        this.mData = list;
        this.mOnItemClickListener = listener;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    public void refreshData() {
//        mCurrent = SpeechList.getInstance().getCurrent();
//        notifyDataSetChanged();
    }

    public void setData(List<Article> list) {
        if (mData == null) {
            mData = list;
        } else {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (mData != null)
            mData.clear();
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_unread, viewGroup, false);
        return view;
    }

    @Override
    public void onBindView(@NonNull ItemHolder holder, int position) {
        Article data = mData.get(position);
        holder.tvTitle.setText(data.getTitle());
        holder.tvFrom.setText(TextUtils.isEmpty(data.getSourceName()) ? "其他" : data.getSourceName());
        if (data.getProgress() > 0) {
            holder.tvProgress.setText("已播放：" + getPercentFormat(data.getProgress()));
        } else {
            holder.tvProgress.setText("");
        }
//        if (mCurrent != null ? mCurrent.getArticleId().equals(data.getArticleId()) : false) {
//            mCurrentPosition = position;
//            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.c488def));
//            holder.tvFrom.setTextColor(mContext.getResources().getColor(R.color.c488def));
//            holder.tvProgress.setTextColor(mContext.getResources().getColor(R.color.c488def));
//        } else {
//            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.c333333));
//            holder.tvFrom.setTextColor(mContext.getResources().getColor(R.color.c999999));
//            holder.tvProgress.setTextColor(mContext.getResources().getColor(R.color.c999999));
//        }
        holder.ivMore.setOnClickListener(v -> {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemMoreClick(data);
        });
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(data);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemMoreClick(data);
            return false;
        });
    }

    public List<Article> getArticleList() {
        return mData;
    }

}