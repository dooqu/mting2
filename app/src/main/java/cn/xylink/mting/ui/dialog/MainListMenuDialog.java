package cn.xylink.mting.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.openapi.WXapi;

/*
 *底部选择通用dialog,2item
 *
 * -----------------------------------------------------------------
 * 2019/5/22 16:04 : Create BottomSelect2Dialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class MainListMenuDialog extends BaseDimDialog {

    private OnBottomSelectDialogListener mListener;

    public MainListMenuDialog(Context context) {
        super(context);
    }

    public void setData(String first, OnBottomSelectDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_main_list_menu, null);
    }

    @OnClick({R.id.v_dialog_main_list_menu_out, R.id.tv_dialog_main_list_menu_wx, R.id.tv_dialog_main_list_menu_wxp,
            R.id.tv_dialog_main_list_menu_qq, R.id.tv_dialog_main_list_menu_quen, R.id.tv_dialog_main_list_menu_copy,
            R.id.tv_dialog_main_list_menu_love, R.id.tv_dialog_main_list_menu_del, R.id.tv_dialog_main_list_menu_cancel})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_dialog_main_list_menu_out:
                break;
            case R.id.tv_dialog_main_list_menu_wx:
                WXapi.shareWx((Activity) mContext,"111",null,"nihao","dddd");
                break;
            case R.id.tv_dialog_main_list_menu_wxp:
                break;
            case R.id.tv_dialog_main_list_menu_qq:
                break;
            case R.id.tv_dialog_main_list_menu_quen:
                break;
            case R.id.tv_dialog_main_list_menu_copy:
                break;
            case R.id.tv_dialog_main_list_menu_love:
                break;
            case R.id.tv_dialog_main_list_menu_del:
                break;
            case R.id.tv_dialog_main_list_menu_cancel:
                break;
        }
        MainListMenuDialog.this.dismiss();
    }

    public interface OnBottomSelectDialogListener {
        void onFirstClick();
    }

}
