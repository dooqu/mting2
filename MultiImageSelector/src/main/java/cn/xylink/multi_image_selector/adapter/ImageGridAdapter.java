package cn.xylink.multi_image_selector.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.xylink.multi_image_selector.R;
import cn.xylink.multi_image_selector.ViewPagerActivity;
import cn.xylink.multi_image_selector.bean.Image;
import cn.xylink.multi_image_selector.config.PictureMimeType;
import cn.xylink.multi_image_selector.utils.DateUtils;
import cn.xylink.multi_image_selector.utils.SharedPreHelper;
import cn.xylink.multi_image_selector.utils.StringUtils;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Activity mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    final int mGridWidth;
    final int mDefalutCount;

    public void setZoomAnim(boolean isAnim) {
        this.zoomAnim = isAnim;
    }


    private ImageGridAdapter.ImageGridCallBack callBack;

    public void setCallBack(ImageGridAdapter.ImageGridCallBack _callBack) {
        this.callBack = _callBack;
    }


    public ImageGridAdapter(Activity context, boolean showCamera, int column) {
        mContext = context;
        mDefalutCount = (int) SharedPreHelper.getInstance(context).getSharedPreference(SharedPreHelper.SharedAttribute.SELECT_COUNT, 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;
    }

    /**
     * 显示选择指示器
     *
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param image
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        mSelectedImages.clear();
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path) {
        if (mImages != null && mImages.size() > 0) {
            for (Image image : mImages) {
                if (image.path.equalsIgnoreCase(path)) {
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     *
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Image data = getItem(i);
        if (isShowCamera()) {
            if (i == 0) {
                view = mInflater.inflate(R.layout.mis_list_item_camera, viewGroup, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callBack.imageseleected(data, i);
                    }
                });

                return view;
            }
        }


        final ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.mis_list_item_image, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        zoomAnim = true;

        if (holder != null) {
            holder.bindData(data);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImages.size() != mDefalutCount || mSelectedImages.contains(data)) {
                    final Intent mIntent = new Intent();
                    mIntent.setClass(mContext, ViewPagerActivity.class);
                    mIntent.putExtra(ViewPagerActivity.SELECTED_IMAGES, (Serializable) mSelectedImages);
                    mIntent.putExtra(ViewPagerActivity.SELECT_INDEX, data);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeScaleUpAnimation(holder.image,
                            holder.image.getWidth() * 2, holder.image.getHeight() * 2, -500, 780);

                    mContext.startActivity(mIntent,
                            compat.toBundle());

                }
//                callBack.imageseleected(data, i);
//                if (mSelectedImages.size() != mDefalutCount) {
//                    if (mSelectedImages.contains(data)) {
//                        // 设置选中状态
//                        zoom(holder.image);
//                    } else {
//                        // 未选择
//                        disZoom(holder.image);
//                    }
//                }
            }
        });


        holder.indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.imageseleected(data, i);
                if (mSelectedImages.size() != mDefalutCount) {
                    if (mSelectedImages.contains(data)) {
                        // 设置选中状态
                        zoom(holder.image);
                        data.isCheck = true;
                    } else {
                        // 未选择
                        data.isCheck = false;
                        disZoom(holder.image);
                    }
                }
            }
        });

        return view;
    }


    /**
     * 是否是长图
     *
     * @param image
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(Image image) {
        if (null != image) {
            int width = image.getWidth();
            int height = image.getHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    class ViewHolder {
        ImageView image;
        ImageView indicator;
        ImageView mask;
        TextView tvLong;
        TextView tvDuration;
        View mask2;

        ViewHolder(View view) {
            image = view.findViewById(R.id.image);
            indicator = view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            tvLong = view.findViewById(R.id.tv_long_chart);
            tvDuration = view.findViewById(R.id.tv_duration);
            mask2 = view.findViewById(R.id.mask2);
            view.setTag(this);
        }

        void bindData(final Image data) {
            if (data == null) return;
            // 处理单选和多选状态

            if (showSelectIndicator) {
                mask.setBackgroundResource(R.drawable.image_placeholder);
                indicator.setVisibility(View.VISIBLE);
                if (mSelectedImages.contains(data)) {
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.mis_btn_selected);
                    mask.setVisibility(View.VISIBLE);
                    if (mSelectedImages.size() != mDefalutCount) {
                        zoom(mask);
                    }
                } else {
                    // 未选择
                    indicator.setImageResource(R.drawable.mis_btn_unselected);
                    if (mSelectedImages.size() != mDefalutCount)
                        disZoom(mask);
                    mask.setVisibility(View.GONE);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }
//            }


            boolean isLongImage = isLongImg(data);
            tvLong.setVisibility(isLongImage ? View.VISIBLE : View.GONE);
            String pictureType = data.getPictureType();
            final int mediaMimeType = PictureMimeType.isPictureType(pictureType);
            Log.e(ImageGridAdapter.class.getName(), "getview mediaMimeType:" + mediaMimeType);
            if (mediaMimeType == PictureMimeType.TYPE_VIDEO) {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(tvDuration, drawable, 0);
                tvDuration.setVisibility(mediaMimeType == PictureMimeType.TYPE_VIDEO
                        ? View.VISIBLE : View.GONE);
                long duration = Long.parseLong(data.getDuration());
                tvDuration.setText(DateUtils.timeParse(duration));
            } else {
                tvDuration.setVisibility(View.GONE);
            }
            if (mediaMimeType == PictureMimeType.ofAudio()) {
                image.setImageResource(R.drawable.audio_placeholder);
            } else {
                File imageFile = new File(data.path);
                if (imageFile.exists()) {
                    // 显示图片
//                    Picasso.with(mContext)
//                            .load(imageFile)
//                            .placeholder(R.drawable.mis_default_error)
//                            .tag(MultiImageSelectorFragment.TAG)
//                            .resize(mGridWidth, mGridWidth)
//                            .centerCrop()
//                            .into(image);
                } else {
                    image.setImageResource(R.drawable.mis_default_error);
                }

//                RequestOptions options = new RequestOptions();
//                if (overrideWidth <= 0 && overrideHeight <= 0) {
//                    options.sizeMultiplier(sizeMultiplier);
//                } else {
//                    options.override(overrideWidth, overrideHeight);
//                }
//                options.diskCacheStrategy(DiskCacheStrategy.ALL);
//                options.centerCrop();
//                options.placeholder(R.drawable.image_placeholder);
                Glide.with(mContext)
                        .asBitmap()
                        .placeholder(R.drawable.mis_default_error)
                        .load(data.path)
                        .centerCrop()
                        .into(image);
            }
            if (mDefalutCount == mSelectedImages.size()) {
                if (!mSelectedImages.contains(data)) {
                    mask2.setVisibility(View.VISIBLE);
                } else {
                    mask2.setVisibility(View.GONE);
                }
            } else {
                mask2.setVisibility(View.GONE);

            }
        }
    }

    private boolean zoomAnim;
    private final static int DURATION = 450;

    private void zoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1f, 1.12f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    private void disZoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1.12f, 1f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    public interface ImageGridCallBack {
        void imageseleected(Image image, int postion);
    }

}
