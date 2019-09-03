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
 *待读
 *
 * -----------------------------------------------------------------
 * 2019/7/8 15:13 : Create UnreadAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class UnreadAdapter extends BaseMainTabAdapter {
    private Context mContext;
    private Article mCurrent = SpeechList.getInstance().getCurrent();
    private int mCurrentPosition = 0;

    public UnreadAdapter(Context context, List<Article> list, OnItemClickListener listener) {
        this.mContext = context;
        this.mData = list;
        this.mOnItemClickListener = listener;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    public void refreshData() {
        mCurrent = SpeechList.getInstance().getCurrent();
        mData = SpeechList.getInstance().getArticleList();
        notifyDataSetChanged();
    }

    public void setProgress(Article pro) {
        L.v(pro);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            mData.replaceAll(article -> article.getArticleId().equals(data.getArticleId())?data:article);
//        }else {
//            for (int i = 0 ; i <mData.size();i++){
//                if (mData.get(i).getArticleId().equals(data.getArticleId())){
//                    mData.set(i,data);
//                    return;
//                }
//            }
//        }
        notifyItemChanged(mCurrentPosition);
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
        if (mCurrent != null ? mCurrent.getArticleId().equals(data.getArticleId()) : position == 0) {
            mCurrentPosition = position;
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.c488def));
            holder.tvFrom.setTextColor(mContext.getResources().getColor(R.color.c488def));
            holder.tvProgress.setTextColor(mContext.getResources().getColor(R.color.c488def));
        } else {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.c333333));
            holder.tvFrom.setTextColor(mContext.getResources().getColor(R.color.c999999));
            holder.tvProgress.setTextColor(mContext.getResources().getColor(R.color.c999999));
        }
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