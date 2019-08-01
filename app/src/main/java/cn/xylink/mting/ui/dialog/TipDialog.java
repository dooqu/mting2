package cn.xylink.mting.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import cn.xylink.mting.R;

/*
 *提示对话框
 *
 * -----------------------------------------------------------------
 * 2019/7/31 17:41 : Create TipDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class TipDialog implements View.OnClickListener {
    private AlertDialog alertDialog;
    AlertDialog.Builder builder;
    private TextView msgTextView;
    private TextView cancelTextView;
    private TextView sureTextView;
    private OnTipListener mListener;

    public TipDialog(@NonNull Context context) {
        builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_tip, null);
        msgTextView = view.findViewById(R.id.tv_tip_dialog_msg);
        cancelTextView = view.findViewById(R.id.tv_tip_dialog_cancel);
        sureTextView = view.findViewById(R.id.tv_tip_dialog_sure);
        msgTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        sureTextView.setOnClickListener(this);
        builder.setView(view);

    }

    public void setMsg(String msg, String cancel, String sure, OnTipListener listener) {
        msgTextView.setText(msg);
        cancelTextView.setText(cancel);
        sureTextView.setText(sure);
        mListener = listener;
    }

    public void show() {
        alertDialog = builder.show();
    }

    public void dissmiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    public boolean isShowing() {
        return alertDialog != null ? alertDialog.isShowing() : false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tip_dialog_cancel:
                if (mListener != null)
                    mListener.onLeftClick();
                break;
            case R.id.tv_tip_dialog_sure:
                if (mListener != null)
                    mListener.onRightClick();
                break;
        }
        dissmiss();
    }

    public interface OnTipListener {
        void onLeftClick();

        void onRightClick();
    }
}
