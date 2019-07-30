package cn.xylink.mting.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.base.BaseActivity;
import cn.xylink.mting.contract.IBaseView;
import cn.xylink.mting.presenter.BasePresenter;
import cn.xylink.mting.ui.dialog.CopyAddDialog;
import cn.xylink.mting.ui.dialog.LoadingDialog;
import cn.xylink.mting.utils.L;


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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoading == null)
                    mLoading = new LoadingDialog(BasePresenterActivity.this);
                if (!mLoading.isShowing())
                    mLoading.show();
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        showCopyDialog();
    }

    private void showCopyDialog() {
        if (this.getComponentName().getClassName().equals(MainActivity.class.getName())
                || this.getComponentName().getClassName().equals(SearchActivity.class.getName())
                || this.getComponentName().getClassName().equals(ArticleDetailActivity.class.getName())) {
            CharSequence copy = getCopy(this);
            if (!TextUtils.isEmpty(copy) && (copy.toString().startsWith("http://") || copy.toString().startsWith("https://"))) {
                for (String s : tCopy)
                    if (s.equals(copy.toString()))
                        return;
                tCopy.add(copy.toString());
                CopyAddDialog dialog = new CopyAddDialog(this, tCopy.get(tCopy.size() - 1));
                dialog.show();
            }
        }
    }

    private static List<String> tCopy = new ArrayList<>();

    public static CharSequence getCopy(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            CharSequence str = clipData.getItemAt(0).getText();
            return str;
        }
        return null;
    }
}
