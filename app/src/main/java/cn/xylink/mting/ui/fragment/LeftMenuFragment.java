package cn.xylink.mting.ui.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.ui.activity.AboutVersion;
import cn.xylink.mting.ui.activity.LoginActivity;
import cn.xylink.mting.ui.activity.PersonalInfoActivity;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.ImageUtils;
import cn.xylink.mting.utils.L;

/*
 *左侧菜单
 *
 * -----------------------------------------------------------------
 * 2019/7/11 15:47 : Create LeftMenuFragment.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class LeftMenuFragment extends BasePresenterFragment {

    @BindView(R.id.iv_left_menu_head)
    ImageView mHeadImageView;
    @BindView(R.id.tv_left_menu_title)
    TextView mTitleView;
    @BindView(R.id.tv_left_menu_out)
    TextView mLogoutView;
    @BindView(R.id.rl_left_menu_feedback)
    RelativeLayout mFeedBackLayout;
    @BindView(R.id.rl_left_menu_share)
    RelativeLayout mShareLayout;
    @BindView(R.id.rl_left_menu_fun)
    RelativeLayout mFunLayout;
    @BindView(R.id.rl_left_menu_about)
    RelativeLayout mAboutLayout;

    @Override
    protected int getLayoutViewId() {
        return R.layout.fragment_left_menu;
    }

    @Override
    protected void initView(View view) {
    }

    public void setUserInfo()
    {
        UserInfo info = ContentManager.getInstance().getUserInfo();
        L.v(info);
        if (info != null) {
            if (!TextUtils.isEmpty(info.getHeadImg()))
                ImageUtils.get().load(mHeadImageView, R.mipmap.icon_head_default, R.mipmap.icon_head_default, 90, info.getHeadImg());
            if (!TextUtils.isEmpty(info.getNickName()))
                mTitleView.setText(info.getNickName());
            if (info.getSex() == 0)
                mTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.mipmap.icon_my_women),
                        null);
            if (info.getSex() == 1)
                mTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.mipmap.icon_my_man),
                        null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }

    @Override
    protected void initData() {
    }



    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @OnClick({R.id.rl_left_menu_about, R.id.rl_left_menu_feedback, R.id.rl_left_menu_fun, R.id.rl_left_menu_share, R.id.tv_left_menu_out,
            R.id.tv_left_menu_title, R.id.iv_left_menu_head, R.id.ll_left_menu_layout})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_menu_head: //头像
            case R.id.tv_left_menu_title://姓名
                Intent intent = new Intent(this.getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_left_menu_share://分享
                break;
            case R.id.rl_left_menu_feedback://反馈
                break;
            case R.id.rl_left_menu_fun://玩转
                break;
            case R.id.rl_left_menu_about://关于
                startActivity(new Intent(getActivity(), AboutVersion.class));
                break;
            case R.id.tv_left_menu_out://退出
                ContentManager.getInstance().setUserInfo(null);
                ContentManager.getInstance().setLoginToken("");
                Intent intents = new Intent(getActivity(), LoginActivity.class);
                intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intents);
                break;
        }
    }


}
