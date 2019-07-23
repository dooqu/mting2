package cn.xylink.mting.bean;

import cn.xylink.mting.base.BaseRequest;

/**
 * Created by wjn on 2019/3/5.
 */

public class UploadHeadImgInfo extends BaseRequest {
    /**
     * imageUrl : http://10.1.1.9:7070/resource/M00/18/88/rBMAY1xik_KAb-qeAAMjN9pP-AA08.jpeg
     */

    private String imageUrl;

    private String headImg;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
