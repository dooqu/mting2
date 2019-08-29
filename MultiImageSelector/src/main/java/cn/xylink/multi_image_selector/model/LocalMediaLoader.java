package cn.xylink.multi_image_selector.model;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import cn.xylink.multi_image_selector.config.PictureMimeType;
import cn.xylink.multi_image_selector.bean.Folder;
import cn.xylink.multi_image_selector.bean.Image;

public class LocalMediaLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String DURATION = "duration";
    private static final String NOT_GIF = "!='image/gif'";
    private static final int AUDIO_DURATION = 500;// 过滤掉小于500毫秒的录音
    private int type = PictureMimeType.TYPE_IMAGE;
    private FragmentActivity activity;
    private boolean isGif;
    private long videoMaxS = 0;
    private long videoMinS = 0;

    private boolean hasFolderGened = false;

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_AUDIO = 3;

    // 媒体文件数据库字段
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            DURATION,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
    };


    public LocalMediaLoader(FragmentActivity _activity) {
        this.activity = _activity;
    }

    private LocalMediaLoadListener imageLoadListener;

    public void setImageLoadListener(LocalMediaLoadListener imageLoadListener) {
        this.imageLoadListener = imageLoadListener;
    }

    // 图片
    private static final String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";
//            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF;

    // 查询条件(音视频)
    private static String getSelectionArgsForSingleMediaCondition(String time_condition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + time_condition;
    }

    // 全部模式下条件
    private static String getSelectionArgsForAllMediaCondition(String time_condition, boolean isGif) {
        String condition = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
//                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + time_condition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        return condition;
    }

    /**
     * 获取指定类型的文件
     *
     * @param mediaType
     * @return
     */
    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }


    // 获取图片or视频
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };


    /**
     * 获取视频(最长或最小时间)
     *
     * @param exMaxLimit
     * @param exMinLimit
     * @return
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = videoMaxS == 0 ? Long.MAX_VALUE : videoMaxS;
        if (exMaxLimit != 0) maxS = Math.min(maxS, exMaxLimit);

        return String.format(Locale.CHINA, "%d <%s duration and duration <= %d",
                Math.max(exMinLimit, videoMinS),
                Math.max(exMinLimit, videoMinS) == 0 ? "" : "=",
                maxS);
    }


    /**
     * 创建相应文件夹
     *
     * @param path
     * @param imageFolders
     * @return
     */
    private Folder getImageFolder(String path, List<Folder> imageFolders) {
        if(imageFolders != null) {
            for (Folder folder : imageFolders) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }


    public LoaderManager.LoaderCallbacks<Cursor> loadAllMedia(final LocalMediaLoadListener imageLoadListener) {
        this.imageLoadListener = imageLoadListener;
        return mLoaderCallback;
    }


    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            switch (id) {
                case TYPE_ALL:
                    String all_condition = getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), isGif);
                    cursorLoader = new CursorLoader(
                            activity, QUERY_URI,
                            PROJECTION, all_condition,
                            SELECTION_ALL_ARGS, ORDER_BY);
                    break;
                case TYPE_IMAGE:
                    // 只获取图片
                    String[] MEDIA_TYPE_IMAGE = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    cursorLoader = new CursorLoader(
                            activity, QUERY_URI,
                            PROJECTION, isGif ? SELECTION : SELECTION_NOT_GIF, MEDIA_TYPE_IMAGE
                            , ORDER_BY);
                    break;
                case TYPE_VIDEO:
                    // 只获取视频
                    String video_condition = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, 0));
                    String[] MEDIA_TYPE_VIDEO = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    cursorLoader = new CursorLoader(
                            activity, QUERY_URI, PROJECTION, video_condition, MEDIA_TYPE_VIDEO
                            , ORDER_BY);
                    break;
                case TYPE_AUDIO:
                    String audio_condition = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, AUDIO_DURATION));
                    String[] MEDIA_TYPE_AUDIO = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
                    cursorLoader = new CursorLoader(
                            activity, QUERY_URI, PROJECTION, audio_condition, MEDIA_TYPE_AUDIO
                            , ORDER_BY);
                    break;
            }
            return cursorLoader;
        }

        private boolean fileExist(String path) {
            if (!TextUtils.isEmpty(path)) {
                return new File(path).exists();
            }
            return false;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            /*if (data != null) {
                if (data.getCount() > 0) {
                    List<Image> images = new ArrayList<>();
                    data.moveToFirst();
                    do{
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        if(!fileExist(path)){continue;}
                        Image image = null;
                        if (!TextUtils.isEmpty(name)) {
                            image = new Image(path, name, dateTime);
                            image.setWidth(data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6])));
                            image.setHeight(data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7])));
//                            image.setPictureType(data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3])));
//                            int duration = data.getInt(data.getColumnIndexOrThrow("duration"));
//                            image.setDuration(Integer.toString(duration));
                            images.add(image);
                        }

                        if( !hasFolderGened ) {
                            // get all folder data
                            File folderFile = new File(path).getParentFile();
                            if(folderFile != null && folderFile.exists()){
                                String fp = folderFile.getAbsolutePath();
                                Folder f = getFolderByPath(fp);
                                if(f == null){
                                    Folder folder = new Folder();
                                    folder.name = folderFile.getName();
                                    folder.path = fp;
                                    folder.cover = image;
                                    List<Image> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folder.images = imageList;
                                    mResultFolder.add(folder);
                                }else {
                                    f.images.add(image);
                                }
                            }
                        }

                    }while(data.moveToNext());
                    mImageAdapter.setData(images);
//                    if(resultList != null && resultList.size()>0){
//                        mImageAdapter.setDefaultSelected(resultList);
//                    }
                    if(!hasFolderGened) {
                        mFolderAdapter.setData(mResultFolder);
                        hasFolderGened = true;
                    }
                }
            }*/

            ArrayList<Folder> mResultFolder = new ArrayList<>();
            ArrayList<Folder> imageFolders = new ArrayList<>();
//                LocalMediaFolder allImageFolder = new LocalMediaFolder();
            List<Image> latelyImages = new ArrayList<>();
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString
                                (data.getColumnIndexOrThrow(PROJECTION[1]));
                        if (!fileExist(path)) {
                            continue;
                        }
                        String pictureType = data.getString
                                (data.getColumnIndexOrThrow(PROJECTION[2]));

                        int w = data.getInt
                                (data.getColumnIndexOrThrow(PROJECTION[3]));

                        int h = data.getInt
                                (data.getColumnIndexOrThrow(PROJECTION[4]));

                        int duration = data.getInt
                                (data.getColumnIndexOrThrow(PROJECTION[5]));
                        String name = data.getString(data.getColumnIndexOrThrow(PROJECTION[6]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(PROJECTION[7]));

                        Image image = new Image(path, name, dateTime);
                        image.setWidth(w);
                        image.setHeight(h);
                        image.setDuration(Integer.toString(duration));
                        image.setPictureType(pictureType);

                        if (!hasFolderGened) {
                            // get all folder data
                            File folderFile = new File(path).getParentFile();
                            if (folderFile != null && folderFile.exists()) {
                                String fp = folderFile.getAbsolutePath();
                                Folder f = getImageFolder(fp, mResultFolder);
                                if (f == null) {
                                    Folder folder = new Folder();
                                    folder.name = folderFile.getName();
                                    folder.path = fp;
                                    folder.cover = image;
                                    List<Image> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folder.images = imageList;
                                    mResultFolder.add(folder);
                                } else {
                                    f.getImages().add(image);
                                }
                            }
                        }

                        latelyImages.add(image);
                    } while (data.moveToNext());

                    imageLoadListener.loadCompleteImage(latelyImages);
                    if (!hasFolderGened) {
                        imageLoadListener.loadComplete(mResultFolder);
                        hasFolderGened = true;
                    }

                } else {
                    // 如果没有相册
                    imageLoadListener.loadComplete(imageFolders);
//                    imageLoadListener.loadCompleteImage(latelyImages);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    /**
     * 文件夹数量进行排序
     *
     * @param imageFolders
     */
    private void sortFolder(List<Folder> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<Folder>() {
            @Override
            public int compare(Folder lhs, Folder rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }


    public interface LocalMediaLoadListener {
        void loadComplete(ArrayList<Folder> folders);

        void loadCompleteImage(List<Image> images);
    }
}
