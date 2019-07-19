package cn.xylink.mting.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.bean.SearchResultInfo;

/*
 *搜索
 *
 * -----------------------------------------------------------------
 * 2019/7/18 15:13 : Create SearchAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ReadedHolder> {
    private Context mContext;
    private List<SearchResultInfo> mData = new ArrayList<>();
    private SearchAdapter.OnItemClickListener mOnItemClickListener;

    public SearchAdapter(Context context) {
        this.mContext = context;
    }

    public SearchAdapter(Context context, SearchAdapter.OnItemClickListener listener) {
        this.mContext = context;
        this.mOnItemClickListener = listener;
    }

    public SearchAdapter(Context context, List<SearchResultInfo> list, SearchAdapter.OnItemClickListener listener) {
        this.mContext = context;
        this.mData = list;
        this.mOnItemClickListener = listener;
    }

    public void setData(List<SearchResultInfo> list) {
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
    public SearchAdapter.ReadedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search, viewGroup, false);
        return new SearchAdapter.ReadedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ReadedHolder holder, int position) {
        SearchResultInfo data = mData.get(position);
        holder.tvTitle.setText(TextUtils.isEmpty(data.getTitle()) ? "" : Html.fromHtml(data.getTitle()));
        holder.tvContact.setText(TextUtils.isEmpty(data.getContent()) ? "" : Html.fromHtml(data.getContent()));

        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(data);
        });
    }

    public List<SearchResultInfo> getArticleList() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ReadedHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvContact;

        public ReadedHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_search_item_title);
            tvContact = itemView.findViewById(R.id.tv_search_item_contact);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SearchResultInfo article);
    }
}