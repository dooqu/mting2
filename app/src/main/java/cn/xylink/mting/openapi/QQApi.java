package cn.xylink.mting.openapi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import cn.xylink.mting.common.Const;
import cn.xylink.mting.utils.LogUtils;

/**
 * Created by liuhe on 2018/10/31.
 */

public class QQApi {

    private static final int THUMB_SIZE = 150;
    private static Tencent mTencent;

    public static void init(Context context) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Const.QQ_ID, context);
        }
    }

    public static Tencent getInstance() {
        return mTencent;
    }

    public static void shareQQ(final Activity activity, String shareUrl, String bmp, String title, String des) {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, bmp);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQQ(activity, params, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {

                    }

                    @Override
                    public void onError(UiError uiError) {
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });


    }

    public static void shareSpace(final Activity activity, String shareUrl, String picture, String title, String des) {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
        ArrayList<String> path_arr = new ArrayList<>();
        if (!TextUtils.isEmpty(picture)) {
            path_arr.add(picture);
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, path_arr);

        Bundle bundle2 = new Bundle();
        bundle2.putString(QzonePublish.HULIAN_EXTRA_SCENE, "");
        bundle2.putString(QzonePublish.HULIAN_CALL_BACK, "");

        params.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, bundle2);
        mTencent.shareToQzone(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {
                LogUtils.e(uiError.errorMessage);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
