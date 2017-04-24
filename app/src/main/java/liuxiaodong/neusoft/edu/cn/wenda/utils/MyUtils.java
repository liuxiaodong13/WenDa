package liuxiaodong.neusoft.edu.cn.wenda.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by DONG on 2016/10/21.
 */

public class MyUtils {
    //int screenWidth = MyUtils.getScreenMetrics(this).widthPixels;
    //int screenHeight = MyUtils.getScreenMetrics(this).heightPixels;
    public static DisplayMetrics getScreenMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }


    /**
     * getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache0
     * getCacheDir() = /data/data/com.my.app/cache
     * @param context
     * @return
     */
    public static String getDiskDLCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath() + "/download";
        } else {
            cachePath = context.getCacheDir().getPath() + "/download";

        }
        Logger.i("下载缓存目录:" + cachePath);
        return cachePath;
    }

    public static String getAudioFilesDir(Context context) {
        String path = null;
        // /mnt/sdcard/Android/data/com.my.app/files/audio

        //完成录制并上传成功 需要删除文件

        //若保存为草稿 则不需要删除文件
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            path = context.getExternalFilesDir(DefaultValue.AUDIO_DIR_NAME).getPath();
            Logger.i("使用sd卡:" + path);
        } else {
            //没有储存卡就用系统空间
            // /data/data/com.my.app/files/audio
            path = context.getFilesDir().getPath() + "/" + DefaultValue.AUDIO_DIR_NAME;
            Logger.i("使用系统储存空间:" + path);
        }
        return path;
    }

    public static String getDownloadDir(Context context) {
        String dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            dir = context.getExternalFilesDir(DefaultValue.AUDIO_DIR_NAME).getPath();
        } else {
            //没有储存卡就用系统空间
            // /data/data/com.my.app/files
            dir = context.getFilesDir().getPath();
        }
        return null;

    }
}
