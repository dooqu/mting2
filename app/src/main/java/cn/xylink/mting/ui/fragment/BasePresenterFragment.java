package cn.xylink.mting.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.presenter.BasePresenter;
import cn.xylink.mting.ui.dialog.LoadingDialog;


public abstract class BasePresenterFragment<T extends BasePresenter> extends BaseFragment implements IBaseView<T> {
    protected List<T> mPresenterList = new ArrayList<>();

    protected BasePresenter createPresenter(Class presentClass) {
        try {
            Object object = presentClass.newInstance();
            T instancePresenter = (T) object;
            mPresenterList.add(instancePresenter);
            return instancePresenter;
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDetach() {
        for (T item : mPresenterList) {
            if (item != null)
                item.deatchView();
        }
        super.onDetach();
    }

    private LoadingDialog mLoading;

    @Override
    public void showLoading() {
        if (mLoading == null)
            mLoading = new LoadingDialog(this.getActivity());
        if (!mLoading.isShowing())
            mLoading.show();
    }

    @Override
    public void hideLoading() {
        if (mLoading != null)
            mLoading.dismiss();
    }

}
