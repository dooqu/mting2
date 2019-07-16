package cn.xylink.mting.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;

public class CheckArticleDialog extends BaseDimDialog {
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.bt_ok)
    Button btOk;
    @BindView(R.id.tv_update_msg)
    TextView tvUpdateMsg;

    public CheckArticleDialog(Context context) {
        super(context);
    }

    public void setButton(String strCancel, String strOk) {
        if (!TextUtils.isEmpty(strCancel)) {
            btCancel.setText(strCancel);
        }
        if (!TextUtils.isEmpty(strOk)) {
            btOk.setText(strOk);
        }
    }

    public void setUpdateMsg(String text)
    {
        tvUpdateMsg.setText(text);
    }


    public void setData(String message, MessageListener listener) {
        tvMessage.setText(message);
        this.mListener = listener;
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_check_article_message, null);
    }

    @OnClick({R.id.bt_ok, R.id.bt_cancel,R.id.iv_close})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                mListener.onUpdate();
                break;
            case R.id.bt_cancel:
                mListener.onLook();
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
        CheckArticleDialog.this.dismiss();
    }

    private MessageListener mListener;

    public interface MessageListener {
        void onUpdate();
        void onLook();
    }
}
