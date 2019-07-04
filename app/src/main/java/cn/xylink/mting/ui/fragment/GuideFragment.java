package cn.xylink.mting.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import cn.xylink.mting.R;

public class GuideFragment extends BaseFragment {
    private final static String TITLE_ID = "TITLE_ID";
    private final static String MSG_ID = "MSG_ID";
    private final static String IMAGE_ID = "IMAGE_ID";

    @BindView(R.id.tv_guide_title)
    TextView mGuideTitleView;
    @BindView(R.id.tv_guide_msg)
    TextView mGuideMsgView;
    @BindView(R.id.iv_guide_bitmap)
    ImageView mGuidePromptView;

//    private AnimationDrawableUtils animationDrawableUtils;

    public static GuideFragment newInstance(int titleId, int msgId, int imageId) {
        GuideFragment guideFragment = new GuideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TITLE_ID, titleId);
        bundle.putInt(MSG_ID, msgId);
        bundle.putInt(IMAGE_ID, imageId);
        guideFragment.setArguments(bundle);
        return guideFragment;
    }


    @Override
    protected int getLayoutViewId() {
        return R.layout.guide_fragment;
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void initData() {
//        animationDrawableUtils = new AnimationDrawableUtils();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mGuideTitleView.setText(getString(bundle.getInt(TITLE_ID)));
            mGuideMsgView.setText(getString(bundle.getInt(MSG_ID)));
            mGuidePromptView.setImageResource(bundle.getInt(IMAGE_ID));
//            animationDrawableUtils.animateRawManuallyFromXML(bundle.getInt(IMAGE_ID), mGuidePromptView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        animationDrawableUtils.stopAnimation(true);
    }
}
