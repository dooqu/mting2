package cn.xylink.mting.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.xylink.mting.R;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.UpgradeRequest;
import cn.xylink.mting.model.UpgradeResponse;
import cn.xylink.mting.model.data.OkGoUtils;
import cn.xylink.mting.utils.GsonUtil;
import cn.xylink.mting.utils.PackageUtils;

public class AboutVersion extends Activity {
    TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_version);
        versionName = (TextView) findViewById(R.id.versionName);
        checkNewVersion();
    }


    protected void checkNewVersion() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.upgrade_dialog);
        alert.setView(R.layout.dialog_upgrade);
        AlertDialog dialog = alert.create();
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager windowManager = this.getWindowManager();

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = (int)(windowManager.getDefaultDisplay().getWidth() * 0.8);
        //layoutParams.height = (int)(windowManager.getDefaultDisplay().getHeight() * 0.5);
        window.getAttributes().gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        UpgradeRequest request = new UpgradeRequest();
        request.setAppPackage(PackageUtils.getAppPackage(this));
        request.setAppVersion(PackageUtils.getAppVersionName(this));
        request.setVersionId(PackageUtils.getAppVersionCode(this));
        request.setChannel("_91");
        request.setDeviceId("001001");
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
                "http://test.xylink.cn/api/v2/version/check",
                GsonUtil.GsonString(request), UpgradeResponse.class,
                new OkGoUtils.ICallback<UpgradeResponse>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        versionName.setText(errorMsg);
                    }

                    @Override
                    public void onSuccess(UpgradeResponse response) {
                        if (response.getData() != null) {
                            versionName.setText(response.getData().getAppContent());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("xylink", "onComplete");
                    }
                });
    }
}
