package liuxiaodong.neusoft.edu.cn.wenda.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.os.Vibrator;

import com.bumptech.glide.Glide;

import java.io.File;
import java.math.BigDecimal;

import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;

/**
 * Created by DONG on 2017/2/11.
 */

public class SystemUtils {


    public static SystemUtils.NetWorkType getNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case 0:
                    return SystemUtils.NetWorkType.mobile;
                case 1:
                    return SystemUtils.NetWorkType.wifi;
            }
        }

        return SystemUtils.NetWorkType.none;
    }

    public enum NetWorkType {
        none,
        mobile,
        wifi;

        NetWorkType() {
        }
    }

    public static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(700);

    }

    /**
     * 清除 缓存  图片 + 语音
     */
    public static void cleanCache() {

        File recordDlDir = new File(MyUtils.getDiskDLCacheDir(APP.getInstance()));
        recordDlDir.delete();

        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(APP.getInstance()).clearDiskCache();
                    EventManager.postSticky(new Event(Event.CLEAN_CACHE_SUCCESS));
                }
            }).start();
        } else {
            Glide.get(APP.getInstance()).clearDiskCache();
            EventManager.postSticky(new Event(Event.CLEAN_CACHE_SUCCESS));
        }
    }


    public static String getCacheSize() {
        // 图片+ 语音

        String totalSize = null;
        try {
            String picsCache = getFormatSize(getFolderSize(new File(Glide.getPhotoCacheDir(APP.getInstance()).getPath())));
            long picsCacheSize = getFolderSize(new File(Glide.getPhotoCacheDir(APP.getInstance()).getPath()));
            long recordDlCacheSize = getFolderSize(new File(MyUtils.getDiskDLCacheDir(APP.getInstance())));

            totalSize = getFormatSize(picsCacheSize + recordDlCacheSize);

            Logger.i("Gilide缓存：" + picsCache);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalSize;
    }

    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    // 获取指定文件夹内所有文件大小的和
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
