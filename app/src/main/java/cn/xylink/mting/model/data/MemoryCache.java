package cn.xylink.mting.model.data;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.bean.UserInfo;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：内存缓存类 <p>
 * 创建日期：2013-9-9
 * </p>
 * @see
 */
public class MemoryCache {

    private List<BaseActivity> acts = new ArrayList<>();
    private static MemoryCache sInstance;
    private String loginToken;
    private int experience;
    private UserInfo userInfo;
    private String deviceUuid;
    private boolean changeFavor;
    private int keyBordHeight;
    private int rgTime;
    private int textSize;

    private MemoryCache() {
    }

    public static void init() {
        sInstance = new MemoryCache();
    }

    public static MemoryCache getInstance() {
        return sInstance;
    }

    public void putActivity(BaseActivity activity) {
        this.acts.add(activity);
    }

    public void removeActivity(BaseActivity activity) {
        this.acts.remove(activity);
    }

    public void closeActivityEx(BaseActivity baseActivity) {
        for (int i = 0; i < acts.size(); i++) {
            BaseActivity act = acts.get(i);
            if (act == baseActivity) {
                continue;
            }
            act.finish();
        }
        acts.clear();
        if (baseActivity != null) {
            acts.add(baseActivity);
        }
    }

    public void setLoginToken(String token) {
        this.loginToken = token;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getExperience() {
        return experience;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInformation) {
        this.userInfo = userInformation;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setChangeFavor(boolean changeFavor) {
        this.changeFavor = changeFavor;
    }

    public boolean isChangeFavor() {
        return changeFavor;
    }

    public void setKeyBordHeight(int keyBordHeight) {
        this.keyBordHeight = keyBordHeight;
    }

    public int getKeyBordHeight() {
        return keyBordHeight;
    }

    public void setRgTime(int rgTime) {
        this.rgTime = rgTime;
    }

    public int getRgTime() {
        return rgTime;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextSize() {
        return textSize;
    }
}
