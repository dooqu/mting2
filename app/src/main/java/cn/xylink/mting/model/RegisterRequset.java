package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class RegisterRequset extends BaseRequest {

    private String phone;
    private String ticket;
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTicket() {
        return ticket;
    }


    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    @Override
    public String toString() {
        return "RegisterRequset{" +
                "phone='" + phone + '\'' +
                ", ticket='" + ticket + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
