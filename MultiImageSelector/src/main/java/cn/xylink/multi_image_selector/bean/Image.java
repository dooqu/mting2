package cn.xylink.multi_image_selector.bean;

import android.text.TextUtils;

/**
 * 图片实体
 * Created by Nereo on 2015/4/7.
 */
public class Image  implements java.io.Serializable{
    public String path;
    public String name;
    public long time;
    private int width;
    private int height;
    public boolean isCheck;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private String pictureType;
    private String duration;

    public String getPictureType() {
        if(pictureType == null || TextUtils.isEmpty(pictureType) || pictureType.equals("null"))
        {
            return "1";
        }
        return pictureType;
    }

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }

    public Image(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return TextUtils.equals(this.path, other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
