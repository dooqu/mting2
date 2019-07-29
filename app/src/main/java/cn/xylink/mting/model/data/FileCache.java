package cn.xylink.mting.model.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.xylink.mting.utils.DesAlaorithm;


/**
 * @author yangningbo
 * @version V1.0
 * @Description：文件缓存类 <p>
 * 创建日期：2013-9-4
 * </p>
 * @see
 */
public class FileCache {
    private static final String LOGINTOKEN = "LOGINTOKEN";
    private static final String EXPERIENCE = "EXPERIENCE";
    private static final String PROMPTCLOSE_AT = "PROMPTCLOSE_AT";
    private static final String PROMPTCLOSE_FAVORITE = "PROMPTCLOSE_FAVORITE";
    private static final String GUIDE_FIRST = "GUIDE_FIRST";
    private static final String HX_USER_ID = "HX_USER_ID";
    private static final String HX_PASSWORD = "HX_PASSWORD";
    private static final String KEYBORDHEIGHT = "KEYBORDHEIGHT";
    private static final String HXFIRST = "HXFIRST";
    private static final String TEXT_SIZE = "TEXT_SIZE";
    private static FileCache sInstance;
    private SharedPreferences mPrefs;

    private FileCache(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        sInstance = new FileCache(context);
    }

    public static FileCache getInstance() {
        return sInstance;
    }


    /**
     * @param obj
     * @return
     * @throws Exception String
     * @Description：对象序列化成字符串，并对该字符串进行Base64编码 <p>
     * 创建人：yangningbo , 2013-9-4
     * 下午3:50:28
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-9-4
     * 下午3:50:28
     * </p>
     */
    private String obj2Str(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            throw e;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param str
     * @return
     * @throws Exception Object
     * @Description：对字符串进行Base64解码，并对该字符串反序列化成对象 <p>
     * 创建人：yangningbo , 2013-9-4
     * 下午3:49:14
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-9-4
     * 下午3:49:14
     * </p>
     */
    private Object str2Obj(String str) throws Exception {
        if (str == null) {
            throw new IllegalArgumentException("str can not null.");
        }

        ObjectInputStream ois = null;
        try {
            byte[] data = Base64.decode(str.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param obj
     * @return
     * @throws Exception String
     * @Description：对象序列化成字符串，并对该字符串DES加密，再进行Base64编码 <p>
     * 创建人：baichenxi , 2014-4-4
     * 下午3:49:14
     * </p>
     * <p>
     * 修改人：baichenxi , 2014-4-4
     * 下午3:49:14
     * </p>
     */
    public static String obj2EncStr(Object obj, String key) throws Exception {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] encStr = DesAlaorithm.symmetricEncrypto(baos.toByteArray(),
                    key);
            return new String(Base64.encode(encStr, Base64.DEFAULT));
        } catch (Exception e) {
            throw e;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param str
     * @return
     * @throws Exception Object
     * @Description：对字符串进行Base64解码,DES解密，并对该字符串反序列化成对象 <p>
     * 创建人：baichenxi , 2014-4-4
     * 下午3:49:14
     * </p>
     * <p>
     * 修改人：baichenxi , 2014-4-4
     * 下午3:49:14
     * </p>
     */
    public static Object encStr2Obj(String str, String key) throws Exception {
        if (str == null) {
            throw new IllegalArgumentException("str can not null.");
        }

        ObjectInputStream ois = null;
        try {

            byte[] data = Base64.decode(str.getBytes(), Base64.DEFAULT);
            byte[] decoder = DesAlaorithm.symmetricDecrypto(data, key);

            ByteArrayInputStream bais = new ByteArrayInputStream(decoder);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setLoginToken(String token) {
        Editor editor = mPrefs.edit();
        editor.putString(LOGINTOKEN, token);
        editor.commit();
    }

    public String getLogintoken() {
        return mPrefs.getString(LOGINTOKEN, "");
    }

    public void setExperience(int experience) {
        Editor editor = mPrefs.edit();
        editor.putInt(EXPERIENCE, experience);
        editor.commit();
    }

    public int getExperience() {
        return mPrefs.getInt(EXPERIENCE, 0);
    }

    public boolean hasPromptedAtClose() {
        return mPrefs.getBoolean(PROMPTCLOSE_AT, false);
    }

    public void setHasPromptedAt() {
        Editor editor = mPrefs.edit();
        editor.putBoolean(PROMPTCLOSE_AT, true);
        editor.commit();
    }

    public boolean hasPromptFavoriteClose() {
        return mPrefs.getBoolean(PROMPTCLOSE_FAVORITE, false);
    }

    public void setHasPromptFavorite() {
        Editor editor = mPrefs.edit();
        editor.putBoolean(PROMPTCLOSE_FAVORITE, true);
        editor.commit();
    }

    public boolean isGuideFirst() {
        return mPrefs.getBoolean(GUIDE_FIRST, true);
    }

    public void setHasGuide() {
        Editor editor = mPrefs.edit();
        editor.putBoolean(GUIDE_FIRST, false);
        editor.commit();
    }

    public void setHxUserId(String id) {
        Editor editor = mPrefs.edit();
        editor.putString(HX_USER_ID, id);
        editor.commit();
    }

    public void setHxPasword(String pasword) {
        Editor editor = mPrefs.edit();
        editor.putString(HX_PASSWORD, pasword);
        editor.commit();
    }

    public String getHxUserId() {
        return mPrefs.getString(HX_USER_ID, "");
    }

    public String getHxPassword() {
        return mPrefs.getString(HX_PASSWORD, "");
    }

    public void setKeyBordHeight(int keyBordHeight) {
        Editor editor = mPrefs.edit();
        editor.putInt(KEYBORDHEIGHT, keyBordHeight);
        editor.commit();
    }

    public int getKeyBordHeight() {
        return mPrefs.getInt(KEYBORDHEIGHT, 0);
    }

    public void setHxFirst(boolean flag) {
        Editor editor = mPrefs.edit();
        editor.putBoolean(HXFIRST, flag);
        editor.commit();
    }

    public boolean getHxFirst() {
        return mPrefs.getBoolean(HXFIRST, true);
    }

    public void setTextSize(int change) {
        Editor editor = mPrefs.edit();
        editor.putInt(TEXT_SIZE, change);
        editor.commit();
    }


    public int getTextSize() {
        return mPrefs.getInt(TEXT_SIZE, 0);
    }
}
