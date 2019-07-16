package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import cn.xylink.mting.R;
import cn.xylink.mting.ui.activity.ArticleDetailActivity;

public class ArticleDetailSetting extends ArticleDetailBottomDialog {

    private SettingListener listener;

    public ArticleDetailSetting(SettingListener listener) {
        this.listener = listener;
    }

    @Override
    public View initView(Context context, Dialog dialog) {
        View view = View.inflate(context, R.layout.dialog_setting, null);
        RadioGroup rgSpeed = view.findViewById(R.id.rg_speed);
        rgSpeed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (listener == null) {
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_normal:
                        listener.onSpeed(0);
                        break;
                    case R.id.rb_1_5:
                        listener.onSpeed(1);
                        break;
                    case R.id.rb_2:
                        listener.onSpeed(2);
                        break;
                    case R.id.rb_2_5:
                        listener.onSpeed(3);
                        break;
                }
            }
        });

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

    public interface SettingListener {
        void onSpeed(int speed);

        void onTime(int time);
    }
}
