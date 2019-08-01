package cn.xylink.mting.ui.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.UpgradeInfo;
import cn.xylink.mting.ui.dialog.UpgradeConfirmDialog;
import cn.xylink.mting.upgrade.UpgradeManager;
import cn.xylink.mting.utils.PackageUtils;

public class AboutVersion extends BaseActivity {

    TextView versionName;
    TextView txtCurrentVersion;
    ImageView backIcon;
    Timer timer;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void preView() {
        setContentView(R.layout.activity_about_version);
    }

    @Override
    protected void initView() {
        int currentVersionCode = Integer.parseInt(PackageUtils.getAppVersionCode(this));
        if (UpgradeManager.DownloadTaskId > 0) {
            long state = UpgradeManager.getInstance().queryState(UpgradeManager.DownloadTaskId);
            if (state == DownloadManager.STATUS_RUNNING || state == DownloadManager.STATUS_PENDING) {
                onUpgradeConfirm(UpgradeManager.DownloadTaskId, null);
                return;
            }
        }
        else if (UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getAppVersionCode() > currentVersionCode) {
            versionName.setText("轩辕听 v" + UpgradeManager.CurrentUpgradeInfo.getAppVersionName());
            return;
        }
        versionName.setText("当前是最新版本");
    }

    @Override
    protected void initData() {
        txtCurrentVersion = (TextView) findViewById(R.id.txtCurrentVersion);
        txtCurrentVersion.setText("v" + PackageUtils.getAppVersionName(this));
        versionName = (TextView) findViewById(R.id.versionName);
        versionName.setOnClickListener(this::checkNewVersion);
        backIcon = (ImageView) findViewById(R.id.about_version_back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        timer = new Timer();
    }

    @Override
    protected void initTitleBar() {

    }

    protected void checkNewVersion(View view) {
        /* 如果当前有下载，不弹出*/
        if (UpgradeManager.DownloadTaskId != 0) {
            return;
        }
        int currentVersionCode = Integer.parseInt(PackageUtils.getAppVersionCode(this));
        if (UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getAppVersionCode() > currentVersionCode) {
            UpgradeConfirmDialog upgradeConfirmDialog = new UpgradeConfirmDialog(this, UpgradeManager.CurrentUpgradeInfo);
            upgradeConfirmDialog.setListener(this::onUpgradeConfirm);
            upgradeConfirmDialog.show();
        }
    }


    protected void onUpgradeConfirm(long downloadId, UpgradeInfo upgradeInfo) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {

                    int[] status = UpgradeManager.getInstance().queryInfo(downloadId);
                    switch (status[2]) {
                        case DownloadManager.STATUS_PAUSED:
                            versionName.setText("下载已经暂停");
                            break;
                        case DownloadManager.STATUS_PENDING:
                            versionName.setText("准备下载中");
                            break;
                        case DownloadManager.STATUS_FAILED:
                            UpgradeManager.DownloadTaskId = 0;
                            versionName.setText("下载失败");
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            UpgradeManager.DownloadTaskId = 0;
                            timer.cancel();
                            versionName.setText("下载完成");
                            break;

                        case DownloadManager.STATUS_RUNNING:
                            versionName.setText("正在下载安装包: " + (status[0] * 100 / status[1]) + "%");
                            break;
                    }
                });
            }
        }, 10, 500);

    }
}