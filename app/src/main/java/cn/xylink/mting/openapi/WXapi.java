package cn.xylink.mting.openapi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.net.URL;

import cn.xylink.mting.R;
import cn.xylink.mting.common.Const;

/**
 * Created by liuhe on 2018/10/24.
 */

public class WXapi {

    private static final int THUMB_SIZE = 150;
    private static final int MAX_PIC_SIZE = 25000;
    private static final int MAX_TITLE_SIZE = 18;
    private static final int MAX_DES_SIZE = 19;
    private static IWXAPI wxapi;

    public static void init(Context context) {
        if (wxapi == null) {
            wxapi = WXAPIFactory.createWXAPI(context, Const.WX_ID, false);
        }
    }

    public static IWXAPI getInstance() {
        return wxapi;
    }


    public static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    public static void shareWx(final Activity activity, final String url, final String pic, final String title, final String description) {
        new Thread() {
            @Override
            public void run() {
                WXWebpageObject webpage = new WXWebpageObject();
                String webUrl = url;
                if (TextUtils.isEmpty(webUrl)) {
                    webUrl = "http://www.qq.com";
                }
                Bitmap bmp = null;
                try {
                    if (!TextUtils.isEmpty(pic)) {
                        bmp = BitmapFactory.decodeStream(new URL(pic).openStream());
                    } else {
                        bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                webpage.webpageUrl = webUrl;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                if (title.length() > MAX_TITLE_SIZE) {
                    msg.title = title.substring(0, MAX_TITLE_SIZE-3) + "...";
                } else {
                    msg.title = title;
                }
                if (description.length() > MAX_DES_SIZE) {
                    msg.description = description.substring(0, MAX_DES_SIZE-3) + "...";
                } else {
                    msg.description = description;
                }
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true);
                if (msg.thumbData.length > MAX_PIC_SIZE) {
                    thumbBmp.recycle();
                    bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
                    thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                    bmp.recycle();
                    msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true);
                }
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = WXapi.buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                WXapi.getInstance().sendReq(req);
            }
        }.start();

    }


    public static void sharePyq(final Activity activity, final String url, final String pic, final String title, final String description) {
        new Thread() {
            @Override
            public void run() {
                WXWebpageObject webpage = new WXWebpageObject();
                String webUrl;
                if (TextUtils.isEmpty(url)) {
                    webUrl = "http://www.qq.com";
                } else {
                    webUrl = url;
                }
                Bitmap bmp = null;
                try {
                    if (!TextUtils.isEmpty(pic)) {
                        bmp = BitmapFactory.decodeStream(new URL(pic).openStream());
                    } else {
                        bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                webpage.webpageUrl = webUrl;
                WXMediaMessage msg = new WXMediaMessage(webpage);
//                if (title.length() > 18) {
//                    msg.title = title.substring(0, 15) + "...";
//                } else {
                    msg.title = title;
//                }
//                if (description.length() > 18) {
//                    msg.description = description.substring(0, 15) + "...";
//                } else {
                    msg.description = description;
//                }
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true);
                if (msg.thumbData.length > MAX_PIC_SIZE) {
                    thumbBmp.recycle();
                    bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
                    thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                    bmp.recycle();
                    msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true);
                }
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = WXapi.buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                WXapi.getInstance().sendReq(req);
            }
        }.start();

    }

    public static boolean isInstallWX() {
        // 判断是否安装了微信客户端
        if (wxapi.isWXAppInstalled()) {
            return true;
        } else {
            return false;
        }
    }

    public static void loginWX() {

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        wxapi.sendReq(req);
    }
}
