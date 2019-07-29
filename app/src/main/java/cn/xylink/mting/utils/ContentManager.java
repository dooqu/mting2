package cn.xylink.mting.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.xylink.mting.MTing;
import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.model.data.Const;
import cn.xylink.mting.model.data.FileCache;
import cn.xylink.mting.model.data.MemoryCache;

/**
 * Created by wjn on 2019/3/4.
 */

public class ContentManager {
    private static ContentManager sInstance;
    private static Context mContext;

    private ContentManager(Context context) {
        MemoryCache.init();
        this.mContext = context.getApplicationContext();
        FileCache.init(mContext);
    }

    public static void init(Context context) {
        sInstance = new ContentManager(context);
    }

    public static ContentManager getInstance() {
        return sInstance;
    }

    public void startActivity(BaseActivity activity) {
        MemoryCache.getInstance().putActivity(activity);
    }

    public void finishActivity(BaseActivity act) {
        MemoryCache.getInstance().removeActivity(act);
    }

    public void clearAllActEx(BaseActivity act) {
        MemoryCache.getInstance().closeActivityEx(act);
    }

    public void setLoginToken(String token) {
        MemoryCache.getInstance().setLoginToken(token);
        FileCache.getInstance().setLoginToken(token);
    }

    public String getLoginToken() {
        String loginToken = MemoryCache.getInstance().getLoginToken();
        if (TextUtils.isEmpty(loginToken)) {
            loginToken = FileCache.getInstance().getLogintoken();
        }
        return loginToken;
    }

    public void setExperience(int experience) {
        MemoryCache.getInstance().setExperience(experience);
        FileCache.getInstance().setExperience(experience);
    }

    public int getExperience() {
        int experience = FileCache.getInstance().getExperience();
        return experience;
    }

    public void setUserInfo(UserInfo userInformation) {
        MemoryCache.getInstance().setUserInfo(userInformation);
        FileUtil.writeFile(MTing.getInstance(), Const.FileName.USER_INFO, new Gson().toJson(userInformation));
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = MemoryCache.getInstance().getUserInfo();
        if (userInfo == null) {
            String userInfoData = FileUtil.readFile(MTing.getInstance(), Const.FileName.USER_INFO);
            userInfo = new Gson().fromJson(userInfoData, new TypeToken<UserInfo>() {
            }.getType());
        }
        return userInfo;
    }

    public void setDeviceUuid(String uuid) {
        MemoryCache.getInstance().setDeviceUuid(uuid);
    }

    public String getDeviceUuid() {
        return MemoryCache.getInstance().getDeviceUuid();
    }

    public void setChangeFavor(boolean flag) {
        MemoryCache.getInstance().setChangeFavor(flag);
    }

    public boolean isChangeFavor() {
        return MemoryCache.getInstance().isChangeFavor();
    }

    public void setHxUserId(String id) {
        FileCache.getInstance().setHxUserId(id);
    }

    public void setHxPasword(String pasword) {
        FileCache.getInstance().setHxPasword(pasword);
    }

    public String getHxUserId() {
        return FileCache.getInstance().getHxUserId();
    }

    public String getHxPassword() {
        return FileCache.getInstance().getHxPassword();
    }

    public void setKeyBordHeight(int keyBordHeight) {
        MemoryCache.getInstance().setKeyBordHeight(keyBordHeight);
        FileCache.getInstance().setKeyBordHeight(keyBordHeight);
    }

    public int getKeyBordHeight() {
        int keyBordHeight = MemoryCache.getInstance().getKeyBordHeight();
        if (keyBordHeight == 0) {
            keyBordHeight = FileCache.getInstance().getKeyBordHeight();
        }
        return keyBordHeight;
    }

    public void setHxFirst(boolean b) {
        FileCache.getInstance().setHxFirst(b);
    }

    public boolean getHxFirst() {
        return FileCache.getInstance().getHxFirst();
    }

    public void setRgTime(int rgtime) {
        MemoryCache.getInstance().setRgTime(rgtime);
    }


    public int getRgTime() {
        return MemoryCache.getInstance().getRgTime();
    }

    public void setTextSize(int change) {
        MemoryCache.getInstance().setTextSize(change);
        FileCache.getInstance().setTextSize(change);
    }

    public int getTextSize() {
        int i = MemoryCache.getInstance().getTextSize();
        if (i > 0) {
            return i;
        }
        return FileCache.getInstance().getTextSize();
    }
}
