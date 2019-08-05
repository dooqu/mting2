package cn.xylink.mting.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.UpgradeInfo;
import cn.xylink.mting.upgrade.UpgradeManager;

public class UpgradeConfirmDialog extends Dialog {

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
                Toast.makeText(context, "升级包正在下载中", Toast.LENGTH_SHORT).show();
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


        final String packageName = "com.android.providers.downloads";
        int state = context.getPackageManager().getApplicationEnabledSetting(packageName);

        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            Log.d("SPEECH_", "禁用");
        }

        return UpgradeManager.getInstance().startDownload(upgradeInfo);
    }
}
