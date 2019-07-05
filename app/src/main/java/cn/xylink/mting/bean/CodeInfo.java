package cn.xylink.mting.bean;

public class CodeInfo {

    private String codeId;
    private String createAt;

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }


    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }


    @Override
    public String toString() {
        return "CodeInfo{" +
                "codeId='" + codeId + '\'' +
                ", createAt='" + createAt + '\'' +
                '}';
    }
}
