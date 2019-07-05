package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import butterknife.ButterKnife;
import cn.xylink.mting.R;

/*
 *暗背景dialog基类
 *
 * -----------------------------------------------------------------
 * 2019/3/1 13:43 : Create BaseDimDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public abstract class BaseDimDialog extends Dialog {

    protected Context mContext;
    protected boolean b;

    public BaseDimDialog(Context context) {
        super(context, R.style.base_dim_dialog);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = getLayout();
        setContentView(v);
        ButterKnife.bind(this, v);
        initDialogSize();
        initView();
    }

    public BaseDimDialog(Context context, boolean b) {
        super(context, R.style.base_dim_dialog);
        this.b = b;
        init(context);
    }

    public void initView() {

    }

    protected abstract View getLayout();

    protected void initDialogSize() {
//        Window dialogWindow = this.getWindow();
//        dialogWindow.setStatusBarColor(mContext.getResources().getColor(R.color.white));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        dialogWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
//        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.dimAmount = 0.4f;
//        dialogWindow.setAttributes(lp);
    }
}
