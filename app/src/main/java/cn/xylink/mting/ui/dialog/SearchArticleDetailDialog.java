package cn.xylink.mting.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.ArticleDetailInfo;
import cn.xylink.mting.utils.DensityUtil;

/*
 *搜索页查看原文
 *
 * -----------------------------------------------------------------
 * 2019/7/19 11:08 : Create SearchArticleDetailDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class SearchArticleDetailDialog extends Dialog {

    @BindView(R.id.tv_search_dialog_title)
    TextView mTitleTextView;
    @BindView(R.id.tv_search_dialog_from)
    TextView mFromTextView;
    @BindView(R.id.tv_search_dialog_contact)
    TextView mContactTextView;
    private OnBottomSelectDialogListener mListener;

    public SearchArticleDetailDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        View purchase = LayoutInflater.from(context).inflate(R.layout.dialog_search, null);
        this.setCancelable(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = DensityUtil.dip2px(context, 550);
        window.setAttributes(lp);
        this.setContentView(purchase);
        ButterKnife.bind(this, purchase);
    }

    @OnClick({R.id.iv_search_dialog_close, R.id.tv_search_dialog_add_unread, R.id.tv_search_dialog_play})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search_dialog_close:
                break;
            case R.id.tv_search_dialog_add_unread:
                if (mListener!=null) {
                    mListener.onAddUnrad(mData);
                }
                break;
            case R.id.tv_search_dialog_play:
                if (mListener!=null) {
                    mListener.onPlay(mData);
                }
                break;
        }
        this.dismiss();
    }

    public void setListener(OnBottomSelectDialogListener listener){
        this.mListener = listener;
    }

    private ArticleDetailInfo mData;
    public void show(ArticleDetailInfo info) {
        mData = info;
        mTitleTextView.setText(info.getTitle());
        mFromTextView.setText(TextUtils.isEmpty(info.getSourceName()) ? "其他" : info.getSourceName());
        mContactTextView.setText(info.getContent());
        super.show();
    }

    public interface OnBottomSelectDialogListener {
        void onAddUnrad(ArticleDetailInfo info);
        void onPlay(ArticleDetailInfo info);
    }
}
