package cn.xylink.multi_image_selector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

public class MyVideoView extends VideoView {

    private String absolutePath;
    private String originalPath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    int width;
    int height;
    public MyVideoView(Context context) {
        this(context, null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }


    public void setMeasure(int width, int height) {
        this.width = width;
        this.height = height;
    }

    //重写测量方法，让视频按照布局显示
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mVideoWidth = dm.widthPixels;
        int mVideoHeight = dm.heightPixels / 2;

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth * height > width * mVideoHeight) {
            height = width * mVideoHeight / mVideoWidth;
        }else if(mVideoWidth * height < width * mVideoHeight){
            width = height * mVideoWidth / mVideoHeight;
        }

        setMeasuredDimension(width, height);

    }

}
