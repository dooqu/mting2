package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import cn.xylink.mting.R;

public class ArticleDetailShare extends ArticleDetailBottomDialog{
    @Override
    public View initView(Context context, Dialog dialog) {
        View view = View.inflate(context, R.layout.dialog_share, null);
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
}
