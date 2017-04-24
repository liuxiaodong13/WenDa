package liuxiaodong.neusoft.edu.cn.wenda.other;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.bmob.v3.datatype.BmobFile;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.MyUtils;

/**
 * Created by DONG on 2017/3/26.
 */

public class RecordDownloader {
    private BmobFile record;


    public interface onDownloadListener {
        void onComplete(String filePath);
    }

    private onDownloadListener mListener;

    private String fileLocalPath;

    public void setDownloadListener(onDownloadListener listener) {
        mListener = listener;
    }

    public RecordDownloader(BmobFile record, onDownloadListener listener) {
        this.record = record;
        mListener = listener;
    }

    /**
     * 下载文件 返回
     */
    public void download() {
        //先判断文件是否存在 或存在直接返回
        final String diskCacheDir = MyUtils.getDiskDLCacheDir(APP.getInstance());
        if (isExist()) {
            if (mListener != null) {
                mListener.onComplete(fileLocalPath);
            }
            Logger.d("文件已存在！" +fileLocalPath);
            return;

        }

        //不存在先下载 再返回
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFromUrl(record.getFileUrl(), diskCacheDir, record.getFilename());

            }
        }).start();

    }

    private void downloadFromUrl(String url, String dir, String fileName) {

        try {
            File file = new File(dir, fileName);
            Logger.d("下载文件：" + file.getAbsolutePath());
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            InputStream in = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(in);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);


            byte[] buffer = new byte[1024];
            int size = -1;
            while ((size = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }

            fos.flush();
            fos.close();
            bis.close();
            connection.disconnect();
            if (mListener != null)
            {
                fileLocalPath = file.getAbsolutePath();
                mListener.onComplete(file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean isExist() {
        String diskCacheDir = MyUtils.getDiskDLCacheDir(APP.getInstance());
        if (record != null) {
            String filename = record.getFilename();
            Logger.i("下载目录:" + diskCacheDir);
            File cacheDirFile = new File(diskCacheDir);
            if (!cacheDirFile.exists()) {
                cacheDirFile.mkdir();
                return false;
            }
            File[] listFiles = cacheDirFile.listFiles();
            for (File f : listFiles) {
                if (f.isFile()) {
                    if (f.getName().equals(filename)) {
                        fileLocalPath = f.getAbsolutePath();
                        return true;

                    }
                }
            }

        }


        return false;
    }
//
//    static Collection<File> listFiles(File root) {
//        List<File> files = new ArrayList<File>();
//        listFiles(files, root);
//        return files;
//    }
//
//    static void listFiles(List<File> files, File dir) {
//        File[] listFiles = dir.listFiles();
//        for (File f : listFiles) {
//            if (f.isFile()) {
//                files.add(f);
//            } else if (f.isDirectory()) {
//                listFiles(files, f);
//            }
//        }
//    }
}
