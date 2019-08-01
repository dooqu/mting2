package cn.xylink.mting.upgrade;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.UpgradeInfo;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpgradeManager {

    public static class DownloadReceiver extends BroadcastReceiver {
        Activity activity;

        public void regist(Activity context) {
            if (context == activity) {
                return;
            }

            if (activity != null) {
                activity.unregisterReceiver(this);
            }

            if (context != null) {
                context.registerReceiver(this, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }

            this.activity = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long currentId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //如果不是升级下载任务，那么忽略
            if (DownloadTaskId != currentId || currentId <= 0) {
                return;
            }
            int result = UpgradeManager.getInstance().queryState(DownloadTaskId);
            switch (result) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    File apkFile = new File(DownloadTaskFilePath);
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    Uri fileUri = null;
                    if (false || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        File destFile = new File(context.getFilesDir() + File.pathSeparator + apkFile.getName());
                        apkFile.renameTo(destFile);
                        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        fileUri = FileProvider.getUriForFile(context, "cn.xylink.mting.utils.DownloadFileProvider", destFile);
                    }
                    else {
                        fileUri = Uri.fromFile(apkFile);
                    }
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                    DownloadTaskId = 0;
                    try {
                        activity.startActivity(installIntent);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    break;

                case DownloadManager.STATUS_FAILED:
                    DownloadTaskId = 0;
                    break;
            }
        }
    }


    protected static UpgradeManager upgradeManager = new UpgradeManager();
    public static long DownloadTaskId;
    protected static String DownloadTaskFilePath;
    public static UpgradeInfo CurrentUpgradeInfo;

    public static UpgradeManager getInstance() {
        return upgradeManager;
    }

    public long startDownload(UpgradeInfo upgradeInfo) {
        if (DownloadTaskId != 0) {
            return -1;
        }
        DownloadManager mDownloadManager = (DownloadManager) MTing.getInstance().getSystemService(DOWNLOAD_SERVICE);
        Uri resource = Uri.parse(upgradeInfo.getAppDownloadUrl());
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setMimeType("application/vnd.android.package-archive");
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, resource.getLastPathSegment());
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("轩辕听正在下载升级:" + upgradeInfo.getAppName());
        long ret = mDownloadManager.enqueue(request);

        if (ret > 0) {
            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), resource.getLastPathSegment());
            DownloadTaskId = ret;
            DownloadTaskFilePath = apkFile.getAbsolutePath();
        }
        return ret;
    }

    public int[] queryInfo(long taskId) {
        DownloadManager mDownloadManager = (DownloadManager) MTing.getInstance().getSystemService(DOWNLOAD_SERVICE);
        int[] bytesAndStatus = new int[]{-1, -1, -1};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(taskId);
        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    public int queryState(long taskId) {
        int[] info = queryInfo(taskId);

        return info[2];
    }
}
