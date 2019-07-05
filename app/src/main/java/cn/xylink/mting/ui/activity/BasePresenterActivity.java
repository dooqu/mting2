package cn.xylink.mting.ui.activity;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.presenter.BasePresenter;
import cn.xylink.mting.ui.dialog.LoadingDialog;


public abstract class BasePresenterActivity<T extends BasePresenter> extends BaseActivity implements IBaseView<T> {
    protected List<T> mPresenterList = new ArrayList<>();

    protected BasePresenter createPresenter(Class presentClass) {
        try {
            Object object = presentClass.newInstance();
            T instancePresenter = (T) object;
            mPresenterList.add(instancePresenter);
            return instancePresenter;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        for (T item : mPresenterList) {
            if (item != null)
                item.deatchView();
        }
        if (mLoading != null)
            mLoading.dismiss();
        super.onDestroy();
    }

    private LoadingDialog mLoading;

    @Override
    public void showLoading() {
        if (mLoading == null)
            mLoading = new LoadingDialog(this);
        if (!mLoading.isShowing())
            mLoading.show();
    }

    @Override
    public void hideLoading() {
        if (mLoading != null)
            mLoading.dismiss();
    }

    /**
     * 弹出一个3s显示的toast框
     */
    @Override
    public void toastShort(String msg) {
        cn.xylink.mting.utils.T.showCustomToast(msg);
    }
}
