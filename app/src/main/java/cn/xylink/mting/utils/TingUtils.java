package cn.xylink.mting.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

public class TingUtils {

    public static String getDeviceId(Context context) {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //wifi mac地址
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();
            if (!TextUtils.isEmpty(wifiMac)) {
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                L.v("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if(!TextUtils.isEmpty(imei)){
                deviceId.append("imei");
                deviceId.append(imei);
                L.v("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if(!TextUtils.isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                L.v("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.v("getDeviceId : ", deviceId.toString());
        return deviceId.toString();
    }


    /**
     * 去应用市场评分
     */
    public static void goToMarket(Context context) {
//        if (!isMarketInstalled(context)) {
//            T.showCustomToast("您的手机没有安装应用市场");
//            return;
//        }
        try {
            Uri uri = Uri.parse("market://details?id="+ context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            // 也可以调到某个网页应用市场
            T.showCustomToast("手机没有安装应用市场");
        }
    }

    /**
     * 本手机是否安装了应用市场
     * @param context
     * @return
     */
    public static boolean isMarketInstalled(Context context) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));
        List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return 0 != list.size();

    }
}
