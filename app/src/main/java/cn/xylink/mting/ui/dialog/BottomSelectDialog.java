package cn.xylink.mting.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;

/*
 *底部选择通用dialog
 *
 * -----------------------------------------------------------------
 * 2019/3/1 13:54 : Create BottomSelectDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class BottomSelectDialog extends BaseDimDialog {

    @BindView(R.id.btn_bottom_select_first)
    Button mFirstButton;
    @BindView(R.id.btn_bottom_select_second)
    Button mSecondButton;
    private OnBottomSelectDialogListener mListener;

    public BottomSelectDialog(Context context) {
        super(context);
    }

    public void setData(String first, String second, OnBottomSelectDialogListener listener) {
        mFirstButton.setText(first);
        mSecondButton.setText(second);
        this.mListener = listener;
    }

    public void setData(String first, String second,int joinLimit, OnBottomSelectDialogListener listener) {
        mFirstButton.setText(first);
        mSecondButton.setText(second);
        this.mListener = listener;
        if (joinLimit==0){
            mSecondButton.setTextColor(0xff666666);
            mFirstButton.setTextColor(0xff007b7d);
            mFirstButton.setTextSize(19);
        }else {
            mFirstButton.setTextColor(0xff666666);
            mSecondButton.setTextColor(0xff007b7d);
            mSecondButton.setTextSize(19);
        }
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.bottom_select_dialog, null);
    }

    @OnClick({R.id.btn_bottom_select_first, R.id.btn_bottom_select_second, R.id.btn_bottom_select_cancel})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bottom_select_first:
                mListener.onFirstClick();
                break;
            case R.id.btn_bottom_select_second:
                mListener.onSecondClick();
                break;
            case R.id.btn_bottom_select_cancel:
                break;
        }
        BottomSelectDialog.this.dismiss();
    }

    public interface OnBottomSelectDialogListener {
        void onFirstClick();

        void onSecondClick();
    }

}
