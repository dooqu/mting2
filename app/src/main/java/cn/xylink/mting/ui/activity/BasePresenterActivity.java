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
import cn.xylink.mting.ui.dialog.TipDialog;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.StringUtil;


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
        L.v();
        if (alertDialog == null || (alertDialog != null && !alertDialog.isShowing())) {
            showCopyDialog();
        }
    }

    private TipDialog alertDialog;

    protected void showShareResultDialog(int sucess, String shareUrl) {
        if (sucess >= 0) {
            String msg;
            if (sucess == 1) {
                msg = "分享成功";
                if (!TextUtils.isEmpty(shareUrl)) {
                    if (tCopy == null)
                        tCopy = new ArrayList<>();
                    tCopy.add(shareUrl);
                    if (tCopy.size() > 20)
                        tCopy.remove(0);
                    ContentManager.getInstance().setCopyArray(tCopy);
                }
            } else {
                msg = "分享失败";
            }
            alertDialog = new TipDialog(this);
            alertDialog.setMsg(msg, "返回", "留在轩辕听", new TipDialog.OnTipListener() {
                @Override
                public void onLeftClick() {
                    BasePresenterActivity.this.finish();
                }

                @Override
                public void onRightClick() {
                    showCopyDialog();
                }
            });
            alertDialog.show();
            L.v();
        }
    }

    private void showCopyDialog() {
        if (this.getComponentName().getClassName().equals(MainActivity.class.getName())
                || this.getComponentName().getClassName().equals(SearchActivity.class.getName())
                || this.getComponentName().getClassName().equals(ArticleDetailActivity.class.getName())) {
            CharSequence copyStr = getCopy(this);
//            if (!TextUtils.isEmpty(copy) && (copy.toString().startsWith("http://") || copy.toString().startsWith("https://"))) {
            if (!TextUtils.isEmpty(copyStr) && !TextUtils.isEmpty(StringUtil.matcherUrl(copyStr.toString()))
                    && !StringUtil.isShieldUrl(this, copyStr.toString())) {
                copyStr = StringUtil.matcherUrl(copyStr.toString());
                tCopy = ContentManager.getInstance().getCopyArray();
                if (tCopy != null && tCopy.size() > 0)
                    for (String s : tCopy) {
                        if (s.equals(copyStr)) {
                            return;
                        }
                    }
                if (tCopy == null)
                    tCopy = new ArrayList<>();
                tCopy.add(copyStr.toString());
                if (tCopy.size() > 20)
                    tCopy.remove(0);
                ContentManager.getInstance().setCopyArray(tCopy);
//                ContentManager.getInstance().addCopyItem(copy.toString());
//                tCopy = ContentManager.getInstance().getCopyArray();
                CopyAddDialog dialog = new CopyAddDialog(this, tCopy.get(tCopy.size() - 1));
                dialog.show();
            }
        }
    }

    private List<String> tCopy;

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
