package cn.xylink.mting.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xylink.mting.R;


/**
 * 类：PhoneCode
 * 作者： qxc
 * 日期：2018/3/14.
 */
public class PhoneCode extends RelativeLayout {
    private Context context;
    private TextView tv_code1;
    private TextView tv_code2;
    private TextView tv_code3;
    private TextView tv_code4;
    private View v1;
    private View v2;
    private View v3;
    private View v4;
    private EditText et_code;
    private List<String> codes = new ArrayList<>();
    private InputMethodManager imm;
    private Listener listener;

    public PhoneCode(Context context) {
        super(context);
        this.context = context;
        loadView();
    }

    public PhoneCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        loadView();
    }

    public void setOnCompleteListener(Listener listener) {
        this.listener = listener;
    }

    private void loadView() {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.phone_code, this);
        initView(view);
        initEvent();
        showSoftInput();
    }

    private void initView(View view) {
        tv_code1 = (TextView) view.findViewById(R.id.tv_code1);
        tv_code2 = (TextView) view.findViewById(R.id.tv_code2);
        tv_code3 = (TextView) view.findViewById(R.id.tv_code3);
        tv_code4 = (TextView) view.findViewById(R.id.tv_code4);
        et_code = (EditText) view.findViewById(R.id.et_code);
        et_code.requestFocus();
        v1 = view.findViewById(R.id.v1);
        v2 = view.findViewById(R.id.v2);
        v3 = view.findViewById(R.id.v3);
        v4 = view.findViewById(R.id.v4);
    }

    private void initEvent() {

        addTextWatcher();

    }

    public void addTextWatcher() {
        //验证码输入
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.length() > 0) {
                    et_code.setText("");
                    if (codes.size() < 4) {
                        codes.add(editable.toString());
                        showCode();
                        if (listener != null && codes.size() == 4) {
                            listener.onComplete(getPhoneCode());
                            setEnabled(false);
                        }
                    }
                }
            }
        });
        // 监听验证码删除按键
        et_code.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN && codes.size() > 0) {
                    codes.remove(codes.size() - 1);
                    showCode();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 显示输入的验证码
     */
    private void showCode() {
        String code1 = "";
        String code2 = "";
        String code3 = "";
        String code4 = "";
        if (codes.size() >= 1) {
            code1 = codes.get(0);
        }
        if (codes.size() >= 2) {
            code2 = codes.get(1);
        }
        if (codes.size() >= 3) {
            code3 = codes.get(2);
        }
        if (codes.size() >= 4) {
            code4 = codes.get(3);
        }
        tv_code1.setText(code1);
        tv_code2.setText(code2);
        tv_code3.setText(code3);
        tv_code4.setText(code4);
        setColor();
    }

    /**
     * 设置高亮颜色
     */
    private void setColor() {
        int color_default = R.color.color_blue_hint;
        int color_focus = R.color.color_blue;
        v1.setBackgroundColor(getResources().getColor(color_default));
        v2.setBackgroundColor(getResources().getColor(color_default));
        v3.setBackgroundColor(getResources().getColor(color_default));
        v4.setBackgroundColor(getResources().getColor(color_default));
        if (codes.size() == 1) {
            v1.setBackgroundColor(getResources().getColor(color_focus));
        }
        if (codes.size() == 2) {
//            v1.setBackgroundColor(getResources().getColor(color_focus));
            v2.setBackgroundColor(getResources().getColor(color_focus));
        }
        if (codes.size() == 3) {
//            v1.setBackgroundColor(getResources().getColor(color_focus));
//            v2.setBackgroundColor(getResources().getColor(color_focus));
            v3.setBackgroundColor(getResources().getColor(color_focus));
        }
        if (codes.size() >= 4) {
//            v1.setBackgroundColor(getResources().getColor(color_focus));
//            v2.setBackgroundColor(getResources().getColor(color_focus));
//            v3.setBackgroundColor(getResources().getColor(color_focus));
            v4.setBackgroundColor(getResources().getColor(color_focus));
        }
    }

    /**
     * 显示键盘
     */
    public void showSoftInput() {
        //显示软键盘
        if (imm != null && et_code != null) {
            et_code.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(et_code, 0);
                }
            }, 200);
        }
    }

    /**
     * 获得手机号验证码
     *
     * @return 验证码
     */
    public String getPhoneCode() {
        StringBuilder sb = new StringBuilder();
        for (String code : codes) {
            sb.append(code);
        }
        return sb.toString();
    }

    public interface Listener {
        void onComplete(String content);
    }

    /**
     * 清空输入框
     */
    public void clearText() {
        codes.clear();
        showCode();
    }
}
