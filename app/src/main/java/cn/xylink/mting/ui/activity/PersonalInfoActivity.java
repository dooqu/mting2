package cn.xylink.mting.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.apaches.commons.codec.digest.Md5Crypt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseRequest;
import cn.xylink.mting.base.BaseResponse;
import cn.xylink.mting.bean.UploadHeadImgInfo;
import cn.xylink.mting.bean.UserInfo;
import cn.xylink.mting.contract.UploadHeadImgContact;
import cn.xylink.mting.model.UploadHeadImgRequest;
import cn.xylink.mting.presenter.UploadHeadImgPresenter;
import cn.xylink.mting.ui.dialog.BottomSelectDialog;
import cn.xylink.mting.utils.ContentManager;
import cn.xylink.mting.utils.ImageUtils;
import cn.xylink.mting.utils.L;
import cn.xylink.mting.utils.MD5;

public class PersonalInfoActivity extends BasePresenterActivity implements TakePhoto.TakeResultListener, InvokeListener, UploadHeadImgContact.IUploadHeadImgView,BottomSelectDialog.OnBottomSelectDialogListener {


    @BindView(R.id.tv_include_title)
    TextView tvTitle;
    @BindView(R.id.iv_my_head)
    ImageView ivhead;
    @BindView(R.id.tv_nickName)
    EditText tvNickName;
    @BindView(R.id.tv_sex)
    TextView tvSex;

    private TakePhoto takePhoto;

    private InvokeParam invokeParam;

    private UploadHeadImgPresenter headImgPresenter;

    private String headImgUrl;

    UserInfo user;

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
//        takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(), true);
        return takePhoto;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void preView() {
        setContentView(R.layout.activity_personal_info);
    }

    @Override
    protected void initView() {
        tvTitle.setText("个人信息");
        tvNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {
        headImgPresenter = (UploadHeadImgPresenter) createPresenter(UploadHeadImgPresenter.class);
        headImgPresenter.attachView(this);

        UserInfo user = ContentManager.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(user.getHeadImg()))
            ImageUtils.get().loadCircle(ivhead, headImgUrl);
        if(!TextUtils.isEmpty(user.getNickName()))
            tvNickName.setText(user.getNickName());
    }

    @Override
    protected void initTitleBar() {

    }

    @OnClick({R.id.btn_left,
            R.id.iv_my_head,
            R.id.iv_arrow_1,
            R.id.tv_sex,
            R.id.iv_arrow_2,
            R.id.iv_arrow_3,
            R.id.tv_nickName
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.iv_arrow_1:
            case R.id.iv_my_head:
                showPicSelectDialog();
                break;
            case R.id.iv_arrow_2:
            case R.id.tv_sex:
                break;
            case R.id.iv_arrow_3:
            case R.id.tv_nickName:
                break;

        }
    }

    private void showPicSelectDialog() {
        BottomSelectDialog dialog = new BottomSelectDialog(this);
        dialog.setData(getResources().getString(R.string.take_pic), getResources().getString(R.string.select_album), this);
        dialog.show();
    }


    private void cropPic(String result) {
        //第三方裁剪库
        File file = new File(this.getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        Uri uri = Uri.fromFile(file);
        Uri uri1 = Uri.parse("file://" + result);
        L.v(uri1);
        UCrop uCrop = UCrop.of(uri1, uri);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setHideBottomControls(true);
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        options.setFreeStyleCropEnabled(false);
        uCrop.withOptions(options);
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(224, 224);
        uCrop.start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            String picPath = resultUri.toString().replace("file://", "");
            doUploadImg(new File(picPath));
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }


    private void doUploadImg(File file) {
        Map<String, String> data = new HashMap<>();
        UploadHeadImgRequest request = new UploadHeadImgRequest();
        data.put("token", request.token);
        data.put("timestamp", request.timestamp + "");
        String sign = MD5.md5crypt(BaseRequest.desKey + request.token + request.timestamp);
        data.put("sign",sign);
        headImgPresenter.uploadHeadImg(data, file);
    }

    private File picFile;

    @Override
    public void takeSuccess(TResult result) {
        String path;
        if (picFile != null) {
            path = picFile.toString();
        } else {
            path = result.getImage().getOriginalPath();
        }
        L.v("===============TResult=" + path);
        if (!TextUtils.isEmpty(path)) {
//            ImageUtils.get().loadCircle(mHeadImg, path);
//            uploadHeadImg(path);
            cropPic(path);
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(PersonalInfoActivity.this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onSuccessUploadHeadImg(BaseResponse<UserInfo> baseResponse) {
        headImgUrl = baseResponse.data.getHeadImg();

        if (TextUtils.isEmpty(headImgUrl)) {
            toastShort("上传头像失败");
        } else {
            ImageUtils.get().loadCircle(ivhead, headImgUrl);
            ContentManager.getInstance().setUserInfo(baseResponse.data);
        }
    }

    @Override
    public void onErrorUploadHeadImg(int code, String errorMsg) {
        if (null != ContentManager.getInstance().getUserInfo()) {
            if (TextUtils.isEmpty(ContentManager.getInstance().getUserInfo().getHeadImg())) {
                ImageUtils.get().loadCircle(ivhead, ContentManager.getInstance().getUserInfo().getHeadImg());
            }
        } else {
            ivhead.setImageResource(R.mipmap.personal_head);
        }
    }


    @Override
    public void onFirstClick() {
        picFile = new File(this.getExternalCacheDir(), System.currentTimeMillis() + ".png");
        Uri uri = Uri.fromFile(picFile);
//                CropOptions cropOptions = new CropOptions.Builder().setOutputX(224).setOutputX(224).setWithOwnCrop(true).create();
//                takePhoto.onPickFromCaptureWithCrop(uri, cropOptions);
        takePhoto.onPickFromCapture(uri);
    }

    @Override
    public void onSecondClick() {
//                File file = new File(getActivity().getExternalCacheDir(), System.currentTimeMillis() + ".png");
//                Uri uri = Uri.fromFile(file);
//                CropOptions cropOptions = new CropOptions.Builder().setOutputX(224).setOutputX(224).setWithOwnCrop(true).create();
//                takePhoto.onPickFromGalleryWithCrop(uri, cropOptions);
        picFile = null;
        takePhoto.onPickFromGallery();
    }

}
