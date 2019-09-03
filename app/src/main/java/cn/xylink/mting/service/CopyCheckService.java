package cn.xylink.mting.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import java.util.List;

import cn.xylink.mting.R;
import cn.xylink.mting.ui.activity.MainActivity;
import cn.xylink.mting.ui.activity.SearchActivity;
import cn.xylink.mting.ui.dialog.CopyAddDialog;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.StringUtil;

/*
 *全局检测复制事件
 *
 * -----------------------------------------------------------------
 * 2019/8/20 14:34 : Create CopyCheckService.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class CopyCheckService extends Service {
    private ClipboardManager clipboardManager;
    private CopyAddDialog mDialog;
    private NotificationManager mNotifManager;
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mNotifManager != null)
                mNotifManager.cancel(212312313);
        }
    };
    private long mNotifTime;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(() -> {
            CharSequence copyStr = getCopy();
            L.v(copyStr);
//            Intent intent =new Intent(this, SearchActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
            if (!TextUtils.isEmpty(copyStr) && (System.currentTimeMillis() - mNotifTime) > 500
                    && !TextUtils.isEmpty(StringUtil.matcherUrl(copyStr.toString()))) {
                copyStr = StringUtil.matcherUrl(copyStr.toString());
                L.v(copyStr);
                List<String> tCopy = ContentManager.getInstance().getCopyArray();
                if (tCopy != null && tCopy.size() > 0)
                    for (String s : tCopy) {
                        if (s.equals(copyStr.toString())) {
                            return;
                        }
                    }
                mNotifManager.cancel(212312313);
                mHandler.removeCallbacks(runnable);
                sendCustomHeadsUpViewNotification(this, copyStr.toString());
                mNotifTime = System.currentTimeMillis();
            }
//            if ((mDialog == null || !mDialog.isShowing())
//                    && !TextUtils.isEmpty(copyStr)
//                    && (copyStr.toString().startsWith("http://") || copyStr.toString().startsWith("https://"))) {
//                mDialog = new CopyAddDialog(MTing.getInstance(), copyStr.toString());
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//                } else {
//                    mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//                }
//                mDialog.show();
//                ContentManager.getInstance().addCopyItem(copyStr.toString());
//            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            initNotification();
    }

    private void initNotification() {

        synchronized (this) {
            Intent intentNotifOpen = new Intent();
            intentNotifOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentNotifOpen.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intentNotifOpen.setData(Uri.fromParts("package", this.getPackageName(), null));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentNotifOpen, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.icon_notif)
                    .setContentTitle("“轩辕听”正在运行")
                    .setContentText("点击即可了解详情或停止应用")
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setShowWhen(true);

            //>= android 8.0 设定foregroundService的前提是notification要创建channel，并关掉channel的sound
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelId = "cn.xylink.mting";
                String channelName = "SPEECH_SERVICE_NAME";
                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(false);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setSound(null, null);
                mNotifManager.createNotificationChannel(notificationChannel);
                //设定builder的channelid
                builder.setChannelId(channelId);
            }

            Notification notification = builder.build();
            this.startForeground(android.os.Process.myPid(), notification);
        }
    }


    private NotificationCompat.Builder nb;
    private RemoteViews headsUpView;

    public void sendCustomHeadsUpViewNotification(Context context, String str) {
        if (nb == null || headsUpView == null) {
            //创建点击通知时发送的广播
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent answerIntent = new Intent(context, MainActivity.class);
            answerIntent.putExtra("copy_str", str);
            answerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent answerPendingIntent = PendingIntent.getActivity(context, 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            headsUpView = new RemoteViews(context.getPackageName(), R.layout.copy_tip_notif);
            headsUpView.setOnClickPendingIntent(R.id.tv_copy_tip_notif_add, answerPendingIntent);
//            headsUpView.setCharSequence(R.id.tv_copy_tip_notif_link, "setText", str);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelId = "cn.xylink.mting.copy.tip";
                String channelName = "COPY_CHECK_TIP";
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.setShowBadge(false);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mNotifManager.createNotificationChannel(notificationChannel);
            }
            //创建通知
            nb = new NotificationCompat.Builder(this, "cn.xylink.mting.copy.tip")
                    .setSmallIcon(R.mipmap.icon_notif)
                    .setContentTitle("添加到轩辕听")
//                    .setContentText(str)
                    .setAutoCancel(true)
                    .setShowWhen(true)
                    .setContentIntent(pi)
                    .setCategory(NotificationCompat.CATEGORY_SYSTEM)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setFullScreenIntent(null, true)
                    .setCustomHeadsUpContentView(headsUpView);
        }
        nb.setContentText(str);
        headsUpView.setCharSequence(R.id.tv_copy_tip_notif_link, "setText", str);
        mNotifManager.notify(212312313, nb.build());
        mHandler.postDelayed(runnable, 2000);
    }

    public CharSequence getCopy() {
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            L.v(clipData.getDescription().getLabel());
            CharSequence str = clipData.getItemAt(0).getText();
            return str;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.v();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
