package cn.xylink.mting.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.utils.DensityUtil;


/*
 *文章段落adapter
 *
 * -----------------------------------------------------------------
 * 2019/8/2 11:42 : Create SearchArticleDetailAdapter.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SearchArticleDetailAdapter extends RecyclerView.Adapter<SearchArticleDetailAdapter.SearchArticleDetailHolder> {
    private Context mContext;
    private List<String> mData = new ArrayList<>();

    public SearchArticleDetailAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmData(List<String> mData) {
        if (mData != null)
            this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchArticleDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(mContext.getResources().getColor(R.color.c333333));
//        textView.setTextSize(mContext.getResources().getDimension(R.dimen.text_16));
        textView.setTextSize(16);
        textView.setLineSpacing(0,1.7f);
        return new SearchArticleDetailHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchArticleDetailHolder searchArticleDetailHolder, int i) {
        String str = mData.get(i);
        if (!TextUtils.isEmpty(str))
            searchArticleDetailHolder.tvContact.setText(str);
        if (i==0){
            searchArticleDetailHolder.tvContact.setTextColor(mContext.getResources().getColor(R.color.c999999));
        }else {
            searchArticleDetailHolder.tvContact.setTextColor(mContext.getResources().getColor(R.color.c333333));
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    class SearchArticleDetailHolder extends RecyclerView.ViewHolder {

        TextView tvContact;

        public SearchArticleDetailHolder(@NonNull TextView itemView) {
            super(itemView);
            tvContact = itemView;
        }
    }
}
