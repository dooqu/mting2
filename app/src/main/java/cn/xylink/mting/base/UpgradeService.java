package cn.xylink.mting.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import cn.xylink.mting.R;

public class UpgradeService extends Service {

    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateDownloadNotif() {

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification_album))
                .setTicker("正在下载升级")
                .setContentTitle("轩辕听正在下载升级")
                .setContentText("正在升级")
                .setOngoing(false)
                .setAutoCancel(true)
                .setShowWhen(false);
    }
}
