package cn.xylink.multi_image_selector.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

	/**
	 * bitmap转为base64 
	 * @param bitmap 
	 * @return 
	 */  
	public static String bitmapToBase64(Bitmap bitmap) {  
	  
	    String result = null;  
	    ByteArrayOutputStream baos = null;  
	    try {  
	        if (bitmap != null) {  
	            baos = new ByteArrayOutputStream();  
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	  
	            baos.flush();  
	            baos.close();  
	  
	            byte[] bitmapBytes = baos.toByteArray();  
	            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);  
	            bitmapBytes = new byte[1];
	            bitmapBytes = null;
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        try {  
	            if (baos != null) {  
	                baos.flush();  
	                baos.close();  
	            }
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    return result;  
	}  
	  
	/** 
	 * base64转为bitmap 
	 * @param base64Data 
	 * @return 
	 */  
	public static Bitmap base64ToBitmap(String base64Data) {  
		String[] safe = base64Data.split("=");
	    byte[] bytes = Base64.decode(safe[0], Base64.NO_PADDING); 
	    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	    safe = null;
	    bytes = new byte[1];
	    bytes = null;
	    return  bm; 
	}  
	
	
	/**
	 * 
	* @Description:获取图片bitmap
	* @param @param filePath
	* @param @return  
	* @return Bitmap  
	* @throws
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BitmapFactory.decodeStream(fis, null, options);
		try {
			fis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Calculate inSampleSize 最大　２５６０*１６００
		options.inSampleSize = calculateInSampleSize(options, 2280, 1480);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		ExifInterface exifInterface = null;
		Matrix mtx = new Matrix();
		try {
			exifInterface = new ExifInterface(filePath);
			int tag = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			switch (tag) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				mtx.postRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				mtx.postRotate(180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				mtx.postRotate(270);
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmap == null)
			return bitmap;
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), mtx, true);
		bitmap = null;
		mtx = null;
		return bitmap2;
	}
	
		/**
		 * 
		* @Description: 图片比例
		* @param @param options
		* @param @param reqWidth
		* @param @param reqHeight
		* @param @return  
		* @return int  
		* @throws
		 */
		public static int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				final int heightRatio = Math.round((float) height
						/ (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
			return inSampleSize;
		}
		

		
		/**
		* @Description:  获取磁盘空间大小
		* @param @return  
		* @return long  
		* @throws
		 */
		@SuppressLint("NewApi")
		public static long getAvailaleSize() {
			File path = Environment.getExternalStorageDirectory();
			StatFs statFs = new StatFs(path.getPath());
			long blockSize = statFs.getBlockSizeLong();
			long availableBlocks = statFs.getAvailableBlocksLong();
			long size = (availableBlocks * blockSize) / 1024 / 1024;
			return size;
		}
		

		@SuppressLint("SdCardPath")
		public static String getImagePathFromUri(Uri fileUrl, Context c) {
			String fileName = null;
			Uri filePathUri = fileUrl;
			if (fileUrl != null) {
				if (fileUrl.getScheme().toString().compareTo("content") == 0) {
					// content:// uri
					Cursor cursor = c.getContentResolver().query(fileUrl, null,
							null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						int column_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						fileName = cursor.getString(column_index); // 图片名称

						// Android 4.1
						// /storage/sdcard0
						if (!fileName.startsWith("/storage")
								&& !fileName.startsWith("/mnt")) {
							//拼接 /mnt+fileName
							fileName = "/mnt" + fileName;
						}
						cursor.close();
					}
				} else if (fileUrl.getScheme().compareTo("file") == 0) // 以file 为开头时
				{
					fileName = filePathUri.toString();// 图片名称
					fileName = filePathUri.toString().replace("file://", "");
					int index = fileName.indexOf("/sdcard");
					fileName = index == -1 ? fileName : fileName.substring(index);

					if (!fileName.startsWith("/mnt")) {
						//图片路径
						fileName += "/mnt";
					}
				}
			}
			return fileName;
		}
		
		/**
		 * 根据Uri获取图片的路径
		 * 
		 * @param uri
		 * @return
		 */
		public  static String getPathByUri(Activity act,Uri uri) {
			String path = null;
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor actualimagecursor = act.managedQuery(uri, proj, null, null, null);
			if (actualimagecursor == null) {
				path = getImagePathFromUri(uri, act);
				return path;
			}
			int actual_image_column_index = actualimagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			path = actualimagecursor.getString(actual_image_column_index);
			return path;
		}
		
		/**
		 * 通知相册更新照片
		* @param @param context
		* @param @param filePath  
		* @return void  
		* @throws
		 */
		public static void sendBoradcaseMediaFile(Context context,String filePath){
		
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.fromFile(new File(filePath));
			intent.setData(uri);
			context.sendBroadcast(intent);
		}
		
		
		
		@SuppressLint("SimpleDateFormat")
		public static String getPhotoFileName(String id) {
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat("'" + id
					+ "'_yyyyMMdd_HHmmss");
			return dateFormat.format(date) + ".jpeg";

		}
		
		@SuppressLint("SimpleDateFormat")
		public static String getPhotoFileName()
		{
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"'IMG'_yyyyMMdd_HHmmss");
			return dateFormat.format(date) + ".jpg";

		}
		
		
		/**
		 * 释放Bitmap图片资源
		 * @param bm
		 */
		public static void recycledBitmap(Bitmap bm) {
			if (null != bm) {
				if (!bm.isRecycled()) {
					bm.recycle();
					bm = null;
				}
			}
		}

		/**
		 * 释放ImageView
		 * @param view
		 */
		public static void recycledImage(ImageView view) {
			if (null == view)
				return;
			BitmapDrawable drawable2 = (BitmapDrawable) view.getDrawable();
			if (null != drawable2) {
				Bitmap bm = drawable2.getBitmap();
				if (null != bm && !bm.isRecycled()) {
					bm.recycle();
					// bm = null;
				}
			}
		}
		
		

	
}
