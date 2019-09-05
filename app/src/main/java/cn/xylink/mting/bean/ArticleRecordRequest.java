package cn.xylink.mting.bean;

import java.util.Date;
import java.util.List;

import cn.xylink.mting.base.BaseRequest;

public class ArticleRecordRequest extends BaseRequest {

    List<ArticleRecord> readDate;

    public static class ArticleRecord {
        String articleId;
        java.util.Date date;
        long time;

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

        public String getArticleId() {
            return articleId;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }
}
