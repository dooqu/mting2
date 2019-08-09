package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class UpdateUserRequset extends BaseRequest {

    private String nickName;

    @Override
    public String  toString() {
        return "UpdateUserRequset{" +
                "nickName='" + nickName + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", sex=" + sex +
                '}';
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    private long birthdate;
    private int sex;
}
