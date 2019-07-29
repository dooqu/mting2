package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import cn.xylink.mting.R;

/**
 * Created by wjn on 2018/12/26.
 */

public class AgreementDialog extends Dialog implements View.OnClickListener {
    private OnConfirmClickListener mConfirmClickListener;


    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.mConfirmClickListener = onConfirmClickListener;
    }

    public AgreementDialog(@NonNull Context context) {
        super(context, R.style.dialogBase);
    }

    public interface OnConfirmClickListener {
        void setConfirmClickListener();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agreement);
        TextView tv_cancel_dialog = findViewById(R.id.tv_cancel_dialog);
        TextView tv_confirm_dialog = findViewById(R.id.tv_confirm_dialog);
        tv_cancel_dialog.setOnClickListener(this);
        tv_confirm_dialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm_dialog:
                if (null != mConfirmClickListener) {
                    mConfirmClickListener.setConfirmClickListener();
                }
                break;
            case R.id.tv_cancel_dialog:
                System.exit(0);
                break;
        }
        dismiss();
    }
}
