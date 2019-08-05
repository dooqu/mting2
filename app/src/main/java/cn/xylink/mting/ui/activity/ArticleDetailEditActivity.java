package cn.xylink.mting.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.contract.EditArticleContact;
import cn.xylink.mting.presenter.EditArticlePresenter;

public class ArticleDetailEditActivity extends BasePresenterActivity implements EditArticleContact.ICreateView {

    @BindView(R.id.tv_include_title)
    TextView tvInclude;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.et_article_title)
    EditText etArticleTitle;
    @BindView(R.id.et_article_content)
    EditText etArticleContent;

    private String id;
    private String title;
    private String content;
    private EditArticlePresenter mEditArticlePresenter;

    @Override
    protected void preView() {
        setContentView(R.layout.activity_article_detail_edit);
    }

    @Override
    protected void initView() {
        mEditArticlePresenter = (EditArticlePresenter) createPresenter(EditArticlePresenter.class);
        mEditArticlePresenter.attachView(this);
    }

    @Override
    protected void initData() {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        title = extras.getString("title");
        content = extras.getString("content");
        etArticleTitle.setText(title);
        etArticleContent.setText(content);
    }

    @Override
    protected void initTitleBar() {
        tvInclude.setText("编辑文章");
        tvRight.setText("保存");
        tvRight.setTextColor(Color.parseColor("#488def"));
    }

    @OnClick({R.id.tv_right})
    void onSave(View v) {
        mEditArticlePresenter.onEditNote(id, etArticleTitle.getText().toString(), etArticleContent.getText().toString());
    }
    @OnClick({R.id.btn_left})
    void onExit(View v) {
        finish();
    }

    @Override
    public void onSaveSuccess() {
        finish();
        toastShort("保存成功");
    }

    @Override
    public void onSaveError() {
        toastShort("保存失败");
    }
}
