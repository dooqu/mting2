package cn.xylink.mting.upgrade;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.UpgradeInfo;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpgradeManager {

    public static class DownloadReceiver extends BroadcastReceiver {
        BaseActivity activity;

        public void regist(BaseActivity context) {
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
                    DownloadTaskId = 0;
                    installApk(DownloadTaskFilePath);
                    break;

                case DownloadManager.STATUS_FAILED:
                    DownloadTaskId = 0;
                    break;
            }
        }

        public void installApk(String filePath) {
            if (applyUnkownSourcePackagePermission(filePath) == true) {
                installApkProcess(filePath);
            }
        }

        public void changFileMode(String filePath) {
            String command = "chmod " + " 777 " + " " + filePath;
            Runtime runtime = Runtime.getRuntime();
            try {
                Process p = runtime.exec(command);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void installApkProcess(String filePath) {
            File apkFile = new File(filePath);
            changFileMode(apkFile.getAbsolutePath());
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri fileUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileUri = FileProvider.getUriForFile(activity, "cn.xylink.mting.utils.DownloadFileProvider", apkFile);
            }
            else {
                fileUri = Uri.fromFile(apkFile);
            }

            installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            try {
                activity.startActivity(installIntent);
                if (UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getNeedUpdate() == 0) {
                    System.exit(0);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        private boolean applyUnkownSourcePackagePermission(String path) {
            boolean haveInstallPermission = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //先获取是否有安装未知来源应用的权限
                haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {//没有权限
                    AlertDialog alertDialog = new AlertDialog.Builder(activity)
                            .setTitle("请开启未知来源权限")
                            .setMessage("安装应用需要打开安装未知来源应用权限")
                            .setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getNeedUpdate() == 0) {
                                        System.exit(0);
                                        return;
                                    }
                                    else {
                                        Toast.makeText(activity, "安全授权被取消", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    toInStallPermissionSettingActivity(path);
                                }
                            }).create();

                    alertDialog.show();

                }
            }
            //有权限，进行安装操作 安装就不写了
            return haveInstallPermission;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void toInStallPermissionSettingActivity(String packagePath) {
            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            //注意这个是8.0新API
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            Bundle bundle = new Bundle();
            bundle.putString("packagePath", packagePath);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, BaseActivity.GET_UNKNOWN_APP_SOURCES, bundle);
        }
    }


    protected static UpgradeManager upgradeManager = new UpgradeManager();
    public static long DownloadTaskId;
    public static String DownloadTaskFilePath;
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
