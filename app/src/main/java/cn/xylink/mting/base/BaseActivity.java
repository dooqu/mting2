package cn.xylink.mting.base;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import cn.xylink.mting.utils.T;

public abstract class BaseActivity extends AppCompatActivity {


    private static final int INSTALL_PACKAGES_REQUESTCODE = 100;
    private static final int GET_UNKNOWN_APP_SOURCES = 101;
    protected Intent mUpdateIntent;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(0xffffffff);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        preView();
        ButterKnife.bind(this);
        initView();
        initTitleBar();
        initData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        Uri uri = getUriFromFile(getBaseContext(),new File(""));
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
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


}
