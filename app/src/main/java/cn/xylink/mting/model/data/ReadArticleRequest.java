package cn.xylink.mting.model.data;

import cn.xylink.mting.base.BaseRequest;

public class ReadArticleRequest extends BaseRequest {
    String articleId;
    int read;
    float progress;

    public void setArticleId(String articleId)
    {
        this.articleId = articleId;
    }

    public String getArticleId()
    {
        return this.articleId;
    }


    public void setRead(int read)
    {
        this.read = read;
    }

    public int getRead()
    {
        return this.read;
    }

    public void setProgress(float progress)
    {
        this.progress = progress;
    }

    public float getProgress()
    {
        return this.progress;
    }
}
