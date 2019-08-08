package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import cn.xylink.mting.R;

public class ArticleDetailShare extends ArticleDetailBottomDialog {

    private ShareClickListener listener;

    public ArticleDetailShare(ShareClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View initView(Context context, Dialog dialog) {
        View view = View.inflate(context, R.layout.dialog_share, null);
        if (listener != null) {
            view.findViewById(R.id.ll_wx).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareChange(0);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            view.findViewById(R.id.ll_wx_quan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareChange(1);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            view.findViewById(R.id.ll_qq).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareChange(2);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            view.findViewById(R.id.ll_qq_space).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareChange(3);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            view.findViewById(R.id.ll_link).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareChange(4);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        return view;
    }

    public interface ShareClickListener {
        void onShareChange(int i);
    }
}
