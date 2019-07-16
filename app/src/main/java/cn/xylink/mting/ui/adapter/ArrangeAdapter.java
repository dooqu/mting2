package cn.xylink.mting.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;

/*
 *整理
 *
 * -----------------------------------------------------------------
 * 2019/7/15 19:42 : Create ArrangeAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ArrangeAdapter extends RecyclerView.Adapter<ArrangeAdapter.ReadedHolder> {
    private Context mContext;
    private static List<Article> mData;
    private OnItemClickListener mOnItemClickListener;

    public ArrangeAdapter(Context context) {
        this.mContext = context;
    }

    public ArrangeAdapter(Context context, OnItemClickListener listener) {
        this.mContext = context;
        this.mOnItemClickListener = listener;
    }

    public ArrangeAdapter(Context context, List<Article> list, OnItemClickListener listener) {
        this.mContext = context;
        this.mData = list;
        this.mOnItemClickListener = listener;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    public void setData(List<Article> list) {
        mData = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArrangeAdapter.ReadedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_arrange, viewGroup, false);
        return new ArrangeAdapter.ReadedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArrangeAdapter.ReadedHolder holder, int position) {
        Article data = mData.get(position);
        holder.tvTitle.setText(data.getTitle());
        holder.tvFrom.setText(TextUtils.isEmpty(data.getSourceName()) ? "其他" : data.getSourceName());
        if (data.getProgress() > 0) {
            holder.tvProgress.setText("已播放：" + getPercentFormat(data.getProgress()));
        } else {
            holder.tvProgress.setText("");
        }
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(data);
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
        TextView tvTitle;
        TextView tvFrom;
        TextView tvProgress;
        CheckBox checkBox;

        public ReadedHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_unread_item_title);
            tvFrom = itemView.findViewById(R.id.tv_unread_item_from);
            tvProgress = itemView.findViewById(R.id.tv_unread_item_progress);
            checkBox = itemView.findViewById(R.id.cb_arrange_item_check);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }
}