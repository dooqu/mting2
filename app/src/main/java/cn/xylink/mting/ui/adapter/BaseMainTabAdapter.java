package cn.xylink.mting.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;

/*
 *tab页列表基类
 *
 * -----------------------------------------------------------------
 * 2019/9/2 15:51 : Create BaseMainTabAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public abstract class BaseMainTabAdapter extends RecyclerView.Adapter<BaseMainTabAdapter.ItemHolder> {
    private static final int NORMAL_VIEW = 0;
    private static final int FOOT_VIEW = 1;
    public static final int TYPE_GONE = 10;
    public static final int TYPE_LOADING = 11;
    public static final int TYPE_END = 12;
    protected List<Article> mData;
    protected OnItemClickListener mOnItemClickListener;
    private int footType = TYPE_GONE;

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == FOOT_VIEW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_foot, viewGroup, false);
            return new ItemHolder(view, i);
        } else {
            return new ItemHolder(onCreateView(viewGroup, i), i);
        }
    }

    public abstract View onCreateView(ViewGroup viewGroup, int i);

    @Override
    public void onBindViewHolder(@NonNull ItemHolder viewHolder, int i) {
        if (getItemViewType(i) == NORMAL_VIEW) {
            onBindView(viewHolder, i);
        } else {
            switch (footType) {
                case TYPE_END:
                    viewHolder.tvFoot.setText("已经到底了~");
                    viewHolder.tvFoot.setVisibility(View.VISIBLE);
                    break;
                case TYPE_GONE:
                    viewHolder.tvFoot.setText("");
                    viewHolder.tvFoot.setVisibility(View.GONE);
                    break;
                case TYPE_LOADING:
                    viewHolder.tvFoot.setText("加载中...");
                    viewHolder.tvFoot.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public abstract void onBindView(ItemHolder viewHolder, int i);

    public void setFootType(int footType) {
        this.footType = footType;
    }

    public int getFootType() {
        return footType;
    }

    @Override
    public int getItemCount() {
        return mData != null && mData.size() > 0 ? mData.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOT_VIEW;
        }
        return NORMAL_VIEW;
    }

    protected String getPercentFormat(double d) {
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(3);//小数点前保留几位
        nf.setMinimumFractionDigits(0);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView ivMore;
        TextView tvTitle;
        TextView tvFrom;
        TextView tvProgress;
        TextView tvFoot;

        public ItemHolder(@NonNull View itemView, int typeView) {
            super(itemView);
            if (typeView == NORMAL_VIEW) {
                ivMore = itemView.findViewById(R.id.iv_unread_item_more);
                tvTitle = itemView.findViewById(R.id.tv_unread_item_title);
                tvFrom = itemView.findViewById(R.id.tv_unread_item_from);
                tvProgress = itemView.findViewById(R.id.tv_unread_item_progress);
            } else {
                tvFoot = itemView.findViewById(R.id.tv_foot);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Article article);

        void onItemMoreClick(Article article);
    }
}
