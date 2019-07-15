package cn.xylink.mting.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseFragment;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.contract.InputCreateContact;
import cn.xylink.mting.event.OneArticleEvent;
import cn.xylink.mting.model.InputCreateRequest;
import cn.xylink.mting.presenter.InputCreatePresenter;
import cn.xylink.mting.utils.L;

public class AddOneNoteFragment extends BasePresenterFragment implements InputCreateContact.ICreateView {

    @BindView(R.id.et_article_title)
    EditText etTitle;
    @BindView(R.id.et_article_content)
    EditText etContent;

    private InputCreatePresenter inputCreatePresenter;


    public static AddOneNoteFragment newInstance(Bundle args) {
        AddOneNoteFragment fragment = new AddOneNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutViewId() {
        return  R.layout.fragment_add_aricle_1;
    }

    @Override
    protected void initView(View view) {
        EventBus.getDefault().register(this);
        inputCreatePresenter = (InputCreatePresenter) createPresenter(InputCreatePresenter.class);
        inputCreatePresenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {

    }

    public static final String sRegEx = "[`~!@#$%^&*()+=\\-\\s*|\t|\r|\n|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OneArticleEvent event) {
        L.v("event 1");
        if(TextUtils.isEmpty(etContent.getText().toString()))
            return;
        String title = etTitle.getText().toString();

        if (TextUtils.isEmpty(title)) {
            Pattern p = Pattern.compile(sRegEx);
            Matcher matcher = p.matcher(etContent.getText().toString());
            while (matcher.find()) {
                title = matcher.group();
                Toast.makeText(this.getContext(), title, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        inputCreateRequset(title,etContent.getText().toString());

    }

    public void inputCreateRequset(String title,String content)
    {
        InputCreateRequest requset = new InputCreateRequest();
        requset.setContent(content);
        requset.setTitle(title);
        requset.doSign();
        inputCreatePresenter.onCreateNote(requset);

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onCreateSuccess(BaseResponse<Article> requset) {
      String json =  new Gson().toJson(requset.data);
      L.v(json);
    }

    @Override
    public void onCreateError(int code, String errorMsg) {

    }
}
