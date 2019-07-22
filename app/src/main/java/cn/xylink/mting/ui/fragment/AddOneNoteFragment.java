package cn.xylink.mting.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.BreakIterator;
import java.util.regex.Pattern;

import butterknife.BindView;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.Article;
import cn.xylink.mting.contract.InputCreateContact;
import cn.xylink.mting.event.AddArticleHomeEvent;
import cn.xylink.mting.event.AddUnreadEvent;
import cn.xylink.mting.event.OneArticleEvent;
import cn.xylink.mting.model.InputCreateRequest;
import cn.xylink.mting.presenter.InputCreatePresenter;
import cn.xylink.mting.utils.L;

public class AddOneNoteFragment extends BasePresenterFragment implements InputCreateContact.ICreateView {

    private final int ET_TITLE_LENTGH = 30;
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
        return R.layout.fragment_add_aricle_1;
    }

    @Override
    protected void initView(View view) {
        EventBus.getDefault().register(this);
        inputCreatePresenter = (InputCreatePresenter) createPresenter(InputCreatePresenter.class);
        inputCreatePresenter.attachView(this);

        InputFilter[] filters = {new InputFilter.LengthFilter(ET_TITLE_LENTGH)};
        etTitle.setFilters(filters);

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    EventBus.getDefault().post(new AddArticleHomeEvent(1));
                } else {
                    EventBus.getDefault().post(new AddArticleHomeEvent(0));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OneArticleEvent event) {

        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (event.type == OneArticleEvent.TYPE_BACK) {
            L.v("title", content);
            if (TextUtils.isEmpty(content)) {
                getActivity().finish();
                return;
            }
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "请输入文章正文", Toast.LENGTH_SHORT).show();
            return;
        }
        L.v("title", title);
        if (TextUtils.isEmpty(title)) {
            BreakIterator iterator = BreakIterator.getSentenceInstance();
            iterator.setText(content);
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
                title = content.substring(start, end);
                L.v(title);
                break;
            }
            if (title.length() > 30) {
                title = title.substring(0, 30);
            }
        }
        inputCreateRequset(title, etContent.getText().toString());

    }

    public void inputCreateRequset(String title, String content) {
        InputCreateRequest requset = new InputCreateRequest();
        requset.setContent(content);
        requset.setTitle(title);
        requset.doSign();
        inputCreatePresenter.onCreateNote(requset);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        L.v("isVisibleToUser", isVisibleToUser);
        if (!isVisibleToUser) {
            EventBus.getDefault().post(new AddArticleHomeEvent(0));
        } else {
            if (!TextUtils.isEmpty(etContent.getText())) {

                EventBus.getDefault().post(new AddArticleHomeEvent(1));
            }
        }
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void onCreateSuccess(BaseResponse<Article> response) {
        L.v("response.msg", response.message);
        String json = new Gson().toJson(response.data);

        AddUnreadEvent event = new AddUnreadEvent();
        event.setArticleID(response.data.getArticleId());
        EventBus.getDefault().post(event);
        getActivity().finish();
    }

    @Override
    public void onCreateError(int code, String errorMsg) {
        L.v("code", code, "errorMsg", errorMsg);

    }
}
