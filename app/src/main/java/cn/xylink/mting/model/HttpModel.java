package cn.xylink.mting.model;

/**
 * Created by liuhe. on Date: 2019/7/2
 */
public class HttpModel {

    public IModel iModel;
    public Class<? extends IModel> clazz;

    public HttpModel(IModel iModel, Class<? extends IModel> clazz) {
        this.iModel = iModel;
        this.clazz = clazz;
    }
}
