package cn.xylink.mting.base;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.tendcloud.tenddata.TCAgent;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import cn.xylink.mting.MTing;
import cn.xylink.mting.ui.SplashActivity;
import cn.xylink.mting.ui.dialog.UpgradeConfirmDialog;
import cn.xylink.mting.upgrade.UpgradeManager;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.PackageUtils;
import cn.xylink.mting.utils.T;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int INSTALL_PACKAGES_REQUESTCODE = 100;
    public static final int GET_UNKNOWN_APP_SOURCES = 106;
    protected Intent mUpdateIntent;
    protected Context context;
    protected Timer upgradeTimer;
    UpgradeManager.DownloadReceiver downloadReceiver ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().setStatusBarColor(0xffffffff);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
        preView();
        ButterKnife.bind(this);
        initData();
        initView();
        initTitleBar();

        downloadReceiver = new UpgradeManager.DownloadReceiver();
        downloadReceiver.regist(this);
        if (enableVersionUpgrade() == true) {
            checkOnlineUpgrade();
        }

        TCAgent.onPageStart(this, this.getComponentName().getClassName());
    }


    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            //之前一直不成功的原因是,getX获取的是相对父视图的坐标,getRawX获取的才是相对屏幕原点的坐标！！！
            L.v("leftTop[]", "zz--left:" + left + "--top:" + top + "--bottom:" + bottom + "--right:" + right);
            L.v("event", "zz--getX():" + event.getRawX() + "--getY():" + event.getRawY());
            if (event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            boolean hideInputResult = isShouldHideInput(v, ev);
            L.v("hideInputResult", "zzz-->>" + hideInputResult);
            if (hideInputResult) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) this
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (v != null) {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upgradeTimer != null) {
            upgradeTimer.cancel();
            upgradeTimer = null;
        }

        downloadReceiver.regist(null);

        TCAgent.onPageEnd(this, this.getComponentName().getClassName());
    }

    @Override
    protected void onResume() {
        super.onResume();

        TCAgent.onPageStart(this, this.getComponentName().getClassName());
    }


    @Override
    protected void onPause() {
        super.onPause();

        TCAgent.onPageEnd(this, this.getComponentName().getClassName());
    }

    /**
     * 布局文件
     */
    protected abstract void preView();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化titleBar
     */
    protected abstract void initTitleBar();


    /**
     * 弹出一个6s显示的toast框
     */
    public void toastLong(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出一个6s显示的toast框
     */
    public void toastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出一个3s显示的toast框
     */
    public void toastShort(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出一个3s显示的toast框
     */
    public void toastShort(String msg) {
        T.showCustomToast(msg);
    }


    /*
     * 描述：跳转Activity不传值，不返回数据
     *
     * @param act 要跳转的Activity
     * */
    public void jumpActivity(Class act) {
        jumpActivityForResult(act, -1);
    }

    /*
     * 描述：跳转Activity不传值，不返回数据
     *
     * @param act 要跳转的Activity
     * @param bundle没值传null
     * */
    public void jumpActivity(Class act, Bundle bundle) {
        jumpActivityForResult(act, bundle, -1);
    }

    /*
     * 描述：跳转Activity不传值，返回数据
     *
     * @param act 要跳转的Activity
     * @param code>0才能带有返回值
     * */
    public void jumpActivityForResult(Class act, int code) {
        jumpActivityForResult(act, null, code);
    }

    /*
     * 描述：跳转Activity不传值，返回数据
     *
     * @param act 要跳转的Activity
     * @param code>0才能带有返回值
     * @param bundle没值传null
     * */
    public void jumpActivityForResult(Class act, Bundle bundle, int code) {
        Intent intent = new Intent();
        intent.setClass(this, act);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (code > 0) {
            startActivityForResult(intent, code);
        } else {
            startActivity(intent);
        }
    }


    public void hideSoftInput() {
        try {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSoftInput(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {

        }
    }



    private void installApk() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                installAPK();
            } else {                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
            }
        } else {
            installAPK();
        }
    }

    private void installAPK() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = getUriFromFile(getBaseContext(), new File(""));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(context,
                    "cn.xylink.mting.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        return imageUri;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_UNKNOWN_APP_SOURCES) {
            if(resultCode == Activity.RESULT_CANCELED) {
                Log.d("SPEECH_", "授权被取消");
                if(UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getNeedUpdate() == 0) {
                    Toast.makeText(this, "当前升级为重要更新，请开启应用重新授权", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                    return;
                }
                else {
                    Toast.makeText(this, "授权被取消，升级安装中断", Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode == Activity.RESULT_OK) {
                Log.d("SPEECH_", "授权成功");
                if(UpgradeManager.DownloadTaskFilePath != null) {
                    downloadReceiver.installApk(UpgradeManager.DownloadTaskFilePath);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INSTALL_PACKAGES_REQUESTCODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installApk();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                }
                break;
        }
    }

    /**
     * 查看服务是否开启
     */
    private Boolean isServiceRunning(Context context, String serviceName) {
        //获取服务方法  参数 必须用大写的Context！！！
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String className = info.service.getClassName();
            if (serviceName.equals(className))
                return true;
        }
        return false;
    }

    protected void startBaseService(Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);// 启动服务
            } else {
                startService(intent);
            }
        } catch (Exception e) {

        }
    }


    protected boolean enableVersionUpgrade() {
        return false;
    }


    protected void checkOnlineUpgrade() {
        int currentVersionCode = Integer.parseInt(PackageUtils.getAppVersionCode(this));
        if (UpgradeManager.CurrentUpgradeInfo == null || UpgradeManager.CurrentUpgradeInfo.getAppVersionCode() <= currentVersionCode) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Log.d("SPEECH_", "isDestroy:" + BaseActivity.this.isDestroyed());
            Log.d("SPEECH_", "isFinishing:" + BaseActivity.this.isFinishing());
            if(BaseActivity.this.isFinishing() || BaseActivity.this.isDestroyed()) {
                return;
            }
            if (UpgradeManager.CurrentUpgradeInfo != null && UpgradeManager.CurrentUpgradeInfo.getAppVersionCode() > currentVersionCode) {
                UpgradeConfirmDialog upgradeConfirmDialog = new UpgradeConfirmDialog(this, UpgradeManager.CurrentUpgradeInfo);
                UpgradeManager.CurrentUpgradeInfo = null;
                upgradeConfirmDialog.show();
            }
        }, 3000);
    }
}
