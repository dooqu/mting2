package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.xylink.mting.R;

public abstract class ArticleDetailBottomDialog {

    private Dialog mShareDialog;

    /**
     * 显示分享弹出框
     */
    public void showDialog(Context context) {
        if (mShareDialog != null && mShareDialog.isShowing()) {
            mShareDialog.dismiss();
        }
        initShareDialog(context);
        mShareDialog.show();
    }

    /**
     * 初始化分享弹出框
     */
    private   void initShareDialog(Context context) {
        mShareDialog = new Dialog(context, R.style.dialog_bottom_full);
        mShareDialog.setCanceledOnTouchOutside(true);
        mShareDialog.setCancelable(true);
        Window window = mShareDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        View view = initView(context,mShareDialog);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    public abstract View initView(Context context, Dialog dialog) ;
}
