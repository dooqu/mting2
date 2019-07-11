package cn.xylink.mting.bean;

import java.io.Serializable;

/**
 * 个人信息
 */
public class UserInfo implements Serializable {
    // 用户id
    private String userId;
    // 手机号
    private String phone;
    // 昵称
    private String nickName;
    // 头像原图
    private String headImg;
    // 头像缩略图
    private String headThumb;
    // 性别 1 男 0 女
    private int sex;
    private int status;
    private String regSource;
    private String token;
    private long createAt;
    private long birthdate;
    private String provinceId;
    private String province;
    private String cityId;
    private String city;
    // 公司
    private String company;
    // 关注数
    private long concernTotal;

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

    public String getHeadThumb() {
        return headThumb;
    }

    public void setHeadThumb(String headThumb) {
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

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
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

    public long getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", phone='" + phone + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headImg='" + headImg + '\'' +
                ", headThumb='" + headThumb + '\'' +
                ", sex=" + sex +
                ", status=" + status +
                ", regSource='" + regSource + '\'' +
                ", token='" + token + '\'' +
                ", createAt=" + createAt +
                ", birthdate=" + birthdate +
                ", provinceId='" + provinceId + '\'' +
                ", province='" + province + '\'' +
                ", cityId='" + cityId + '\'' +
                ", city='" + city + '\'' +
                ", company='" + company + '\'' +
                ", concernTotal=" + concernTotal +
                '}';
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getConcernTotal() {
        return concernTotal;
    }

    public void setConcernTotal(long concernTotal) {
        this.concernTotal = concernTotal;
    }
}
