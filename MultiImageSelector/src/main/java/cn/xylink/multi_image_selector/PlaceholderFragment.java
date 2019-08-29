package cn.xylink.multi_image_selector;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import cn.xylink.multi_image_selector.image.IntensifyImage;
import cn.xylink.multi_image_selector.image.IntensifyImageView;
import cn.xylink.multi_image_selector.model.MediaConstant;
import cn.xylink.multi_image_selector.view.CustomViewPager;

public class PlaceholderFragment extends Fragment implements View.OnClickListener {

    String name, url;
    int pos;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_IMG_TITLE = "image_title";
    private static final String ARG_IMG_URL = "image_url";

    private IntensifyImageView mIvImage;

    private MediaController mMediaController;
//    private IjkVideoView mVideoView;
    private ImageView ivPlay;
    private ImageView ivGif;

    private CustomViewPager viewPager;
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.pos = args.getInt(ARG_SECTION_NUMBER);
        this.name = args.getString(ARG_IMG_TITLE);
        this.url = args.getString(ARG_IMG_URL);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, String name, String url,CustomViewPager viewPager) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setViewPager(viewPager);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_IMG_TITLE, name);
        args.putString(ARG_IMG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public CustomViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(CustomViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public PlaceholderFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!url.endsWith(MediaConstant.MP4) && !url.endsWith(MediaConstant.GIF)) {
            mIvImage.setScaleType(IntensifyImage.ScaleType.FIT_AUTO);
            mIvImage.setImage(new File(url));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("PlaceholderFragment", "onStop url>> " + url);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_page_item_image, container, false);
        mIvImage = rootView.findViewById(R.id.image);
        ivPlay = rootView.findViewById(R.id.iv_play);
        ivGif = rootView.findViewById(R.id.iv_gif);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (url.endsWith(MediaConstant.MP4)) {

            mIvImage.setVisibility(View.GONE);
        } else if (url.endsWith(".gif")){
            ivGif.setVisibility(View.VISIBLE);
            mIvImage.setVisibility(View.GONE);
            Glide.with(this).load(url).placeholder(R.drawable.mis_default_error).listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    if (resource instanceof GifDrawable) {
                        //加载一次
                    }
                    return false;
                }


            }).into(ivGif);
        }else {
            mIvImage.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.GONE);
        }
        registerListenter();

        if(viewPager != null)
        {
            if(viewPager.getCurrentItem() == 2){
                viewPager.setCanScroll(false);
            }
        }
    }

    public void registerListenter()
    {
        ivPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if(id == R.id.iv_play){
//            mVideoView.start();
//            ivOne.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
        }
    }
}