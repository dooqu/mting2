package cn.xylink.mting.ui.dialog;

import android.content.Context;
import android.view.View;

import cn.xylink.mting.R;


/*
 *loading
 *
 * -----------------------------------------------------------------
 * 2019/4/4 15:34 : Create LoadingDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class LoadingDialog extends BaseDimDialog {
    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_loading, null);
    }

    @Override
    public void onBackPressed() {
    }
}
