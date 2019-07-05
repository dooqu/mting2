package cn.xylink.mting.bean;

import java.io.Serializable;

/**
 * Created by wjn on 2019/2/28.
 */

public class LoginInfo implements Serializable {

    /**
     * userId : u1
     * phone : 15811372713
     * nickName : 何超杰
     * headImg : http://
     * headThumb : null
     * sex : 1
     * status : 0
     * regSource : null
     * token : ce17955881e54cc7822c14e529cb5e46
     * createAt : 1551239154105
     * birthdate : 112
     * provinceId : null
     * province : 安徽
     * cityId : null
     * city : 合肥
     * experience : 1
     */

    private String userId;
    private String phone;
    private String nickName;
    private String headImg;
    private Object headThumb;
    private int sex;
    private int status;
    private Object regSource;
    private String token;
    private long createAt;
    private int birthdate;
    private Object provinceId;
    private String province;
    private Object cityId;
    private String city;
    private int experience;
    private String hxUserId;
    private String hxPasswd;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public Object getHeadThumb() {
        return headThumb;
    }

    public void setHeadThumb(Object headThumb) {
        this.headThumb = headThumb;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getRegSource() {
        return regSource;
    }

    public void setRegSource(Object regSource) {
        this.regSource = regSource;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public int getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(int birthdate) {
        this.birthdate = birthdate;
    }

    public Object getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Object provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Object getCityId() {
        return cityId;
    }

    public void setCityId(Object cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getHxUserId() {
        return hxUserId;
    }

    public void setHxUserId(String hxUserId) {
        this.hxUserId = hxUserId;
    }

    public String getHxPasswd() {
        return hxPasswd;
    }

    public void setHxPasswd(String hxPasswd) {
        this.hxPasswd = hxPasswd;
    }
}
