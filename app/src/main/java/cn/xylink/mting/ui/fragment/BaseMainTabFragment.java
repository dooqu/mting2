package cn.xylink.mting.ui.fragment;

import android.content.Context;

import java.util.List;

import cn.xylink.mting.ui.dialog.MainListMenuDialog;


/*
 *主页tab页基类
 *
 * -----------------------------------------------------------------
 * 2019/7/9 14:50 : Create BaseMainTabFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public abstract class BaseMainTabFragment extends BasePresenterFragment {
    protected OnControllerListener mControllerListener;
    protected MainListMenuDialog mBottomDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mControllerListener = (OnControllerListener)context;
    }

    public interface OnControllerListener{
        void onPlay(String id);
        void onDelete(List<String> list);
        void onDataSuccess();
    }

    protected void showBottonDialog(){
        mBottomDialog = new MainListMenuDialog(getActivity());
        mBottomDialog.show();
    }

}
