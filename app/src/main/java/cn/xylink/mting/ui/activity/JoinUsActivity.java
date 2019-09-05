package cn.xylink.mting.ui.activity;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import cn.xylink.mting.R;
import cn.xylink.mting.base.BaseActivity;

public class JoinUsActivity extends BaseActivity {

    @Override
    protected void preView() {
        setContentView(R.layout.activity_join_use);
        View img = findViewById(R.id.contactUsImg);
        img.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = img.getMeasuredWidth();
        ViewGroup.LayoutParams params = img.getLayoutParams();
        params.height = width;
        img.setLayoutParams(params);

        View backIcon = findViewById(R.id.joinUsBack);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        View saveButton = findViewById(R.id.joinUsSaveImgButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SavePhoto(JoinUsActivity.this).SaveBitmapFromView(findViewById(R.id.contactUsImg));
                    Toast.makeText(JoinUsActivity.this, "二维码已保存", Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex) {
                    Toast.makeText(JoinUsActivity.this, "保存失败:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitleBar() {

    }



    public class SavePhoto{
        //存调用该类的活动
        Context context;

        public SavePhoto(Context context) {
            this.context = context;
        }

        //保存文件的方法：
        public void SaveBitmapFromView(View view) throws ParseException {
            int w = view.getWidth();
            int h = view.getHeight();
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int left = view.getLeft();
            int top = view.getTop();

            //view.layout(0, 0, w, h);
            view.draw(c);
            // 缩小图片
            Matrix matrix = new Matrix();
            matrix.postScale(0.5f,0.5f); //长和宽放大缩小的比例
            bmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
            DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            saveBitmap(bmp,bmp.toString() + ".JPEG");
        }

        /*
         * 保存文件，文件名为当前日期
         */
        public void saveBitmap(Bitmap bitmap, String bitName){
            String fileName ;
            File file ;
            if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
                fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
            }else{  // Meizu 、Oppo
                Log.v("qwe","002");
                fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
            }
            file = new File(fileName);

            if(file.exists()){
                file.delete();
            }
            FileOutputStream out;
            try{
                out = new FileOutputStream(file);
                // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
                if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
                {
                    out.flush();
                    out.close();
                    // 插入图库
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);

                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();

            }
            // 发送广播，通知刷新图库的显示
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        }
    }
}
