package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import cn.xylink.mting.R;

public class ArticleDetailFont extends ArticleDetailBottomDialog {
    private FontClickListener listener;
    private int textSize;

    public ArticleDetailFont(FontClickListener listener) {
        this.listener = listener;
    }


    @Override
    public View initView(Context context, Dialog dialog) {
        View view = View.inflate(context, R.layout.dialog_font, null);
        View v1 = view.findViewById(R.id.iv_small);
        View v2 = view.findViewById(R.id.iv_normal);
        View v3 = view.findViewById(R.id.iv_large);

        v1.setVisibility(textSize == 0 ? View.VISIBLE : View.INVISIBLE);
        v2.setVisibility(textSize == 1 ? View.VISIBLE : View.INVISIBLE);
        v3.setVisibility(textSize == 2 ? View.VISIBLE : View.INVISIBLE);
        if (listener != null) {
            view.findViewById(R.id.rl_small).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.INVISIBLE);
                    v3.setVisibility(View.INVISIBLE);
                    listener.onFontChange(0);
                }
            });
            view.findViewById(R.id.rl_normal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v1.setVisibility(View.INVISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    v3.setVisibility(View.INVISIBLE);
                    listener.onFontChange(1);
                }
            });
            view.findViewById(R.id.rl_large).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v1.setVisibility(View.INVISIBLE);
                    v2.setVisibility(View.INVISIBLE);
                    v3.setVisibility(View.VISIBLE);
                    listener.onFontChange(2);
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

    public void getTextSize(int textSize) {
        this.textSize = textSize;
    }

    public interface FontClickListener {
        void onFontChange(int change);
    }
}
