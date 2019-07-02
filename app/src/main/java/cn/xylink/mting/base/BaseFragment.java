package cn.xylink.mting.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import cn.xylink.mting.R;
import cn.xylink.mting.utils.T;

public abstract class BaseFragment extends Fragment {

    protected LayoutInflater inflater;
    protected InputMethodManager imm;
    //    protected Dialog dialog;
    private Handler mDataSetHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onDataReceiver(msg.obj);
        }
    };

    protected abstract void onDataReceiver(Object obj);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(getViewRes(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
    }

    protected abstract int getViewRes();

    protected abstract void initView(View view);

    protected abstract void initData();

    public void refreshMore(String id){}
    /**
     * 弹出一个6s显示的toast框
     */
    public void toastLong(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出一个6s显示的toast框
     */
    public void toastLong(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出一个3s显示的toast框
     */
    public void toastShort(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出一个3s显示的toast框
     */
    public void toastShort(String msg) {
        T.showCustomToast(msg);//修改吐司格式 不显示app名称
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    protected void showInputMethod(View view) {
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    protected void hideInputMethod(View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
    }


    public void setData(Object obj) {
        Message obtain = Message.obtain();
        obtain.obj = obj;
        mDataSetHandle.sendMessage(obtain);
    }


}
