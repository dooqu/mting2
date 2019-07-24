package cn.xylink.mting.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.UpgradeInfo;


import static android.content.Context.DOWNLOAD_SERVICE;

public class UpgradeConfirmDialog extends Dialog {

    public static Map<BigInteger, String> downloadFiles = new HashMap<>();

    public static class UpgradeDownloadCompleteBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadTaskId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            if (downloadTaskId > 0) {
                final UpgradeConfirmDialog.UpgradeDownloadQuery query = new UpgradeConfirmDialog.UpgradeDownloadQuery(context, downloadTaskId);
                int result = query.getCurrentStatus();
                switch (result) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (downloadFiles.containsKey(BigInteger.valueOf(downloadTaskId))) {
                            File apkFile = new File(downloadFiles.get(BigInteger.valueOf(downloadTaskId)));
                            Uri fileUri = Uri.fromFile(apkFile);
                            Intent installIntent = new Intent(Intent.ACTION_VIEW);
                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                            context.startActivity(installIntent);
                            MTing.CurrentUpgradeDownloadId = 0;
                        }
                        break;

                    case DownloadManager.STATUS_FAILED:
                        MTing.CurrentUpgradeDownloadId = 0;
                        break;
                }
            }
        }
    }

    public static class UpgradeDownloadQuery {
        protected long id;
        DownloadManager mDownloadManager;

        public UpgradeDownloadQuery(Context context, long id) {
            this.id = id;
            mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        }

        public int[] getStatus() {
            int[] bytesAndStatus = new int[]{-1, -1, 0};
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
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

        public int getCurrentStatus() {
            int[] bytesAndStatus = new int[]{-1, -1, 0};
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
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

            return bytesAndStatus[2];
        }
    }

    public interface DialogListener {
        void callback(long downloadId, UpgradeInfo upgradeInfo);
    }


    protected Context context;
    protected UpgradeInfo upgradeInfo;
    protected DialogListener listener;
    protected TextView upgradeName;
    protected TextView upgradeTime;
    protected TextView upgradeContent;
    protected View cancelButton;
    protected View confirmButton;
    protected View dialog_upgrade_close;

    public UpgradeConfirmDialog(Context context, UpgradeInfo upgradeInfo) {
        super(context, R.style.upgrade_dialog);
        this.context = context;
        this.upgradeInfo = upgradeInfo;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_upgrade);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.8);
        dialogWindow.getAttributes().gravity = Gravity.CENTER;
        dialogWindow.setAttributes(layoutParams);

        upgradeName = (TextView) findViewById(R.id.upgrade_name);
        upgradeTime = (TextView) findViewById(R.id.upgrade_time_text);
        upgradeContent = (TextView) findViewById(R.id.upgrade_content_text);
        cancelButton = findViewById(R.id.upgrade_button_cancel);
        confirmButton = findViewById(R.id.upgrade_button_confirm);
        dialog_upgrade_close = findViewById(R.id.dialog_upgrade_close);

        if (this.upgradeInfo != null) {
            upgradeName.setText("轩辕听 v" + upgradeInfo.getAppVersionName());
            upgradeContent.setText(upgradeInfo.getAppContent());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            upgradeTime.setText(dateFormat.format(new Date(upgradeInfo.getCreateDate())));
        }

        if (upgradeInfo != null && upgradeInfo.getNeedUpdate() == 0) {
            cancelButton.setVisibility(View.GONE);
        }



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.callback(0, upgradeInfo);
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = initDownload();
                dismiss();
                if (listener != null) {
                    listener.callback(id, upgradeInfo);
                }
            }
        });

        dialog_upgrade_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    protected long initDownload() {

        if (this.upgradeInfo == null
                || this.upgradeInfo.getAppDownloadUrl() == null
                || this.upgradeInfo.getAppDownloadUrl().trim() == "") {
            return -1;
        }

        DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Uri resource = Uri.parse(this.upgradeInfo.getAppDownloadUrl());
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, resource.getLastPathSegment());
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("轩辕听正在下载升级:" + upgradeInfo.getAppName());
        MTing.CurrentUpgradeDownloadId = mDownloadManager.enqueue(request);

        if (MTing.CurrentUpgradeDownloadId > 0) {
            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), resource.getLastPathSegment());
            downloadFiles.put(BigInteger.valueOf(MTing.CurrentUpgradeDownloadId), apkFile.getAbsolutePath());
        }

        return MTing.CurrentUpgradeDownloadId;
    }
}
