package cn.xylink.mting.ui.fragment;

import android.content.Context;

import java.util.List;


/*
 *主页tab页基类
 *
 * -----------------------------------------------------------------
 * 2019/7/9 14:50 : Create BaseMainTabFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public abstract class BaseMainTabFragment extends BasePresenterFragment {
    protected OnControllerListener mControllerListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mControllerListener = (OnControllerListener)context;
    }

    public interface OnControllerListener{
        void onPlay(String id);
        void onDelete(List<String> list);
    }

}
