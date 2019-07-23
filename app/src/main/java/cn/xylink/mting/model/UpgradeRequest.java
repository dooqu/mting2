package cn.xylink.mting.model;

import cn.xylink.mting.base.BaseRequest;

public class UpgradeRequest extends BaseRequest {
    //protected String sn;
    //protected String vin;
    protected String channel;
    protected String appPackage;
    protected String appVersion;
    protected String versionId;


    public UpgradeRequest() {

    }

    /*

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVin() {
        return vin;
    }
    */

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }


    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionId() {
        return versionId;
    }
}
