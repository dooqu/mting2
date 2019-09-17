package cn.xylink.mting.ui.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apaches.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import cn.xylink.mting.MTing;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.UpgradeInfo;
import cn.xylink.mting.common.Const;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.UpgradeRequest;
import cn.xylink.mting.model.UpgradeResponse;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.ui.dialog.UpgradeConfirmDialog;
import cn.xylink.mting.upgrade.UpgradeManager;
import cn.xylink.mting.utils.EncryptionUtil;
import cn.xylink.mting.utils.GsonUtil;
import cn.xylink.mting.utils.PackageUtils;

public class AboutVersion extends BaseActivity {
    TextView versionName;
    TextView txtCurrentVersion;
    View backIcon;
    Timer timer;

    View institutionOfUserButton;
    View privacyButton;
    View contactUsButton;

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
        versionName.setText("检测新版本");

        institutionOfUserButton = findViewById(R.id.institutionOfUserButton);
        privacyButton = findViewById(R.id.privacyButton);
        contactUsButton = findViewById(R.id.contactUsButton);
        institutionOfUserButton.setOnClickListener(this::onButtonClick);
        privacyButton.setOnClickListener(this::onButtonClick);
        contactUsButton.setOnClickListener(this::onButtonClick);
    }

    @Override
    protected void initData() {
        txtCurrentVersion = (TextView) findViewById(R.id.txtCurrentVersion);
        txtCurrentVersion.setText("v" + PackageUtils.getAppVersionName(this));
        versionName = (TextView) findViewById(R.id.versionName);
        versionName.setOnClickListener(this::checkNewVersion);
        backIcon = findViewById(R.id.about_version_back);
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
            Toast.makeText(this, "升级包正在下载中", Toast.LENGTH_SHORT).show();
            return;
        }
        int currentVersionCode = Integer.parseInt(PackageUtils.getAppVersionCode(this));
        /*
        if (UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getAppVersionCode() > currentVersionCode) {
            UpgradeConfirmDialog upgradeConfirmDialog = new UpgradeConfirmDialog(this, UpgradeManager.CurrentUpgradeInfo);
            upgradeConfirmDialog.setListener(this::onUpgradeConfirm);
            upgradeConfirmDialog.show();
        }
        */
        versionName.setText("正在检测新版本...");
        UpgradeRequest request = new UpgradeRequest();
        request.setAppPackage(PackageUtils.getAppPackage(this));
        request.setAppVersion(PackageUtils.getAppVersionName(this));
        request.setVersionId(PackageUtils.getAppVersionCode(this));
        try {
            request.setChannel(new Base64().encodeToString(EncryptionUtil.encrypt("mting", EncryptionUtil.getPublicKey(Const.publicKey))));
        }
        catch (Exception ex) {
            versionName.setText("升级检测发生错误");
            return;
        }
        request.setDeviceId(PackageUtils.getWifiMac(this));
        request.doSign();

        OkGoUtils.getInstance().postData(
                new IBaseView() {
                    @Override
                    public void showLoading() {
                    }

                    @Override
                    public void hideLoading() {
                    }
                },
                RemoteUrl.getUpgradeUrl(),
                GsonUtil.GsonString(request), UpgradeResponse.class,
                new OkGoUtils.ICallback<UpgradeResponse>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        Log.d("SPEECH", "onFailure:" + errorMsg);
                        UpgradeManager.CurrentUpgradeInfo = null;
                        versionName.setText("恭喜您，当前已经是最新版本");
                        versionName.setOnClickListener(null);
                    }

                    @Override
                    public void onSuccess(UpgradeResponse response) {
                        versionName.setText("检测新版本");
                        if ((response.getCode() == 200 || response.getCode() == 201) && response.getData() != null && response.getData().getAppVersionCode() > currentVersionCode) {
                            UpgradeConfirmDialog upgradeConfirmDialog = new UpgradeConfirmDialog(AboutVersion.this, response.getData());
                            upgradeConfirmDialog.setListener(AboutVersion.this::onUpgradeConfirm);
                            upgradeConfirmDialog.show();
                        }
                        else {
                            versionName.setText("恭喜您，当前已经是最新版本");
                            versionName.setOnClickListener(null);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });

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

    private void onButtonClick(View v) {
        Intent intent = new Intent(this, PlayerlActivity.class);
        if (v == privacyButton) {
            intent.putExtra(PlayerlActivity.EXTRA_HTML, "http://service.xylink.cn/article/html/policy.html");
            intent.putExtra(PlayerlActivity.EXTRA_TITLE, "隐私政策");
        }
        else if (v == institutionOfUserButton) {
            intent.putExtra(PlayerlActivity.EXTRA_HTML, "http://service.xylink.cn/article/html/agreement.html");
            intent.putExtra(PlayerlActivity.EXTRA_TITLE, "用户协议");
        }
        else if(v  == contactUsButton) {
            intent = new Intent(this, JoinUsActivity.class);
        }
        startActivity(intent);
    }
}