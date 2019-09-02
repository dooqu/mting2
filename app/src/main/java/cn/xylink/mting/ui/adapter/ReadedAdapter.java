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
 *已读
 *
 * -----------------------------------------------------------------
 * 2019/7/10 14:53 : Create ReadedAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ReadedAdapter extends RecyclerView.Adapter<ReadedAdapter.ReadedHolder> {
    private Context mContext;
    private List<Article> mData;
    private UnreadAdapter.OnItemClickListener mOnItemClickListener;
    private Article mCurrent = SpeechList.getInstance().getCurrent();
    private int mCurrentPosition = 0;

    public ReadedAdapter(Context context, List<Article> list, UnreadAdapter.OnItemClickListener listener) {
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

    public void setProgress(Article pro) {
        L.v(pro.getProgress());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mData.replaceAll(article -> article.getArticleId().equals(pro.getArticleId()) ? pro : article);
        } else {
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).getArticleId().equals(pro.getArticleId())) {
                    mData.set(i, pro);
                    notifyItemChanged(i);
                    L.v(i);
                    L.v(mCurrentPosition);
                    return;
                }
            }
        }
//        notifyItemChanged(mCurrentPosition);
    }


    @NonNull
    @Override
    public ReadedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_unread, viewGroup, false);
        return new ReadedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadedHolder holder, int position) {
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

    public static String getPercentFormat(double d) {
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(3);//小数点前保留几位
        nf.setMinimumFractionDigits(0);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ReadedHolder extends RecyclerView.ViewHolder {
        ImageView ivMore;
        TextView tvTitle;
        TextView tvFrom;
        TextView tvProgress;

        public ReadedHolder(@NonNull View itemView) {
            super(itemView);
            ivMore = itemView.findViewById(R.id.iv_unread_item_more);
            tvTitle = itemView.findViewById(R.id.tv_unread_item_title);
            tvFrom = itemView.findViewById(R.id.tv_unread_item_from);
            tvProgress = itemView.findViewById(R.id.tv_unread_item_progress);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Article article);

        void onItemMoreClick(Article article);
    }
}