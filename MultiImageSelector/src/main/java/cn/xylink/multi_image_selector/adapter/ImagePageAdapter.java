package cn.xylink.multi_image_selector.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.multi_image_selector.R;
import cn.xylink.multi_image_selector.bean.Image;

public class ImagePageAdapter extends RecyclerAdapter<Image> {

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int currentIndex = 0;

    public ImagePageAdapter(Context ctx, List<Image> list) {
        super(ctx, list);
    }

    public void setCurentIndex(int _currentIndex){
        this.currentIndex = _currentIndex;
        notifyDataSetChanged();
    }

    /**
     * 设置数据集
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.recyler_page_item_image;
    }

    float newX = 0;
    float oldX = 0;

    @Override
    protected void bindData(RecyclerViewHolder holder, int position, Image item) {
        ImageView iv_image = holder.findViewById(R.id.image);
        Glide.with(mContext).load(item.path).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.img_load_fail).error(R.drawable.img_load_fail).into(iv_image);

        View v_image_bg = holder.findViewById(R.id.v_image_bg);
        if(currentIndex == position)
        {

            v_image_bg.setVisibility(View.VISIBLE);
        }else
        {
            v_image_bg.setVisibility(View.GONE);
        }

    }


}
