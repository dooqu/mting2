package cn.xylink.mting.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.model.Article;

/*
 *待读
 *
 * -----------------------------------------------------------------
 * 2019/7/8 15:13 : Create UnreadAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class UnreadAdapter extends RecyclerView.Adapter<UnreadAdapter.UnreadHolder> {
    private Context mContext;
    private List<Article> mData;
    private OnItemClickListener mOnItemClickListener;
    private int mCurrentPosition = 0;

    public UnreadAdapter(Context context, List<Article> list, OnItemClickListener listener) {
        this.mContext = context;
        this.mData = list;
        this.mOnItemClickListener = listener;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    public void setCurrentPosition(int position){
        this.mCurrentPosition = position;
    }


    @NonNull
    @Override
    public UnreadHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_unread, viewGroup, false);
        return new UnreadHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnreadHolder holder, int position) {
        Article data = mData.get(position);
        holder.tvTitle.setText(data.getTitle());
//        holder.tvFrom.setText(data.getTextBody());
//        holder.tvProgress.setText(data.);
        if (mCurrentPosition ==position){
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.c488def));
            holder.tvFrom.setTextColor(mContext.getResources().getColor(R.color.c488def));
            holder.tvProgress.setTextColor(mContext.getResources().getColor(R.color.c488def));
        }else {
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
    }

    public List<Article> getArticleList() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class UnreadHolder extends RecyclerView.ViewHolder {
        ImageView ivMore;
        TextView tvTitle;
        TextView tvFrom;
        TextView tvProgress;

        public UnreadHolder(@NonNull View itemView) {
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