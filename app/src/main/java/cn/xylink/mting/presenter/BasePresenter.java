package cn.xylink.mting.presenter;

import java.lang.ref.WeakReference;

import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.model.data.OkGoUtils;

public class BasePresenter<V extends IBaseView> {
    private WeakReference<V> weakReference;
    protected V mView;

    public V getmView() {
        return mView;
    }

    public void attachView(V view) {
        this.weakReference = new WeakReference<>(view);
        mView = weakReference.get();
    }

    public void deatchView() {
        OkGoUtils.getInstance().cancel(mView);
        if (weakReference != null)
            weakReference.clear();
        weakReference = null;
        mView = null;

    }

    boolean isViewAttached() {
        return mView != null;
    }

}
