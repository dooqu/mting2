package cn.xylink.mting.ui.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.model.data.RemoteUrl;
import cn.xylink.mting.openapi.QQApi;
import cn.xylink.mting.openapi.WXapi;
import cn.xylink.mting.utils.T;

/*
 *应用分享
 *
 * -----------------------------------------------------------------
 * 2019/7/23 17:12 : Create ShareAppDialog.java (JoDragon);
 * -----------------------------------------------------------------
 */
public class ShareAppDialog extends BaseDimDialog {

    @BindView(R.id.tv_dialog_share_title)
    TextView mTitleTextView;
    @BindView(R.id.tv_dialog_main_list_menu_love)
    TextView mLoveTextView;
    @BindView(R.id.tv_dialog_main_list_menu_del)
    TextView mDelTextView;
    private String url = RemoteUrl.getShareUrl();
    private String title = "轩辕听";
    private String content = "帮你读文章的软件，读朋友圈、读新闻的APP";

    public ShareAppDialog(Context context) {
        super(context);
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.share_animation);
    }

    @Override
    protected View getLayout() {
        return View.inflate(mContext, R.layout.dialog_main_list_menu, null);
    }

    @Override
    public void show() {
        super.show();
        mTitleTextView.setText("分享");
        mLoveTextView.setVisibility(View.INVISIBLE);
        mDelTextView.setVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.v_dialog_main_list_menu_out, R.id.tv_dialog_main_list_menu_wx, R.id.tv_dialog_main_list_menu_wxp,
            R.id.tv_dialog_main_list_menu_qq, R.id.tv_dialog_main_list_menu_quen, R.id.tv_dialog_main_list_menu_copy,
            R.id.tv_dialog_main_list_menu_love, R.id.tv_dialog_main_list_menu_del, R.id.tv_dialog_main_list_menu_cancel})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_dialog_main_list_menu_out:
                break;
            case R.id.tv_dialog_main_list_menu_wx:
                WXapi.shareWx((Activity) mContext, url, null, title, content);
                break;
            case R.id.tv_dialog_main_list_menu_wxp:
                WXapi.sharePyq((Activity) mContext, url, null, title, content);
                break;
            case R.id.tv_dialog_main_list_menu_qq:
                QQApi.shareQQ((Activity) mContext, url, null, title, content);
                break;
            case R.id.tv_dialog_main_list_menu_quen:
                QQApi.shareSpace((Activity) mContext, url, null, title, content);
                break;
            case R.id.tv_dialog_main_list_menu_copy:
                url = "我正在使用【轩辕听】：一款帮你读文章的软件，读朋友圈、读新闻的APP    " + url;
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", url);
                cm.setPrimaryClip(mClipData);
                T.showCustomToast("分享链接复制成功");
                break;

        }
        ShareAppDialog.this.dismiss();
    }


}