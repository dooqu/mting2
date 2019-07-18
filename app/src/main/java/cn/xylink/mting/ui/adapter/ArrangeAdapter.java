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
import cn.xylink.mting.utils.L;

/*
 *整理
 *
 * -----------------------------------------------------------------
 * 2019/7/15 19:42 : Create ArrangeAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ArrangeAdapter extends RecyclerView.Adapter<ArrangeAdapter.ReadedHolder> {
    private Context mContext;
    private List<Article> mData = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private int mTabType;
    private int mSelectCount = 0;
    private int mUnPlayedTotal = 0;
    private int mUnPlayedCount = 0;

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

    public void setTabType(int tabType) {
        this.mTabType = tabType;
    }

    public void setData(List<Article> list) {
        if (mData == null) {
            mData = list;
        } else {
            mData.addAll(list);
            if (mOnItemClickListener != null)
                mOnItemClickListener.checkChanged(mSelectCount, mUnPlayedTotal > 0 ? mUnPlayedCount == mUnPlayedTotal : false);
        }
        mData.get(0).setProgress(0.5f);
        mUnPlayedTotal = 0;
        for (Article article : mData) {
            if (article.getProgress() < 1.0f)
                mUnPlayedTotal++;
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (mData != null)
            mData.clear();
    }

    @NonNull
    @Override
    public ArrangeAdapter.ReadedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_arrange, viewGroup, false);
        ReadedHolder holder = new ReadedHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArrangeAdapter.ReadedHolder holder, int position) {
        Article data = mData.get(position);
        holder.checkBox.setChecked(data.isChecked());
        holder.tvTitle.setText(data.getTitle());
        holder.tvFrom.setText(TextUtils.isEmpty(data.getSourceName()) ? "其他" : data.getSourceName());
        if (data.getProgress() > 0 && mTabType != 2) {
            holder.tvProgress.setText("已播放：" + getPercentFormat(data.getProgress()));
        } else {
            holder.tvProgress.setText("");
        }
        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            notifCheckChange(holder, data);
        });
        holder.checkBox.setOnClickListener(v -> {
            notifCheckChange(holder, data);
        });
    }

    private void notifCheckChange(@NonNull ReadedHolder holder, Article data) {
        data.setChecked(holder.checkBox.isChecked());
        if (!holder.checkBox.isChecked()) {
            if (mSelectCount > 0)
                mSelectCount--;
            if (data.getProgress() < 1.0f && mUnPlayedCount > 0)
                mUnPlayedCount--;

        } else {
            mSelectCount++;
            if (mSelectCount > getItemCount())
                mSelectCount = getItemCount();
            if (data.getProgress() < 1.0f)
                mUnPlayedCount++;
            if (mUnPlayedCount > mUnPlayedTotal)
                mUnPlayedCount = mUnPlayedTotal;
        }
        L.v(mUnPlayedTotal);
        L.v(mUnPlayedCount);
        L.v(data.getProgress());
        if (mOnItemClickListener != null)
            mOnItemClickListener.checkChanged(mSelectCount, mUnPlayedTotal > 0 ? mUnPlayedCount == mUnPlayedTotal : false);
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

    public String getSelectItemArticleID(){
        StringBuffer buffer = new StringBuffer();
        for (Article article:mData){
            if (article.isChecked())
            buffer.append(article.getArticleId()+",");
        }
        return buffer.toString();
    }

    public String getSelectItemID(){
        StringBuffer buffer = new StringBuffer();
        for (Article article:mData){
            if (article.isChecked())
            buffer.append(article.getId()+",");
        }
        return buffer.toString();
    }

    public void selectAllItem(boolean isCheck) {
        for (Article a : mData) {
            a.setChecked(isCheck);
        }
        if (isCheck) {
            mSelectCount = getItemCount();
            mUnPlayedCount = mUnPlayedTotal;
        } else {
            mUnPlayedCount = 0;
            mSelectCount = 0;
        }
        if (mOnItemClickListener != null)
            mOnItemClickListener.checkChanged(mSelectCount, mUnPlayedTotal > 0 ? mUnPlayedCount == mUnPlayedTotal : false);
        notifyDataSetChanged();
    }

    public void selectAllUnreadItem(boolean isCheck) {
        int count = 0;
        for (Article a : mData) {
            if (a.getProgress() < 1.0f) {
                a.setChecked(isCheck);
            }
            if (a.isChecked())
                count++;
        }
        if (isCheck)
            mUnPlayedCount = mUnPlayedTotal;
        else
            mUnPlayedCount = 0;
        mSelectCount = count;
        if (mOnItemClickListener != null)
            mOnItemClickListener.checkChanged(mSelectCount, mUnPlayedTotal > 0 ? mUnPlayedCount == mUnPlayedTotal : false);
        notifyDataSetChanged();
    }

    class ReadedHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvFrom;
        TextView tvProgress;
        CheckBox checkBox;

        public ReadedHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_arrange_item_title);
            tvFrom = itemView.findViewById(R.id.tv_arrange_item_from);
            tvProgress = itemView.findViewById(R.id.tv_arrange_item_progress);
            checkBox = itemView.findViewById(R.id.cb_arrange_item_check);
        }
    }

    public interface OnItemClickListener {
        void checkChanged(int selectCount, boolean isSelectAllUnplay);
    }
}