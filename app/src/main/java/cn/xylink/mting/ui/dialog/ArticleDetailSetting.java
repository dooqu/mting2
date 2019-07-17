package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import cn.xylink.mting.R;
import cn.xylink.mting.speech.SpeechService;
import cn.xylink.mting.speech.Speechor;
import cn.xylink.mting.ui.activity.ArticleDetailActivity;
import cn.xylink.mting.utils.ContentManager;

public class ArticleDetailSetting extends ArticleDetailBottomDialog {

    private SettingListener listener;
    private Speechor.SpeechorSpeed speed;
    private SpeechService.CountDownMode countDownMode;
    private int countDownValue;

    public ArticleDetailSetting(SettingListener listener) {
        this.listener = listener;
    }

    @Override
    public View initView(Context context, Dialog dialog) {
        View view = View.inflate(context, R.layout.dialog_setting, null);
        RadioGroup rgSpeed = view.findViewById(R.id.rg_speed);
        switch (speed) {
            case SPEECH_SPEED_NORMAL:
                rgSpeed.check(R.id.rb_normal);
                break;
            case SPEECH_SPEED_MULTIPLE_1_POINT_5:
                rgSpeed.check(R.id.rb_1_5);
                break;
            case SPEECH_SPEED_MULTIPLE_2:
                rgSpeed.check(R.id.rb_2);
                break;
            case SPEECH_SPEED_MULTIPLE_2_POINT_5:
                rgSpeed.check(R.id.rb_2_5);
                break;
        }
        RadioGroup rgCountDown = view.findViewById(R.id.rg_count_down);
        Switch swCount = view.findViewById(R.id.sw_count);
        if (countDownMode == SpeechService.CountDownMode.None) {
            swCount.setChecked(false);
        } else if (countDownMode == SpeechService.CountDownMode.NumberCount) {
            rgCountDown.check(R.id.rb_current);
        } else {
            rgCountDown.check(R.id.rb_current);
            swCount.setChecked(true);
        }
        rgCountDown.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (listener == null) {
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_current:
                        ContentManager.getInstance().setRgTime(1);
                        swCount.setChecked(true);
                        listener.onTime(1);
                        break;
                    case R.id.rb_time10:
                        ContentManager.getInstance().setRgTime(2);
                        swCount.setChecked(true);
                        listener.onTime(2);
                        break;
                    case R.id.rb_time20:
                        ContentManager.getInstance().setRgTime(3);
                        swCount.setChecked(true);
                        listener.onTime(3);
                        break;
                    case R.id.rb_time30:
                        ContentManager.getInstance().setRgTime(4);
                        swCount.setChecked(true);
                        listener.onTime(4);
                        break;
                }
            }
        });
        swCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ContentManager.getInstance().setRgTime(0);
                    rgCountDown.check(-1);
                    listener.onTime(0);
                }
                buttonView.setChecked(isChecked);
            }
        });
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

    public void setSpeed(Speechor.SpeechorSpeed speed) {
        this.speed = speed;
    }

    public void setCountDown(SpeechService.CountDownMode countDownMode, int countDownValue) {
        this.countDownMode = countDownMode;
        this.countDownValue = countDownValue;
    }

    public interface SettingListener {
        void onSpeed(int speed);

        void onTime(int time);
    }
}
