package cn.xylink.mting.ui.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.ui.fragment.BaseMainTabFragment;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.T;

/*
 *条目菜单
 *
 * -----------------------------------------------------------------
 * 2019/5/22 16:04 : Create BottomSelect2Dialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class MainListMenuDialog extends BaseDimDialog {

    private OnBottomSelectDialogListener mListener;
    @BindView(R.id.tv_dialog_main_list_menu_love)
    TextView mLoveTextView;
    @BindView(R.id.tv_dialog_main_list_menu_del)
    TextView mDelTextView;

    public MainListMenuDialog(Context context) {
        super(context);
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.share_animation);
    }

    public void setListener(OnBottomSelectDialogListener listener) {
        this.mListener = listener;
    }

    private Article mArticle;
    private BaseMainTabFragment.TAB_TYPE mTabType;

    public void show(BaseMainTabFragment.TAB_TYPE tabType, Article article) {
        super.show();
        mTabType = tabType;
        mArticle = article;

        if (tabType == BaseMainTabFragment.TAB_TYPE.COLLECT)
            mDelTextView.setVisibility(View.INVISIBLE);
        if (mArticle != null && mArticle.getStore() == 1)
            mLoveTextView.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(R.mipmap.icon_f_loved), null, null);
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_main_list_menu, null);
    }

    @OnClick({R.id.v_dialog_main_list_menu_out, R.id.tv_dialog_main_list_menu_wx, R.id.tv_dialog_main_list_menu_wxp,
            R.id.tv_dialog_main_list_menu_qq, R.id.tv_dialog_main_list_menu_quen, R.id.tv_dialog_main_list_menu_copy,
            R.id.tv_dialog_main_list_menu_love, R.id.tv_dialog_main_list_menu_del, R.id.tv_dialog_main_list_menu_cancel})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_dialog_main_list_menu_out:
                break;
            case R.id.tv_dialog_main_list_menu_wx:
                TCAgent.onEvent(mContext,"article_more_wechat");
                if (mArticle != null && !TextUtils.isEmpty(mArticle.getShareUrl()))
                    WXapi.shareWx((Activity) mContext, mArticle.getShareUrl(), null, mArticle.getTitle(),
                            TextUtils.isEmpty(mArticle.getDescribe()) ? "" : mArticle.getDescribe());
                break;
            case R.id.tv_dialog_main_list_menu_wxp:
                TCAgent.onEvent(mContext,"article_more_wecha1");
                if (mArticle != null && !TextUtils.isEmpty(mArticle.getShareUrl()))
                    WXapi.sharePyq((Activity) mContext, mArticle.getShareUrl(), null, mArticle.getTitle(),
                            TextUtils.isEmpty(mArticle.getDescribe()) ? "" : mArticle.getDescribe());
                break;
            case R.id.tv_dialog_main_list_menu_qq:
                TCAgent.onEvent(mContext,"article_more_qq");
                if (mArticle != null && !TextUtils.isEmpty(mArticle.getShareUrl()))
                    QQApi.shareQQ((Activity) mContext, mArticle.getShareUrl(), null, mArticle.getTitle(),
                            TextUtils.isEmpty(mArticle.getDescribe()) ? "" : mArticle.getDescribe());
                break;
            case R.id.tv_dialog_main_list_menu_quen:
                TCAgent.onEvent(mContext,"article_more_qq1");
                if (mArticle != null && !TextUtils.isEmpty(mArticle.getShareUrl()))
                    QQApi.shareSpace((Activity) mContext, mArticle.getShareUrl(), null, mArticle.getTitle(),
                            TextUtils.isEmpty(mArticle.getDescribe()) ? "" : mArticle.getDescribe());
                break;
            case R.id.tv_dialog_main_list_menu_copy:
                TCAgent.onEvent(mContext,"article_more_copyurl");
                if (mArticle != null && !TextUtils.isEmpty(mArticle.getShareUrl())) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", mArticle.getShareUrl());
                    cm.setPrimaryClip(mClipData);
                    ContentManager.getInstance().addCopyItem(mArticle.getShareUrl());
                    T.showCustomToast("分享链接复制成功");
                }
                break;
            case R.id.tv_dialog_main_list_menu_love:
                TCAgent.onEvent(mContext,"article_more_collection");
                if (mArticle != null)
                    mListener.onItemLove(mArticle);
                break;
            case R.id.tv_dialog_main_list_menu_del:
                TCAgent.onEvent(mContext,"article_more_delete");
                if (mArticle != null)
                    mListener.onItemDel(mTabType, mTabType == BaseMainTabFragment.TAB_TYPE.UNREAD ? mArticle.getArticleId() : mArticle.getId());
            case R.id.tv_dialog_main_list_menu_cancel:
                break;
        }
        MainListMenuDialog.this.dismiss();
    }

    public interface OnBottomSelectDialogListener {
        void onItemDel(BaseMainTabFragment.TAB_TYPE tabType, String id);

        void onItemLove(Article article);
    }

}
